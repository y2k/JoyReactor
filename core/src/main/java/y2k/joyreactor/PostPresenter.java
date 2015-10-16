package y2k.joyreactor;

import java.io.File;
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

    public void openPostInBrowser() {
        Navigation.getInstance().openBrowser("http://joyreactor.cc/post/" + getArgumentPost().id);
    }

    private Post getArgumentPost() {
        return Navigation.getInstance().getArgumentPost();
    }

    public void selectComment(int commentId) {
        new CommentListRequest(getArgumentPost().id, commentId)
                .request()
                .subscribe(view::updateComments, Throwable::printStackTrace);
    }

    public void saveImageToGallery() {
        view.setIsBusy(true);
        new ImageRequest(getArgumentPost().image)
                .request()
                .subscribe(imageFile -> {
                    view.uploadToGallery(imageFile);
                    view.setIsBusy(false);
                });
    }

    public interface View {

        void updateComments(List<Comment> comments);

        void updatePostImage(Post post);

        void setIsBusy(boolean isBusy);

        void uploadToGallery(File imageFile);
    }
}