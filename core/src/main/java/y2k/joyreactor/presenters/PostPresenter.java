package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.Comment;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.Post;
import y2k.joyreactor.synchronizers.PostSynchronizer;
import y2k.joyreactor.repository.CommentsForPostQuery;
import y2k.joyreactor.repository.PostByIdQuery;
import y2k.joyreactor.repository.Repository;
import y2k.joyreactor.requests.OriginalImageRequest;

import java.io.File;
import java.util.List;

/**
 * Created by y2k on 28/09/15.
 */
public class PostPresenter {

    private View view;
    private Repository<Post> repository;
    private Repository<Comment> commentRepository;
    private PostSynchronizer synchronizer;

    public PostPresenter(View view) {
        this(view, new Repository<>(Post.class), new PostSynchronizer(), new Repository<>(Comment.class));
    }

    public PostPresenter(View view, Repository<Post> repository, PostSynchronizer synchronizer, Repository<Comment> commentRepository) {
        this.view = view;
        this.repository = repository;
        this.synchronizer = synchronizer;
        this.commentRepository = commentRepository;

        initialize();
    }

    private void initialize() {
        view.setIsBusy(true);
        synchronizer
                .synchronizeWithWeb(getArgumentPostId())
                .flatMap(_void -> getPostFromRepository())
                .subscribe(post -> {
                    view.setIsBusy(false);
                    view.updatePostImage(post);

                    commentRepository
                            .queryAsync(new CommentsForPostQuery(post.id, 0))
                            .subscribe(view::updateComments, Throwable::printStackTrace);
                });
    }

    public void selectComment(int commentId) {
        getPostFromRepository()
                .flatMap(post -> commentRepository.queryAsync(new CommentsForPostQuery(post.id, commentId)))
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
        return repository.queryFirstAsync(new PostByIdQuery(getArgumentPostId()));
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