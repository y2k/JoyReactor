package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/29/15.
 */
public class HttpClient {

    static final CookieStorage sCookies = new CookieStorage();

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
        return connection.getResponseCode() < 300 ? connection.getInputStream() : connection.getErrorStream();
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        HttpURLConnection conn;
        conn = (HttpURLConnection) new URL(url).openConnection();
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

    static class CookieStorage {

        private static final Pattern COOKIE_PATTERN = Pattern.compile("(.+?)=([^;]+)");
        private Map<String, String> storage = new HashMap<>();

        public void attach(HttpURLConnection connection) {
            if (storage.isEmpty()) return;

            StringBuilder cookie = new StringBuilder();
            for (String key : storage.keySet())
                cookie.append(key).append("=").append(storage.get(key)).append("; ");
            connection.addRequestProperty("Cookie", cookie.toString());
        }

        public void grab(HttpURLConnection connection) {
            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            if (cookies == null || cookies.isEmpty()) return;

            for (String c : cookies) {
                Matcher m = COOKIE_PATTERN.matcher(c);
                if (!m.find()) throw new IllegalStateException(c);
                storage.put(m.group(1), m.group(2));
            }
        }

        public void clear() {
            storage.clear();
        }
    }
}