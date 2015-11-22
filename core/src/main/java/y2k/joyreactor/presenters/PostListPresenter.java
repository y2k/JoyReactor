package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.*;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.synchronizers.PostListSynchronizer;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter extends Presenter {

    private View view;
    private StateForTag state;

    private y2k.joyreactor.repository.Repository<Post> repository;
    private PostListSynchronizer.Factory synchronizerFactory;

    public PostListPresenter(View view) {
        this(view, new y2k.joyreactor.repository.Repository<>(Post.class), new PostListSynchronizer.Factory());
    }

    PostListPresenter(View view,
                      y2k.joyreactor.repository.Repository<Post> repository,
                      PostListSynchronizer.Factory synchronizerFactory) {
        this.view = view;
        this.repository = repository;
        this.synchronizerFactory = synchronizerFactory;

        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
        state = new StateForTag(Tag.makeFeatured());
    }

    private void currentTagChanged(Messages.TagSelected m) {
        state = new StateForTag(m.tag);
    }

    public void applyNew() {
        state.applyNew();
    }

    public void loadMore() {
        state.loadMore();
    }

    public void reloadFirstPage() {
        state.reloadFirstPage();
    }

    class StateForTag {

        private PostListSynchronizer synchronizer;

        StateForTag(Tag tag) {
            synchronizer = synchronizerFactory.make(tag);

            view.setBusy(true);
            getFromRepository().subscribe(posts -> view.reloadPosts(posts, null));
            synchronizer.preloadNewPosts()
                    .subscribe(unsafeUpdate -> {
                        view.setHasNewPosts(unsafeUpdate);
                        view.setBusy(false);
                        if (!unsafeUpdate) applyNew();
                    });
        }

        public void applyNew() {
            synchronizer.applyNew()
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.setHasNewPosts(false);
                        view.reloadPosts(posts, synchronizer.getDivider());
                    });
        }

        public void loadMore() {
            view.setBusy(true);
            synchronizer.loadNextPage()
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, synchronizer.getDivider());
                        view.setBusy(false);
                    });
        }

        public void reloadFirstPage() {
            view.setBusy(true);
            synchronizer.reloadFirstPage()
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, posts.size());
                        view.setBusy(false);
                    });
        }

        private Observable<List<Post>> getFromRepository() {
            return repository.queryAsync();
        }
    }

    public void postClicked(Post post) {
        Navigation.getInstance().openPost(post);
    }

    public void playClicked(Post post) {
        if (post.isAnimated()) Navigation.getInstance().openVideo(post);
        else Navigation.getInstance().openImageView(post);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(List<Post> posts, Integer divider);

        void setHasNewPosts(boolean hasNewPosts);
    }
}