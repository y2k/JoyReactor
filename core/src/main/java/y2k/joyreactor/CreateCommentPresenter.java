package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 10/4/15.
 */
public class CreateCommentPresenter {

    private View view;

    public CreateCommentPresenter(View view) {
        this.view = view;
    }

    public void create() {
        view.setIsBusy(true);
        new CreateCommentRequest("2219757", "10412483")
                .request(view.getCommentText())
                .subscribe(s -> {
                    Navigation.getInstance().closeCreateComment();
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    public interface View {

        String getCommentText();

        void setIsBusy(boolean isBusy);
    }

    private static class CreateCommentRequest {

        public static final Pattern TOKEN_REGEX = Pattern.compile("var token = '(.+?)'");
        private String postId;
        private String commentId;

        private CreateCommentRequest(String postId, String commentId) {
            this.postId = postId;
            this.commentId = commentId;
        }

        public Observable<Void> request(String commentText) {
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
}