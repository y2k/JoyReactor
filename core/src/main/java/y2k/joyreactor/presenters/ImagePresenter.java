package y2k.joyreactor.presenters;

import y2k.joyreactor.Navigation;
import y2k.joyreactor.requests.OriginalImageRequest;

import java.io.File;

/**
 * Created by y2k on 10/25/15.
 */
public class ImagePresenter {

    public ImagePresenter(View view) {
        view.setBusy(true);
        new OriginalImageRequest(getVideoUrl())
                .request()
                .subscribe(imageFile -> {
                    view.showImage(imageFile);
                    view.setBusy(false);
                }, e -> {
                    e.printStackTrace();
                    view.setBusy(false);
                });
    }

    private String getVideoUrl() {
        return Navigation.getInstance().getArgumentPost().image;
    }

    public interface View {

        void setBusy(boolean isBusy);

        void showImage(File imageFile);
    }
}