package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.Comment;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.Post;
import y2k.joyreactor.services.PostService;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.OriginalImageRequest;

import java.io.File;
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
        getPostFromRepository()
                .flatMap(post -> service.getCommentsAsync(post.id, commentId))
                .subscribe(view::updateComments, Throwable::printStackTrace);
    }

    public void openPostInBrowser() {
        Navigation.getInstance().openBrowser("http://joyreactor.cc/post/" + getArgumentPostId());
    }

    public void saveImageToGallery() {
        view.setIsBusy(true);
        getPostFromRepository()
                .flatMap(post -> new OriginalImageRequest(post.image.fullUrl(null)).request())
                .subscribe(imageFile -> {
                    view.uploadToGallery(imageFile);
                    view.setIsBusy(false);
                });
    }

    private Observable<Post> getPostFromRepository() {
        return service.queryFirstAsync(getArgumentPostId());
    }

    private String getArgumentPostId() {
        return Navigation.getInstance().getArgumentPostId();
    }

    public interface View {

        void updateComments(List<Comment> comments);

        void updatePostImage(Post post);

        void setIsBusy(boolean isBusy);

        void uploadToGallery(File imageFile);
    }
}