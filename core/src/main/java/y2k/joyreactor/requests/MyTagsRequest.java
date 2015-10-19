package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import y2k.joyreactor.Tag;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by y2k on 19/10/15.
 */
public class MyTagsRequest {

    private TagImageRequest imageRequest = new TagImageRequest();
    private String username;

    public MyTagsRequest(String username) {
        this.username = username;
    }

    public Observable<List<Tag>> request() {
        return ObservableUtils.create(() -> {
            Document document = HttpClient.getInstance().getDocument("http://joyreactor.cc/user/" + username);
            List<Tag> tags = new ArrayList<>();
            for (Element h : document.select(".sideheader")) {
                if ("Читает".equals(h.text())) {
                    for (Element a : h.parent().select("a"))
                        tags.add(createTag(a.text()));
                    break;
                }
            }
            Collections.sort(tags, (l, r) -> l.title.compareToIgnoreCase(r.title));
            return tags;
        });
    }

    private Tag createTag(String title) {
        Tag tag = new Tag();
        tag.title = title;
        tag.image = imageRequest.request(title);
        return tag;
    }
}