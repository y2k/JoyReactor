package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.net.URLEncoder;

/**
 * Created by y2k on 19/10/15.
 */
public class AddTagRequest {

    private String tagName;

    public AddTagRequest(String tagName) {
        this.tagName = tagName;
    }

    public Observable<Void> request() {
        return ObservableUtils.create(() -> {
            String tagUrl = "http://joyreactor.cc/tag/" + URLEncoder.encode(tagName);
            Document tagPage = HttpClient.getInstance().getDocument(tagUrl);
            String addTagLink = tagPage.select("a.change_favorite_link").first().absUrl("href");
            HttpClient.getInstance().getText(addTagLink);
            return null;
        });
    }
}