package y2k.joyreactor.presenters;

import y2k.joyreactor.*;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.platform.Platform;
import y2k.joyreactor.services.PostService;

import java.io.File;
import java.util.List;

/**
 * Created by y2k on 28/09/15.
 */
public class PostPresenter {

    private View view;
    private PostService service;

    public PostPresenter(View view, PostService service) {
        this.view = view;
        this.service = service;
        initialize();
    }

    private void initialize() {
        view.setIsBusy(true);
        service.synchronizePostAsync(getArgumentPostId())
                .subscribe(post -> {
                    view.setIsBusy(false);
                    view.updatePostInformation(post);

                    service.getPostImages(post.id)
                            .subscribe(view::updatePostImages, Throwable::printStackTrace);
                    service.getCommentsAsync(post.id, 0)
                            .subscribe(view::updateComments, Throwable::printStackTrace);
                    service.getSimilarPosts(post.id)
                            .subscribe(view::updateSimilarPosts, Throwable::printStackTrace);
                    service.mainImagePartial(post.serverId)
                            .subscribe(partial -> {
                                if (partial.result == null) {
                                    view.updateImageDownloadProgress(partial.progress, partial.max);
                                } else {
                                    view.updatePostImage(partial.result);
                                }
                            }, Throwable::printStackTrace);
                }, Throwable::printStackTrace);
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
                .flatMap(post -> service.mainImage(post.serverId))
                .flatMap(imageFile -> Platform.Instance.saveToGallery(imageFile))
                .subscribe(_void -> {
                    view.showImageSuccessSavedToGallery();
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    private String getArgumentPostId() {
        return Navigation.getInstance().getArgumentPostId();
    }

    public void replyToComment(Comment comment) {
        // TODO:
    }

    public void replyToPost() {
        // TODO:
        Navigation.getInstance().openCreateComment();
    }

    public interface View {

        void updateComments(CommentGroup comments);

        @Deprecated
        void updatePostInformation(Post post);

        void setIsBusy(boolean isBusy);

        void showImageSuccessSavedToGallery();

        void updatePostImages(List<Image> images);

        void updateSimilarPosts(List<SimilarPost> similarPosts);

        void updatePostImage(File image);

        void updateImageDownloadProgress(int progress, int maxProgress);
    }
}