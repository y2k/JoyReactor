package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.Profile;
import y2k.joyreactor.SimilarPost;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.CommentService;
import y2k.joyreactor.services.ProfileService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.CreateCommentRequestFactory;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 10/4/15.
 */
public class CreateCommentPresenter {

    private View view;
    private CommentService service;

    public CreateCommentPresenter(View view) {
        this(view,
                new ProfileService(),
                new CommentService(
                        new CreateCommentRequestFactory(),
                        new PostSynchronizer(new Repository<>(SimilarPost.class))));
    }

    CreateCommentPresenter(View view, ProfileService profileService, CommentService service) {
        this.view = view;
        this.service = service;

        profileService.get().subscribe(view::setUser, Throwable::printStackTrace);
    }

    public void create(String commentText) {
        view.setIsBusy(true);
        service.createComment("2219757", commentText)
                .subscribe(_void -> {
                    Navigation.getInstance().closeCreateComment();
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void setUser(Profile profile);
    }
}