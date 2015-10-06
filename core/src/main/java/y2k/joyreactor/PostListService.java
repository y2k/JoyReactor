package y2k.joyreactor;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 06/10/15.
 */
public class PostListService {

    private List<Post> buffer = new ArrayList<>();
    private String nextPageId;

    public Observable<Void> loadNextPageAsync() {
        return ObservableUtils.create(() -> {
            PostRequest request = new PostRequest(nextPageId);
            request.request();

            buffer.addAll(request.posts);
            nextPageId = request.nextPageId;
            return null;
        });
    }

    public Observable<List<Post>> getList() {
        return Observable.just(buffer);
    }
}