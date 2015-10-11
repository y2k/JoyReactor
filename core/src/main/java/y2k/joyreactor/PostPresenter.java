package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 28/09/15.
 */
public class PostPresenter {

    private View view;

    public PostPresenter(View view) {
        this.view = view;

        loadComments();
        loadPost();
    }

    private void loadComments() {
        new CommentListRequest(getArgumentPost().id, 0)
                .request()
                .subscribe(view::updateComments, Throwable::printStackTrace);
    }

    private void loadPost() {
        view.updatePostImage(getArgumentPost());
    }

    private Post getArgumentPost() {
        return Navigation.getInstance().getArgumentPost();
    }

    public void selectComment(int commentId) {
        new CommentListRequest(getArgumentPost().id, commentId)
                .request()
                .subscribe(view::updateComments, Throwable::printStackTrace);
    }

    public interface View {

        void updateComments(List<Comment> comments);

        void updatePostImage(Post post);
    }
}