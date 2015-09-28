package y2k.joyreactor;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 28/09/15.
 */
public class PostPresenter {

    View view;

    public PostPresenter(View view) {
        this.view = view;
        loadComments();
        loadPost();
    }

    private void loadComments() {
        ForegroundScheduler.getInstance().createWorker().schedule(() -> {
            Comment.Collection comments = new Comment.Collection();
            for (int i = 1; i <= 100; i++) {
                Comment c = new Comment();
                c.text = "Comment text " + i;
                comments.add(c);
            }
            view.updateComments(comments);
        }, 1, TimeUnit.SECONDS);
    }

    private void loadPost() {
        ForegroundScheduler.getInstance().createWorker().schedule(() -> {
            Post post = new Post();
            post.image = "http://img0.joyreactor.cc/pics/post/-2455736.jpeg";
            post.width = 811;
            post.height = 573;

            view.updatePostImage(post);
        }, 2, TimeUnit.SECONDS);
    }

    public interface View {

        void updateComments(Comment.Collection comments);

        void updatePostImage(Post post);
    }
}