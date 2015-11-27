package y2k.joyreactor.presenters;

import y2k.joyreactor.Tag;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.TagsService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.AddTagRequest;
import y2k.joyreactor.services.synchronizers.MyTagSynchronizer;

/**
 * Created by y2k on 08/10/15.
 */
public class AddTagPresenter {

    private View view;
    private TagsService service;

    public AddTagPresenter(View view) {
        this(view, new TagsService(new Repository<>(Tag.class), new MyTagSynchronizer(new Repository<>(Tag.class))));
    }

    AddTagPresenter(View view, TagsService service) {
        this.view = view;
        this.service = service;
    }

    public void add(String tag) {
        view.setIsBusy(true);
        service.addTag(tag)
                .subscribe(s -> {
                    view.setIsBusy(false);
                    Navigation.getInstance().closeAddTag();
                }, Throwable::printStackTrace);
    }

    public interface View {

        void setIsBusy(boolean isBusy);
    }
}