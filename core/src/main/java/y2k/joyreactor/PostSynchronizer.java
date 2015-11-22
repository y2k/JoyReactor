package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.repository.CommentsForPostQuery;
import y2k.joyreactor.repository.Repository;
import y2k.joyreactor.requests.PostRequest;

import java.util.List;

/**
 * Created by y2k on 11/21/15.
 */
public class PostSynchronizer {

    private PostRequest postRequest = new PostRequest();

    private Repository<Post> postRepository = new Repository<>(Post.class);
    private Repository<Comment> commentRepository = new Repository<>(Comment.class);

    public Observable<Void> synchronizeWithWeb(String postId) {
        return ObservableUtils.create(() -> {
            postRequest.request(postId);

            Post post = postRequest.getPost();
            postRepository.insertOrUpdate(post);

            List<Comment> comments = postRequest.getComments();
            for (Comment c : comments)
                c.postId = post.id;

            commentRepository.deleteWhere(new CommentsForPostQuery(post.id));
            commentRepository.insertAll(comments);

        });
    }

    private Observable<Post> parse(Document doc) {
        return null;
    }
}