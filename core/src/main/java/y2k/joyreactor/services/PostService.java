package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.*;
import y2k.joyreactor.common.PartialResult;
import y2k.joyreactor.services.repository.*;
import y2k.joyreactor.services.requests.OriginalImageRequestFactory;
import y2k.joyreactor.services.synchronizers.PostFetcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/24/15.
 */
public class PostService {

    private OriginalImageRequestFactory imageRequestFactory;
    private PostFetcher synchronizer;
    private Repository<Post> repository;
    private Repository<Comment> commentRepository;
    private Repository<SimilarPost> similarPostRepository;
    private Repository<Attachment> attachmentRepository;

    public PostService(PostFetcher synchronizer,
                       Repository<Post> repository,
                       Repository<Comment> commentRepository,
                       Repository<SimilarPost> similarPostRepository,
                       Repository<Attachment> attachmentRepository,
                       OriginalImageRequestFactory imageRequestFactory) {
        this.repository = repository;
        this.synchronizer = synchronizer;
        this.commentRepository = commentRepository;
        this.similarPostRepository = similarPostRepository;
        this.attachmentRepository = attachmentRepository;
        this.imageRequestFactory = imageRequestFactory;
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

    public Observable<File> mainImage(String serverPostId) {
        return repository
                .queryFirstAsync(new PostByIdQuery(serverPostId))
                .map(post -> post.image.fullUrl(null))
                .flatMap(url -> imageRequestFactory.request(url));
    }

    public Observable<PartialResult<File>> mainImagePartial(String serverPostId) {
        return repository
                .queryFirstAsync(new PostByIdQuery(serverPostId))
                .map(post -> post.image.fullUrl(null))
                .flatMap(url -> imageRequestFactory.requestPartial(url));
    }
}