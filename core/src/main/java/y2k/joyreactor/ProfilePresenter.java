package y2k.joyreactor;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 9/30/15.
 */
public class ProfilePresenter {

    private final View view;

    public ProfilePresenter(View view) {
        this.view = view;
        initialize();
    }

    private void initialize() {
        view.setProgress(true);
        Profile.requestMine()
                .subscribe(profile -> {
                    view.setProfile(profile);
                    view.setProgress(false);
                }, Throwable::printStackTrace);
    }

    public void logout() {
        // TODO:
        view.setProgress(true);
        ForegroundScheduler.getInstance().createWorker().schedule(
                () -> view.setProgress(false), 2, TimeUnit.SECONDS);
    }

    public interface View {

        void setProfile(Profile profile);

        void setProgress(boolean isProgress);
    }
}