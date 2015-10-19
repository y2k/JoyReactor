package y2k.joyreactor.presenters;

import y2k.joyreactor.Navigation;
import y2k.joyreactor.requests.AddTagRequest;

/**
 * Created by y2k on 08/10/15.
 */
public class AddTagPresenter {

    private View view;

    public AddTagPresenter(View view) {
        this.view = view;
    }

    public void addTag() {
        view.setIsBusy(true);
        new AddTagRequest(view.getTagName())
                .request()
                .subscribe(s -> {
                    view.setIsBusy(false);
                    Navigation.getInstance().closeAddTag();
                }, Throwable::printStackTrace);
    }

    public interface View {

        String getTagName();

        void setIsBusy(boolean isBusy);
    }
}