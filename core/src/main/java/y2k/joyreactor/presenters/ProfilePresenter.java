package y2k.joyreactor.presenters;

import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.Profile;
import y2k.joyreactor.services.ProfileService;

/**
 * Created by y2k on 9/30/15.
 */
public class ProfilePresenter {

    private ProfileService service;

    public ProfilePresenter(View view, ProfileService service) {
        this.service = service;

        view.setBusy(true);
        service.get()
                .subscribe(profile -> {
                    view.setProfile(profile);
                    view.setBusy(false);
                }, e -> {
                    e.printStackTrace();
                    Navigation.getInstance().switchProfileToLogin();
                });
    }

    public void logout() {
        service.logout().subscribe(_void -> Navigation.getInstance().switchProfileToLogin());
    }

    public interface View {

        void setProfile(Profile profile);

        void setBusy(boolean isBusy);
    }
}