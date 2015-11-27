package y2k.joyreactor.presenters;

import y2k.joyreactor.Comment;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.Post;
import y2k.joyreactor.platform.Platform;
import y2k.joyreactor.services.PostService;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.OriginalImageRequest;

import java.util.List;

/**
 * Created by y2k on 28/09/15.
 */
public class PostPresenter {

    private View view;
    private PostService service;

    public PostPresenter(View view) {
        this(view, new PostService(new Repository<>(Post.class), new PostSynchronizer(), new Repository<>(Comment.class)));
    }

    PostPresenter(View view, PostService service) {
        this.view = view;
        this.service = service;
        initialize();
    }

    private void initialize() {
        view.setIsBusy(true);
        service.synchronizePostAsync(getArgumentPostId())
                .subscribe(post -> {
                    view.setIsBusy(false);
                    view.updatePostImage(post);

                    service.getCommentsAsync(post.id, 0)
                            .subscribe(view::updateComments, Throwable::printStackTrace);
                });
    }

    public void selectComment(int commentId) {
        service.getFromCache(getArgumentPostId())
                .flatMap(post -> service.getCommentsAsync(post.id, commentId))
                .subscribe(view::updateComments, Throwable::printStackTrace);
    }

    public void openPostInBrowser() {
        Navigation.getInstance().openBrowser("http://joyreactor.cc/post/" + getArgumentPostId());
    }

    public void saveImageToGallery() {
        view.setIsBusy(true);
        service.getFromCache(getArgumentPostId())
                .flatMap(post -> new OriginalImageRequest(post.image.fullUrl(null)).request())
                .flatMap(imageFile -> Platform.Instance.saveToGallery(imageFile))
                .subscribe(_void -> {
                    view.showImageSuccessSavedToGallery();
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    private String getArgumentPostId() {
        return Navigation.getInstance().getArgumentPostId();
    }

    public interface View {

        void updateComments(List<Comment> comments);

        void updatePostImage(Post post);

        void setIsBusy(boolean isBusy);

        void showImageSuccessSavedToGallery();
    }
}