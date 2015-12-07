package y2k.joyreactor.presenters;

import y2k.joyreactor.services.ProfileService;
import y2k.joyreactor.platform.Navigation;

/**
 * Created by y2k on 9/29/15.
 */
public class LoginPresenter {

    private View view;
    private ProfileService service;

    public LoginPresenter(View view, ProfileService service) {
        this.view = view;
        this.service = service;
    }

    public void login(String username, String password) {
        view.setBusy(true);
        service.login(username, password)
                .subscribe(s -> {
                    view.setBusy(false);
                    Navigation.getInstance().switchLoginToProfile();
                }, error -> {
                    error.printStackTrace();
                    view.setBusy(false);
                    view.showError();
                });
    }

    public void register() {
        view.openUrl("http://joyreactor.cc/register");
    }

    public interface View {

        void setBusy(boolean isBusy);

        void showError();

        void openUrl(String url);
    }
}