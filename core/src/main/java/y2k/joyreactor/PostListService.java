package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.List;

/**
 * Created by y2k on 06/10/15.
 */
public class PostListService {

    private Repository<Post> repository;
    private String nextPageId;
    private Tag tag;

    public PostListService() {
        repository = new Repository<>("posts." + null, 1);
    }

    public void setCurrentTag(Tag tag) {
        this.tag = tag;

        repository = new Repository<>("posts." + tag.getId(), 1);
        repository.clear();
        nextPageId = null;
    }

    public Observable<Void> loadNextPageAsync() {
        return ObservableUtils.create(() -> {
            PostsForTagRequest request = new PostsForTagRequest(tag == null ? null : tag.getId(), nextPageId);
            request.request();
            return request;
        }).map(request -> {
            new PostMerger().addNewPosts(request);
            nextPageId = request.nextPageId;
            return null;
        });
    }

    public Observable<List<Post>> getList() {
        return Observable.just(repository.getAll());
    }

    public Observable<Void> reset() {
        nextPageId = null;
        repository.clear();
        return Observable.just(null);
    }

    class PostMerger {

        void addNewPosts(PostsForTagRequest request) {
            for (Post post : request.posts)
                if (isNew(post)) repository.add(post);
        }

        private boolean isNew(Post post) {
            for (Post oldPost : repository.getAll())
                if (oldPost.id.equals(post.id)) return false;
            return true;
        }
    }
}