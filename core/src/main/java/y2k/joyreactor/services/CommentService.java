package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.services.requests.CreateCommentRequestFactory;
import y2k.joyreactor.services.synchronizers.PostFetcher;

/**
 * Created by y2k on 04/12/15.
 */
public class CommentService {

    private PostFetcher postFetcher;
    private CreateCommentRequestFactory requestFactory;

    public CommentService(CreateCommentRequestFactory requestFactory, PostFetcher postFetcher) {
        this.requestFactory = requestFactory;
        this.postFetcher = postFetcher;
    }

    public Observable<?> createComment(String postId, String commentText) {
        return requestFactory
                .create(postId, commentText)
                .flatMap(_void -> postFetcher.synchronizeWithWeb(postId));
    }
}