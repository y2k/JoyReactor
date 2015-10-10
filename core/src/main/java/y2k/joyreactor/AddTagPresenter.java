package y2k.joyreactor;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 08/10/15.
 */
public class AddTagPresenter {

    private View view;

    public AddTagPresenter(View view) {
        this.view = view;
    }

    public void addTag() {
        // TODO:
        view.setIsBusy(true);
        ForegroundScheduler.getInstance().createWorker().schedule(() -> {
            // TODO
            view.setIsBusy(false);

            Navigation.getInstance().closeAddTag();
        }, 2, TimeUnit.SECONDS);

    }

    public interface View {

        String getTagName();

        void setIsBusy(boolean isBusy);
    }
}