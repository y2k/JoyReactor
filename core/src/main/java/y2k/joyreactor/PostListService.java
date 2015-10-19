package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 06/10/15.
 */
public class PostListService {

    private List<Post> buffer = new ArrayList<>();
    private String nextPageId;
    private Tag tag;

    public void setCurrentTag(Tag tag) {
        this.tag = tag;
        buffer.clear();
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
        return Observable.just(buffer);
    }

    public Observable<Void> reset() {
        nextPageId = null;
        buffer.clear();
        return Observable.just(null);
    }

    class PostMerger {

        void addNewPosts(PostsForTagRequest request) {
            for (Post post : request.posts)
                if (isNew(post)) buffer.add(post);
        }

        private boolean isNew(Post post) {
            for (Post oldPost : buffer)
                if (oldPost.id.equals(post.id)) return false;
            return true;
        }
    }
}