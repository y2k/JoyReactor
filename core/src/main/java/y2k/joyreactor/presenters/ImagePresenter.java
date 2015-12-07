package y2k.joyreactor.presenters;

import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.PostService;

import java.io.File;

/**
 * Created by y2k on 10/25/15.
 */
public class ImagePresenter {

    public ImagePresenter(View view, PostService service) {
        view.setBusy(true);
        service.mainImage(Navigation.getInstance().getArgumentPostId())
                .subscribe(imageFile -> {
                    view.showImage(imageFile);
                    view.setBusy(false);
                }, e -> {
                    e.printStackTrace();
                    view.setBusy(false);
                });
    }

    public interface View {

        void setBusy(boolean isBusy);

        void showImage(File imageFile);
    }
}