package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.services.repository.PostsForTagQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.PostListFetcher;

import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class TagService {

    private Repository<Post> repository;
    private PostListFetcher.Factory synchronizerFactory;

    private PostListFetcher synchronizer;
    private Tag tag;

    public TagService(Repository<Post> repository,
                      PostListFetcher.Factory synchronizerFactory) {
        this.repository = repository;
        this.synchronizerFactory = synchronizerFactory;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        this.synchronizer = synchronizerFactory.make(tag);
    }

    public Observable<Boolean> preloadNewPosts() {
        return synchronizer.preloadNewPosts();
    }

    public Observable<List<Post>> applyNew() {
        return synchronizer
                .applyNew()
                .flatMap(s -> getFromRepository());
    }

    public Integer getDivider() {
        return synchronizer.getDivider();
    }

    public Observable<List<Post>> loadNextPage() {
        return synchronizer.loadNextPage().flatMap(s -> getFromRepository());
    }

    public Observable<List<Post>> reloadFirstPage() {
        return synchronizer.reloadFirstPage().flatMap(s -> getFromRepository());
    }

    public Observable<List<Post>> queryAsync() {
        return getFromRepository();
    }

    private Observable<List<Post>> getFromRepository() {
        return repository.queryAsync(new PostsForTagQuery(tag));
    }
}