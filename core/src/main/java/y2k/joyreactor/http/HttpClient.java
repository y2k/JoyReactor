package y2k.joyreactor.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;
import rx.functions.Action2;
import y2k.joyreactor.common.IoUtils;
import y2k.joyreactor.common.ObjectUtils;
import y2k.joyreactor.common.ObservableUtils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by y2k on 9/29/15.
 */
public class HttpClient {

    private static HttpClient sInstance = new HttpClient();

    private static CookieStorage sCookies = new CookieStorage();

    protected HttpClient() {
    }

    public static void setInstance(HttpClient instance) {
        sInstance = instance;
    }

    public static HttpClient getInstance() {
        return sInstance;
    }

    public void downloadToFile(String url, File file, Action2<Integer, Integer> callback) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            in = connection.getInputStream();
            out = new FileOutputStream(file);

            byte[] buf = new byte[4 * 1024];
            int count, transfer = 0;
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);

                if (callback != null) {
                    transfer += count;
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    callback.call(transfer, connection.getContentLength());
                }
            }

        } finally {
            IoUtils.close(in, out);
        }
    }

    public String getText(String url) throws IOException {
        InputStream stream = null;
        try {
            HttpURLConnection conn = createConnection(url);
            stream = getInputStream(conn);
            sCookies.grab(conn);

            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                buffer.append(line).append("\n");
            return buffer.toString();
        } finally {
            if (stream != null) stream.close();
        }
    }

    public Observable<Document> getDocumentAsync(String url) {
        return ObservableUtils.create(() -> getDocument(url));
    }

    public Document getDocument(String url) throws IOException {
        InputStream stream = null;
        try {
            HttpURLConnection conn = createConnection(url);
            stream = getInputStream(conn);
            sCookies.grab(conn);
            return Jsoup.parse(stream, "utf-8", url);
        } finally {
            if (stream != null) stream.close();
        }
    }

    private InputStream getInputStream(HttpURLConnection connection) throws IOException {
        String redirect = connection.getHeaderField("Location");
        if (redirect != null) {
            connection.disconnect();
            connection = createConnection(redirect);
        }

        InputStream stream = connection.getResponseCode() < 300 ? connection.getInputStream() : connection.getErrorStream();
        return "gzip".equals(connection.getContentEncoding()) ? new GZIPInputStream(stream) : stream;
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection conn;
        conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
        sCookies.attach(conn);
        return conn;
    }

    public Form beginForm() {
        return new Form();
    }

    public void clearCookies() {
        sCookies.clear();
    }

    public class Form {

        Map<String, String> form = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        public Form put(String key, String value) {
            form.put(key, value);
            return this;
        }

        public Form putHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Document send(String url) throws Exception {
            HttpURLConnection connection = createConnection(url);
            connection.setRequestMethod("POST");
            connection.setInstanceFollowRedirects(false);
            connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            for (String name : headers.keySet())
                connection.addRequestProperty(name, headers.get(name));
            connection.getOutputStream().write(serializeForm());

            // TODO:
            InputStream stream = null;
            try {
                stream = getInputStream(connection);
                sCookies.grab(connection);
                return Jsoup.parse(stream, "utf-8", url);
            } finally {
                if (stream != null) stream.close();
            }
        }

        private byte[] serializeForm() throws UnsupportedEncodingException {
            StringBuilder buffer = new StringBuilder();
            for (String key : form.keySet()) {
                buffer.append(key);
                buffer.append("=");
                buffer.append(URLEncoder.encode(form.get(key), "UTF-8"));
                buffer.append("&");
            }
            buffer.replace(buffer.length() - 1, buffer.length(), "");
            return buffer.toString().getBytes();
        }
    }
}