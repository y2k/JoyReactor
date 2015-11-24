package y2k.joyreactor.presenters;

import y2k.joyreactor.*;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.services.TagsService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.UserNameRequest;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class TagsPresenter extends Presenter {

    private View view;
    private TagsService service;

    public TagsPresenter(View view) {
        this(view, new TagsService(new Repository<>(Tag.class)));
    }

    TagsPresenter(View view, TagsService service) {
        this.view = view;
        this.service = service;
    }

    @Override
    public void activate() {
        service.get().subscribe(view::reloadData, Throwable::printStackTrace);
    }

    public void selectTag(Tag tag) {
        new Messages.TagSelected(tag).broadcast();
    }

    public void selectedFeatured() {
        new Messages.TagSelected(Tag.makeFeatured()).broadcast();
    }

    public void selectedFavorite() {
        new UserNameRequest()
                .request()
                .subscribe(username -> new Messages.TagSelected(Tag.makeFavorite(username)).broadcast());
    }

    public interface View {

        void reloadData(List<Tag> tags);
    }
}