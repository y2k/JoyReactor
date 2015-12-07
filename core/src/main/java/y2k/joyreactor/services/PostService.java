package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.*;
import y2k.joyreactor.services.repository.*;
import y2k.joyreactor.services.synchronizers.PostFetcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class PostService {

    private Repository<Post> repository;
    private PostFetcher synchronizer;
    private Repository<Comment> commentRepository;
    private Repository<SimilarPost> similarPostRepository;
    private Repository<Attachment> attachmentRepository;

    public PostService(Repository<Post> repository,
                       PostFetcher synchronizer,
                       Repository<Comment> commentRepository,
                       Repository<SimilarPost> similarPostRepository,
                       Repository<Attachment> attachmentRepository) {
        this.repository = repository;
        this.synchronizer = synchronizer;
        this.commentRepository = commentRepository;
        this.similarPostRepository = similarPostRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public Observable<Post> synchronizePostAsync(String postId) {
        return synchronizer
                .synchronizeWithWeb(postId)
                .flatMap(_void -> repository.queryFirstAsync(new PostByIdQuery(postId)));
    }

    public Observable<CommentGroup> getCommentsAsync(int postId, int parentCommentId) {
        if (parentCommentId == 0)
            return getCommentForPost(postId);
        return commentRepository
                .queryFirstByIdAsync(parentCommentId)
                .flatMap(parent -> commentRepository
                        .queryAsync(new CommentsForPostQuery(postId, parentCommentId))
                        .map(children -> new CommentGroup.OneLevel(parent, children)));
    }

    private Observable<CommentGroup> getCommentForPost(int postId) {
        return commentRepository
                .queryAsync(new TwoLeverCommentQuery(postId))
                .map(CommentGroup.TwoLevel::new);
    }

    public Observable<Post> getFromCache(String postId) {
        return repository.queryFirstAsync(new PostByIdQuery(postId));
    }

    public Observable<List<Image>> getPostImages(int postId) {
        Observable<List<Image>> postAttachments = attachmentRepository
                .queryAsync(new AttachmentsQuery(postId))
                .flatMap(attachments -> Observable.from(attachments).map(s -> s.image).toList());
        Observable<List<Image>> commentAttachments = commentRepository
                .queryAsync(new CommentsWithImagesQuery(postId, 10))
                .flatMap(comments -> Observable.from(comments).map(Comment::getAttachment).toList());
        return postAttachments
                .flatMap(s -> commentAttachments.map(s2 -> union(s, s2)));
    }

    private List<Image> union(List<Image> s, List<Image> s2) {
        List<Image> result = new ArrayList<>(s);
        result.addAll(s2);
        return result;
    }

    public Observable<CommentGroup> getTopComments(int postId, int maxCount) {
        return commentRepository
                .queryAsync(new TopCommentsQuery(postId, maxCount))
                .map(CommentGroup.OneLevel::new);
    }

    public Observable<List<SimilarPost>> getSimilarPosts(int postId) {
        return similarPostRepository
                .queryAsync(new SimilarPostQuery(postId));
    }
}