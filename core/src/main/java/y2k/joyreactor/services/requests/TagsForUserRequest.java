package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import y2k.joyreactor.Image;
import y2k.joyreactor.Tag;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by y2k on 19/10/15.
 */
public class TagsForUserRequest {

    private TagImageRequest imageRequest = new TagImageRequest();
    private String username;

    public TagsForUserRequest(String username) {
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

    private Tag createTag(String title) throws IOException {
        Tag tag = new Tag();
        tag.title = title;
        tag.image = new Image(imageRequest.request(title), 0, 0);
        return tag;
    }
}