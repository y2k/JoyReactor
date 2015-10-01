package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

    public Observable<Document> getDocumentAsync(String url) {
        return ObservableUtils.create(() -> getDocument(url));
    }

    public Document getDocument(String url) throws IOException {
        InputStream stream = null;
        try {
            HttpURLConnection conn = createConnection(url);
            stream = getInputStream(conn);
            sCookies.detach(conn);
            return Jsoup.parse(stream, "utf-8", url);
        } finally {
            if (stream != null) stream.close();
        }
    }

    private InputStream getInputStream(HttpURLConnection conn) throws IOException {
        String redirect = conn.getHeaderField("Location");
        if (redirect != null) {
            conn.disconnect();
            conn = createConnection(redirect);
        }
        return conn.getResponseCode() < 300 ? conn.getInputStream() : conn.getErrorStream();
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

    public class Form {

        Map<String, String> form = new HashMap<>();

        public Form put(String key, String value) {
            form.put(key, value);
            return this;
        }

        public Document send(String url) throws Exception {
            HttpURLConnection conn = createConnection(url);
            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(false);
            conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.getOutputStream().write(serializeForm());

            // TODO:
            InputStream stream = null;
            try {
                stream = getInputStream(conn);
                sCookies.detach(conn);
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

        public void detach(HttpURLConnection connection) {
            List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
            if (cookies == null || cookies.isEmpty()) return;

            for (String c : cookies) {
                Matcher m = COOKIE_PATTERN.matcher(c);
                if (!m.find()) throw new IllegalStateException(c);
                storage.put(m.group(1), m.group(2));
            }
        }
    }
}