package y2k.joyreactor.presenters;

import y2k.joyreactor.*;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter extends Presenter {

    private PostListService service = new PostListService();
    private View view;

    public PostListPresenter(View view) {
        this.view = view;

        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
        loadMore();
    }

    private void currentTagChanged(Messages.TagSelected m) {
        service.setCurrentTag(m.tag);
        view.reloadPosts(null);
        loadMore();
    }

    public void loadMore() {
        view.setBusy(true);
        service.loadNextPageAsync()
                .subscribe(s -> reloadPosts());
    }

    public void postClicked(Post post) {
        Navigation.getInstance().openPost(post);
    }

    public void reloadFirstPage() {
        service.reset().subscribe(s -> loadMore());
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