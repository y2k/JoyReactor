package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.*;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.platform.Navigation;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.TagService;
import y2k.joyreactor.services.synchronizers.PostListSynchronizer;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter extends Presenter {

    private View view;
    private TagService serviceFactory;
    private TagService service;

    public PostListPresenter(View view) {
        this(view, new TagService(new Repository<>(Post.class),
                new PostListSynchronizer.Factory(new Repository<>(Post.class), new Repository<>(TagPost.class))));
    }

    @Deprecated
    PostListPresenter(View view,
                      Repository<Post> repository,
                      PostListSynchronizer.Factory synchronizerFactory) {
        this(view, new TagService(repository, synchronizerFactory));
    }

    PostListPresenter(View view, TagService serviceFactory) {
        this.view = view;
        this.serviceFactory = serviceFactory;

        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
        currentTagChanged(new Messages.TagSelected(Tag.makeFeatured()));
    }

    private void currentTagChanged(Messages.TagSelected m) {
        service = serviceFactory.make(m.tag);

        view.setBusy(true);
        getFromRepository().subscribe(posts -> view.reloadPosts(posts, null));
        service.preloadNewPosts()
                .subscribe(unsafeUpdate -> {
                    view.setHasNewPosts(unsafeUpdate);
                    view.setBusy(false);
                    if (!unsafeUpdate) applyNew();
                });
    }

    public void applyNew() {
        service.applyNew()
                .subscribe(posts -> {
                    view.setHasNewPosts(false);
                    view.reloadPosts(posts, service.getDivider());
                });
    }

    public void loadMore() {
        view.setBusy(true);
        service.loadNextPage()
                .subscribe(posts -> {
                    view.reloadPosts(posts, service.getDivider());
                    view.setBusy(false);
                });
    }

    public void reloadFirstPage() {
        view.setBusy(true);
        service.reloadFirstPage()
                .subscribe(posts -> {
                    view.reloadPosts(posts, posts.size());
                    view.setBusy(false);
                });
    }

    private Observable<List<Post>> getFromRepository() {
        return service.queryAsync();
    }

    public void postClicked(Post post) {
        Navigation.getInstance().openPost(post.serverId);
    }

    public void playClicked(Post post) {
        if (post.image.isAnimated()) Navigation.getInstance().openVideo(post.serverId);
        else Navigation.getInstance().openImageView(post);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(List<Post> posts, Integer divider);

        void setHasNewPosts(boolean hasNewPosts);
    }
}