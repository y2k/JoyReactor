package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.Navigation;
import y2k.joyreactor.Post;
import y2k.joyreactor.PostMerger;
import y2k.joyreactor.Repository;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter extends Presenter {

    private View view;
    private StateForTag state;

    private Repository<Post> repository;
    private PostsForTagRequest.Factory requestFactory;
    private PostMerger.Fabric mergerFabric;

    public PostListPresenter(View view) {
        this(view,
                new Repository<>(Post.class),
                new PostsForTagRequest.Factory(),
                new PostMerger.Fabric());
    }

    public PostListPresenter(View view,
                             Repository<Post> repository,
                             PostsForTagRequest.Factory requestFactory,
                             PostMerger.Fabric mergerFabric) {
        this.view = view;
        this.repository = repository;
        this.requestFactory = requestFactory;
        this.mergerFabric = mergerFabric;

        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
        state = new StateForTag();
    }

    private void currentTagChanged(Messages.TagSelected m) {
        state = new StateForTag();
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

        private PostMerger merger;
        private PostsForTagRequest request;

        StateForTag() {
            merger = mergerFabric.make(repository);

            view.setBusy(true);
            getFromRepository().subscribe(posts -> view.reloadPosts(posts, null));
            request = getPostsForTagRequest(null);
            request.requestAsync()
                    .flatMap(s -> merger.isUnsafeUpdate(request.getPosts()))
                    .subscribe(unsafeUpdate -> {
                        view.setHasNewPosts(unsafeUpdate);
                        view.setBusy(false);
                        if (!unsafeUpdate) applyNew();
                    });
        }

        public void applyNew() {
            merger.mergeFirstPage(request.getPosts())
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.setHasNewPosts(false);
                        view.reloadPosts(posts, merger.getDivider());
                    });
        }

        public void loadMore() {
            view.setBusy(true);
            request = getPostsForTagRequest(request.getNextPageId());
            request.requestAsync()
                    .flatMap(s -> merger.mergeNextPage(request.getPosts()))
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, merger.getDivider());
                        view.setBusy(false);
                    });
        }

        public void reloadFirstPage() {
            view.setBusy(true);
            request = getPostsForTagRequest(null);
            request.requestAsync()
                    .flatMap(s -> repository.clearAsync())
                    .flatMap(s -> merger.mergeFirstPage(request.getPosts()))
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, posts.size());
                        view.setBusy(false);
                    });
        }

        private PostsForTagRequest getPostsForTagRequest(String pageId) {
            return requestFactory.make(null, pageId);
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