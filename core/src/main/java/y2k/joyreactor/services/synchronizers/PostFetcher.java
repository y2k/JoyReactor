package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Attachment;
import y2k.joyreactor.Comment;
import y2k.joyreactor.Post;
import y2k.joyreactor.SimilarPost;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.services.repository.*;
import y2k.joyreactor.services.requests.PostRequest;

import java.util.List;

/**
 * Created by y2k on 11/21/15.
 */
public class PostFetcher {

    private PostRequest postRequest = new PostRequest();

    private Repository<Post> postRepository = new Repository<>(Post.class);
    private Repository<Comment> commentRepository = new Repository<>(Comment.class);

    private Repository<SimilarPost> similarPostRepository;
    private Repository<Attachment> attachmentRepository;

    public PostFetcher(Repository<SimilarPost> similarPostRepository,
                       Repository<Attachment> attachmentRepository) {
        this.similarPostRepository = similarPostRepository;
        this.attachmentRepository = attachmentRepository;
    }

    public Observable<Void> synchronizeWithWeb(String postId) {
        return ObservableUtils.create(() -> {
            postRequest.request(postId);

            Post post = postRequest.getPost();
            postRepository.insertOrUpdate(new PostByIdQuery(post.getServerId()), post);

            saveComments(post);
            saveSimilarPosts(post);
            saveAttachments(post.getId());
        });
    }

    private void saveAttachments(int postId) {
        List<Attachment> attachments = postRequest.getAttachments();
        for (Attachment a : attachments)
            a.postId = postId;

        attachmentRepository.deleteWhere(new AttachmentsQuery(postId));
        attachmentRepository.insertAll(attachments);
    }

    private void saveComments(Post post) {
        List<Comment> comments = postRequest.getComments();
        for (Comment c : comments)
            c.postId = post.getId();

        commentRepository.deleteWhere(new CommentsForPostQuery(post.getId()));
        commentRepository.insertAll(comments);
    }

    private void saveSimilarPosts(Post post) {
        List<SimilarPost> posts = postRequest.getSimilarPosts();
        for (SimilarPost s : posts)
            s.setParentPostId(post.getId());

        similarPostRepository.deleteWhere(new SimilarPostQuery(post.getId()));
        similarPostRepository.insertAll(posts);
    }
}