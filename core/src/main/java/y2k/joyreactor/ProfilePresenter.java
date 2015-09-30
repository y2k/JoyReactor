package y2k.joyreactor;

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
        Profile.request()
                .subscribe(profile -> {
                    view.setProfile(profile);
                    view.setProgress(false);
                }, error -> {
                    // TODO:
                });
    }

    public void logout() {
        // TODO:
    }

    public interface View {

        void setProfile(Profile profile);

        void setProgress(boolean isProgress);
    }
}