package y2k.joyreactor;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by y2k on 9/27/15.
 */
public class Post {

    public String title;
    public String image;
    public int width;
    public int height;

    public static class Collection extends ArrayList<Post> {

        public static Observable<Post.Collection> get() {
            Observable<Post.Collection> result = Observable
                    .create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
                        try {
                            subscriber.onNext(new PostLoader().getPosts());
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }));
            return result.observeOn(ForegroundScheduler.getInstance());
        }
    }
}