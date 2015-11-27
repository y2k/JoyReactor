package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.services.repository.PostsForTagQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.PostListSynchronizer;

import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class TagService {

    private Tag tag;
    private Repository<Post> repository;
    private PostListSynchronizer synchronizer;
    private PostListSynchronizer.Factory synchronizerFactory;

    public TagService(Repository<Post> repository,
                      PostListSynchronizer.Factory synchronizerFactory) {
        this.repository = repository;
        this.synchronizerFactory = synchronizerFactory;
    }

    private TagService(Tag tag, Repository<Post> repository, PostListSynchronizer synchronizer) {
        this.tag = tag;
        this.repository = repository;
        this.synchronizer = synchronizer;
    }

    public TagService make(Tag tag) {
        return new TagService(tag, repository, synchronizerFactory.make(tag));
    }

    public Observable<Boolean> preloadNewPosts() {
        return synchronizer.preloadNewPosts();
    }

    public Observable<List<Post>> applyNew() {
        return synchronizer.applyNew().flatMap(s -> getFromRepository());
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