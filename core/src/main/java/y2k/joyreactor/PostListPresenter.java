package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter {

    private View view;

    public PostListPresenter(View view) {
        this.view = view;

        view.setBusy(true);
        Post.Collection
                .request()
                .subscribe(data -> {
                    view.reloadPosts(data);
                    view.setBusy(false);
                }, Throwable::printStackTrace);
    }

    public void loadMore() {
        // TODO:
        view.reloadPosts(null);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(List<Post> posts);
    }
}