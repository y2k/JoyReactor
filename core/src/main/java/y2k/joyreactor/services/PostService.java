package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Comment;
import y2k.joyreactor.Post;
import y2k.joyreactor.services.repository.CommentsForPostQuery;
import y2k.joyreactor.services.repository.PostByIdQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.PostSynchronizer;

import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class PostService {

    private final Repository<Post> repository;
    private final PostSynchronizer synchronizer;
    private final Repository<Comment> commentRepository;

    public PostService(Repository<Post> repository, PostSynchronizer synchronizer, Repository<Comment> commentRepository) {
        this.repository = repository;
        this.synchronizer = synchronizer;
        this.commentRepository = commentRepository;
    }

    public Observable<Post> synchronizePostAsync(String postId) {
        return synchronizer
                .synchronizeWithWeb(postId)
                .flatMap(_void -> repository.queryFirstAsync(new PostByIdQuery(postId)));
    }

    public Observable<List<Comment>> getCommentsAsync(int postId, int parentCommentId) {
        return commentRepository.queryAsync(new CommentsForPostQuery(postId, parentCommentId));
    }

    public Observable<Post> queryFirstAsync(String postId) {
        return repository.queryFirstAsync(new PostByIdQuery(postId));
    }
}