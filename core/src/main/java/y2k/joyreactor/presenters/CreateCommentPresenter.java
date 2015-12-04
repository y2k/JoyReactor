package y2k.joyreactor.presenters;

import y2k.joyreactor.Profile;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.CommentService;
import y2k.joyreactor.services.ProfileService;
import y2k.joyreactor.services.requests.CreateCommentRequest;

/**
 * Created by y2k on 10/4/15.
 */
public class CreateCommentPresenter {

    private View view;
    private CommentService service;

    public CreateCommentPresenter(View view) {
        this(view, new ProfileService(), new CommentService());
    }

    CreateCommentPresenter(View view, ProfileService profileService, CommentService service) {
        this.view = view;
        this.service = service;

        profileService.get()
                .subscribe(view::setUser, Throwable::printStackTrace);
    }

    public void create(String commentText) {
        view.setIsBusy(true);
        service.createComment("2219757", "10412483", commentText)
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