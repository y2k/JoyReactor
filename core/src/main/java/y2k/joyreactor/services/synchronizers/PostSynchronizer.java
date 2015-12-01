package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Comment;
import y2k.joyreactor.Post;
import y2k.joyreactor.SimilarPost;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.services.repository.CommentsForPostQuery;
import y2k.joyreactor.services.repository.PostByIdQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.repository.SimilarPostQuery;
import y2k.joyreactor.services.requests.PostRequest;

import java.util.List;

/**
 * Created by y2k on 11/21/15.
 */
public class PostSynchronizer {

    private PostRequest postRequest = new PostRequest();

    private Repository<Post> postRepository = new Repository<>(Post.class);
    private Repository<Comment> commentRepository = new Repository<>(Comment.class);

    private Repository<SimilarPost> similarPostRepository;

    public PostSynchronizer(Repository<SimilarPost> similarPostRepository) {
        this.similarPostRepository = similarPostRepository;
    }

    public Observable<Void> synchronizeWithWeb(String postId) {
        return ObservableUtils.create(() -> {
            postRequest.request(postId);

            Post post = postRequest.getPost();
            postRepository.insertOrUpdate(new PostByIdQuery(post.serverId), post);

            saveComments(post);
            saveSimilarPosts(post);
        });
    }

    private void saveComments(Post post) {
        List<Comment> comments = postRequest.getComments();
        for (Comment c : comments)
            c.postId = post.id;

        commentRepository.deleteWhere(new CommentsForPostQuery(post.id));
        commentRepository.insertAll(comments);
    }

    private void saveSimilarPosts(Post post) {
        List<SimilarPost> posts = postRequest.getSimilarPosts();
        for (SimilarPost s : posts)
            s.parentPostId = post.id;

        similarPostRepository.deleteWhere(new SimilarPostQuery(post.id));
        similarPostRepository.insertAll(posts);
    }
}