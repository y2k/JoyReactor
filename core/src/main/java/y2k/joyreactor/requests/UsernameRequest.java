package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import y2k.joyreactor.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

/**
 * Created by y2k on 10/4/15.
 */
public class UsernameRequest {

    public Observable<String> request() {
        return ObservableUtils.create(() -> {
            Document document = HttpClient.getInstance().getDocument("http://joyreactor.cc/donate");
            Element node = document.select("a#settings").first();
            return node == null ? null : node.text();
        });
    }
}