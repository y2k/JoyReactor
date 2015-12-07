package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Tag;
import y2k.joyreactor.services.repository.MyTagQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.requests.AddTagRequest;
import y2k.joyreactor.services.requests.UserNameRequest;
import y2k.joyreactor.services.synchronizers.MyTagFetcher;

import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class TagsService {

    private Repository<Tag> repository;
    private MyTagFetcher synchronizer;

    public TagsService(Repository<Tag> repository, MyTagFetcher synchronizer) {
        this.repository = repository;
        this.synchronizer = synchronizer;
    }

    public Observable<List<Tag>> getMyTags() {
        return getFromRepo().mergeWith(
                synchronizer.synchronize().flatMap(_void -> getFromRepo()));

    }

    private Observable<List<Tag>> getFromRepo() {
        return repository.queryAsync(new MyTagQuery());
    }

    public Observable<?> addTag(String tag) {
        return new AddTagRequest(tag).request();
    }

    public Observable<Tag> getTagForFavorite() {
        return new UserNameRequest().request().map(Tag::makeFavorite);
    }
}