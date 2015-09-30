package y2k.joyreactor;

/**
 * Created by y2k on 9/29/15.
 */
public class LoginPresenter {

    private final View view;

    public LoginPresenter(View view) {
        this.view = view;
    }

    public void login() {
        view.setProgress(true);
        new LoginRequest(view.getUsername(), view.getPassword())
                .request()
                .subscribe(
                        s -> view.setProgress(false),
                        error -> {
                            view.setProgress(false);
                            view.showError();
                        });
    }

    public void register() {
        view.openUrl("http://joyreactor.cc/register");
    }

    public interface View {

        String getUsername();

        String getPassword();

        void setProgress(boolean isProgress);

        void showError();

        void openUrl(String url);
    }
}