package y2k.joyreactor.presenters;

import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.requests.CreateCommentRequest;

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
}