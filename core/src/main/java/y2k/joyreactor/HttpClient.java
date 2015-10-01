package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by y2k on 9/29/15.
 */
public class HttpClient {

    static final CookieStorage sCookies = new CookieStorage();

    public Observable<Document> getDocumentAsync(String url) {
        return ObservableUtils.create(() -> getDocument(url));
    }

    public Document getDocument(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        InputStream stream = null;
        try {
            sCookies.attach(conn);
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
            sCookies.detach(conn);
            stream = getInputStream(conn);
            return Jsoup.parse(stream, "utf-8", url);
        } finally {
            if (stream != null) stream.close();
        }
    }

    private InputStream getInputStream(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() < 300 ? conn.getInputStream() : conn.getErrorStream();
    }

    private static void showInformation(HttpURLConnection conn) {
        System.out.println("=| INFORMATION |=======================");
        System.out.println("URL: " + conn.getURL());
        System.out.println("METHOD: " + conn.getRequestMethod() + "\n");
        Map<String, List<String>> headers = conn.getHeaderFields();
        for (String key : headers.keySet()) {
            for (String value : headers.get(key))
                System.out.println(key + " = " + value);
        }
        System.out.println("=| INFORMATION |=======================");
    }

    public Form beginForm() {
        return new Form();
    }

    public class Form {

        Map<String, String> form = new HashMap<>();

        public Form put(String key, String value) {
            form.put(key, value);
            return this;
        }

        public Document send(String url) throws Exception {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko");
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            sCookies.attach(conn);
            conn.getOutputStream().write(serializeForm());

            showInformation(conn);
            sCookies.detach(conn);

            // TODO:
            InputStream stream = null;
            try {
                stream = getInputStream(conn);
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
            System.out.println(buffer);
            return buffer.toString().getBytes();
        }
    }

    static class CookieStorage {

        public void attach(HttpURLConnection connection) {
            // TODO:
        }

        public void detach(HttpURLConnection connection) {
            // TODO:
        }
    }
}