package y2k.joyreactor;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter {

    public PostListPresenter(View view) {
        view.setBusy(true);

        new PostLoader().get().subscribe(data -> {
            view.reloadPosts(data);
            view.setBusy(false);
        });
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(PostLoader.PostCollection posts);
    }
}
