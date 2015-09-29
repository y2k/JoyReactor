package y2k.joyreactor;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter {

    public PostListPresenter(View view) {
        view.setBusy(true);

        Post.Collection
                .request()
                .subscribe(data -> {
                    view.reloadPosts(data);
                    view.setBusy(false);
                }, Throwable::printStackTrace);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(Post.Collection posts);
    }
}