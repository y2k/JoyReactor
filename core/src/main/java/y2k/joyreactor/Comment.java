package y2k.joyreactor;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

/**
 * Created by y2k on 28/09/15.
 */
public class Comment {

    public String text;
    public String userAvatar;
    public int id;
    public int parentId;
    public int childCount;
    public float rating;

    public static class Collection extends ArrayList<Comment> {

        public static Observable<Comment.Collection> request(int postId) {
            Observable<Comment.Collection> subscription = Observable
                    .create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
                        try {
                            CommentListRequest request = new CommentListRequest(postId);
                            request.populate();
                            ChildrenCounter.compute(request.comments);
                            subscriber.onNext(request.comments);
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }));
            return subscription.observeOn(ForegroundScheduler.getInstance());
        }

        static class ChildrenCounter {

            public static void compute(Collection comments) {
                // TODO: оптимизировать
                for (int i = 0; i < comments.size() - 1; i++) {
                    Comment c = comments.get(i);
                    for (int n = i + 1; n < comments.size(); n++)
                        if (comments.get(n).parentId == c.id) c.childCount++;
                }
            }
        }
    }
}