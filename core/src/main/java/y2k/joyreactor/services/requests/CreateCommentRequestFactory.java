package y2k.joyreactor.services.requests;

import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 19/10/15.
 */
public class CreateCommentRequestFactory {

    public static final Pattern TOKEN_REGEX = Pattern.compile("var token = '(.+?)'");
    private String commentId;

    public CreateCommentRequestFactory() {
        // TODO
    }

    public Observable<Void> create(String postId, String commentText) {
        return ObservableUtils.create(() -> {
            HttpClient.getInstance()
                    .beginForm()
                    .put("parent_id", commentId == null ? "0" : commentId)
                    .put("post_id", postId)
                    .put("token", getToken())
                    .put("comment_text", commentText)
                    .putHeader("X-Requested-With", "XMLHttpRequest")
                    .putHeader("Referer", "http://joyreactor.cc/post/" + postId)
                    .send("http://joyreactor.cc/post_comment/create");
            return null;
        });
    }

    private String getToken() throws IOException {
        String document = HttpClient.getInstance().getText("http://joyreactor.cc/donate");
        Matcher m = TOKEN_REGEX.matcher(document);
        if (!m.find()) throw new IllegalStateException();
        return m.group(1);
    }
}
