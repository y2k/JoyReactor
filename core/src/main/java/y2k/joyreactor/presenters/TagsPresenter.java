package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.*;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.MyTagsRequest;
import y2k.joyreactor.services.requests.UserNameRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class TagsPresenter extends Presenter {

    private final View view;

    public TagsPresenter(View view) {
        this.view = view;
    }

    @Override
    public void activate() {
        Repository<Tag> repository = new Repository<>(Tag.class);

        Observable<List<Tag>> subscription = new UserNameRequest()
                .request()
                .flatMap(username -> username == null
                        ? new DefaultTagRequest().request()
                        : new MyTagsRequest(username).request())
                .flatMap(repository::replaceAllAsync)
                .flatMap(s -> repository.queryAsync());

        repository
                .queryAsync()
                .mergeWith(subscription)
                .subscribe(view::reloadData, Throwable::printStackTrace);
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

    private static class DefaultTagRequest {

        private List<Tag> tags = new ArrayList<>();

        DefaultTagRequest() {
            addTag("Anime", "2851");
            addTag("Красивые картинки", "31505");
            addTag("Игры", "753");

            addTag("Длинные картинки", "2851");
            addTag("hi-res", "2851");

            addTag("Комиксы", "27");
            addTag("Гифки", "116");
            addTag("Песочница", "10891");
            addTag("Geek", "7");
            addTag("Котэ", "1481");
            addTag("Видео", "1243");
            addTag("Story", "227");
        }

        private void addTag(String title, String tagId) {
            Tag tag = new Tag();
            tag.title = title;
            tag.image = new Image("http://img0.joyreactor.cc/pics/avatar/tag/" + tagId);
            tags.add(tag);
        }

        public Observable<List<Tag>> request() {
            return Observable.just(tags);
        }
    }
}