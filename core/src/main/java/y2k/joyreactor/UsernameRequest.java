package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rx.Observable;

/**
 * Created by y2k on 10/4/15.
 */
public class UsernameRequest {

    Observable<String> request() {
        return ObservableUtils.create(() -> {
            Document document = new HttpClient().getDocument("http://joyreactor.cc/donate");
            Element node = document.select("a#settings").first();
            return node == null ? null : node.text();
        });
    }
}