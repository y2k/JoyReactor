package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;

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
        return null;
    }

    public class Form {

        public Form put(String key, String value) {
            return this;
        }

        public Document send(String url) {
            // TODO:
            return null;
        }
    }
}