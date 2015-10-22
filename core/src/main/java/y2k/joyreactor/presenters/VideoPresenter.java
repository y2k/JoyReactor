package y2k.joyreactor.presenters;

import y2k.joyreactor.Navigation;
import y2k.joyreactor.requests.OriginalImageRequest;

import java.io.File;

/**
 * Created by y2k on 22/10/15.
 */
public class VideoPresenter {

    public VideoPresenter(View view) {
        view.setBusy(true);
        new OriginalImageRequest(getVideoUrl())
                .request()
                .subscribe(videoFile -> {
                    view.showVideo(videoFile);
                    view.setBusy(false);
                }, Throwable::printStackTrace);
    }

    private String getVideoUrl() {
        return Navigation.getInstance().getArgumentPost().image;
    }

    public interface View {

        void showVideo(File videoFile);

        void setBusy(boolean isBusy);
    }
}