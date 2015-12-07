package y2k.joyreactor.presenters;

import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.TagsService;

/**
 * Created by y2k on 08/10/15.
 */
public class AddTagPresenter {

    private View view;
    private TagsService service;

    public AddTagPresenter(View view, TagsService service) {
        this.view = view;
        this.service = service;
    }

    public void add(String tag) {
        view.setIsBusy(true);
        service.addTag(tag)
                .subscribe(s -> {
                    view.setIsBusy(false);
                    Navigation.getInstance().closeAddTag();
                }, e -> {
                    e.printStackTrace();
                    view.setIsBusy(false);
                    view.showErrorMessage();
                });
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void showErrorMessage();
    }
}