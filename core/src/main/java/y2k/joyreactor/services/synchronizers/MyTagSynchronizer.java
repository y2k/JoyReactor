package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Image;
import y2k.joyreactor.Tag;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.TagsForUserRequest;
import y2k.joyreactor.services.requests.UserNameRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class MyTagSynchronizer {

    private Repository<Tag> repository;

    public MyTagSynchronizer(Repository<Tag> repository) {
        this.repository = repository;
    }

    public Observable<?> synchronize() {
        return new UserNameRequest()
                .request()
                .flatMap(username -> username == null
                        ? new DefaultTagRequest().request()
                        : new TagsForUserRequest(username).request())
                .flatMap(newTags -> repository.queryAsync().flatMap(tags -> merge(tags, newTags)))
                .flatMap(tags -> repository.replaceAllAsync(tags));
    }

    private Observable<List<Tag>> merge(List<Tag> oldTags, List<Tag> newTags) {
        List<Tag> result = new ArrayList<>();

        for (Tag s : oldTags) s.isMine = false;
        for (Tag s : newTags) s.isMine = true;

        result.addAll(oldTags);
        addOrReplaceAll(result, newTags);

        return Observable.just(result);
    }

    private void addOrReplaceAll(List<Tag> left, List<Tag> right) {
        for (Tag tag : right) {
            Tag old = searchForServerid(left, tag.getServerId());
            if (old == null) {
                left.add(tag);
            } else {
                tag.id = old.id;
                left.set(left.indexOf(old), tag);
            }
        }
    }

    private Tag searchForServerid(List<Tag> tags, String serverId) {
        for (Tag tag : tags)
            if (serverId.equals(tag.getServerId())) return tag;
        return null;
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