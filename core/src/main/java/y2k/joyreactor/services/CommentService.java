package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.services.requests.CreateCommentRequestFactory;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;

/**
 * Created by y2k on 04/12/15.
 */
public class CommentService {

    private PostSynchronizer postSynchronizer;
    private CreateCommentRequestFactory requestFactory;

    public CommentService(CreateCommentRequestFactory requestFactory, PostSynchronizer postSynchronizer) {
        this.requestFactory = requestFactory;
        this.postSynchronizer = postSynchronizer;
    }

    public Observable<?> createComment(String postId, String commentText) {
        return requestFactory
                .create(postId, commentText)
                .flatMap(_void -> postSynchronizer.synchronizeWithWeb(postId));
    }
}