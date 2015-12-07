package y2k.joyreactor.presenters;

import y2k.joyreactor.Attachment;
import y2k.joyreactor.Comment;
import y2k.joyreactor.Post;
import y2k.joyreactor.SimilarPost;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.PostService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.OriginalImageRequest;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;

import java.io.File;

/**
 * Created by y2k on 22/10/15.
 */
public class VideoPresenter {

    public VideoPresenter(View view) {
        this(view, new PostService(
                new Repository<>(Post.class),
                new PostSynchronizer(new Repository<>(SimilarPost.class), new Repository<>(Attachment.class)),
                new Repository<>(Comment.class),
                new Repository<>(SimilarPost.class),
                new Repository<>(Attachment.class)));
    }

    VideoPresenter(View view, PostService service) {
        view.setBusy(true);
        service.getFromCache(Navigation.getInstance().getArgumentPostId())
                .map(post -> post.image.fullUrl("mp4"))
                .flatMap(url -> new OriginalImageRequest(url).request())
                .subscribe(videoFile -> {
                    view.showVideo(videoFile);
                    view.setBusy(false);
                }, e -> {
                    e.printStackTrace();
                    view.setBusy(false);
                });
    }

    public interface View {

        void showVideo(File videoFile);

        void setBusy(boolean isBusy);
    }
}