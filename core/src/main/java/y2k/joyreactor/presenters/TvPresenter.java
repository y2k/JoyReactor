package y2k.joyreactor.presenters;

import y2k.joyreactor.Tag;
import y2k.joyreactor.services.TagsService;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.MyTagSynchronizer;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class TvPresenter {

    public TvPresenter(View view) {
        this(view, new TagsService(new Repository<>(Tag.class), new MyTagSynchronizer(new Repository<>(Tag.class))));
    }

    TvPresenter(View view, TagsService service) {
        service.getMyTags().subscribe(view::updateTags, Throwable::printStackTrace);
    }

    public interface View {

        void updateTags(List<Tag> tags);
    }
}