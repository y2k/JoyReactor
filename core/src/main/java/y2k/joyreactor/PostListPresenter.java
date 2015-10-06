package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter {

    private PostListService service = new PostListService();
    private View view;

    public PostListPresenter(View view) {
        this.view = view;
        loadMore();
    }

    public void loadMore() {
        view.setBusy(true);
        service.loadNextPageAsync()
                .subscribe(s -> reloadPosts());
    }

    private void reloadPosts() {
        view.setBusy(true);
        service.getList()
                .subscribe(data -> {
                    view.reloadPosts(data);
                    view.setBusy(false);
                }, Throwable::printStackTrace);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(List<Post> posts);
    }
}