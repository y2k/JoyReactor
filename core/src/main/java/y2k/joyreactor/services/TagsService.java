package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Image;
import y2k.joyreactor.Tag;
import y2k.joyreactor.presenters.TagsPresenter;
import y2k.joyreactor.services.repository.MainTagQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.MyTagsRequest;
import y2k.joyreactor.services.requests.UserNameRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class TagsService {

    private Repository<Tag> repository;

    public TagsService(Repository<Tag> repository) {
        this.repository = repository;
    }

    public Observable<List<Tag>> get() {
        Observable<List<Tag>> subscription = new UserNameRequest()
                .request()
                .flatMap(username -> username == null
                        ? new DefaultTagRequest().request()
                        : new MyTagsRequest(username).request())
                .flatMap(repository::replaceAllAsync)
                .flatMap(s -> repository.queryAsync(new MainTagQuery()));
        return repository.queryAsync().mergeWith(subscription);
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