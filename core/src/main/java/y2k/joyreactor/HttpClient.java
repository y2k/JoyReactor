package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by y2k on 9/29/15.
 */
public class HttpClient {

    public Observable<Document> getDocumentAsync(String url) {
        return ObservableUtils.create(() -> getDocument(url));
    }

    public Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko")
                .timeout(15000).get();
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
            conn.getOutputStream().write(serializeForm());

            conn.getHeaderFields();

            // TODO:
            return null;
        }

        private byte[] serializeForm() throws UnsupportedEncodingException {
            StringBuilder buffer = new StringBuilder();
            for (String key : form.keySet()) {
                buffer.append(URLEncoder.encode(key, "UTF-8"));
                buffer.append("=");
                buffer.append(URLEncoder.encode(form.get(key), "UTF-8"));
                buffer.append("&");
            }
            return buffer.toString().getBytes();
        }
    }
}