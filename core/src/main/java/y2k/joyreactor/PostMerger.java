package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;

import java.util.List;

/**
 * Created by y2k on 10/31/15.
 */
public class PostMerger {

    private Repository<Post> repository;

    public PostMerger(Repository<Post> repository) {
        this.repository = repository;
    }

    public Observable<Void> mergeFirstPage(List<Post> posts) {
        return ObservableUtils.create(() -> {
            // TODO
        });
    }

    public boolean hasNew(List<Post> posts) {
        return false;
    }

    public Observable<Void> mergeNextPage(List<Post> posts) {
        return ObservableUtils.create(() -> {
            // TODO
        });
    }

    public int getDivider() {
        return 0;
    }
}