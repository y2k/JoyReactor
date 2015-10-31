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
    private TagState tagState;

    public PostListPresenter(View view) {
        this.view = view;
        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
        tagState = new TagState();
    }

    private void currentTagChanged(Messages.TagSelected m) {
        tagState = new TagState();
    }

    public void applyNew() {
        tagState.applyNew();
    }

    public void loadMore() {
        tagState.loadMore();
    }

    public void reloadFirstPage() {
        tagState.reloadFirstPage();
    }

    class TagState {

        private PostsForTagRequest request;
        private PostMerger merger;

        void State() {
            getFromRepository().subscribe(posts -> view.reloadPosts(posts, posts.size()));

            view.setBusy(true);
            request = getPostsForTagRequest();
            request.requestAsync()
                    .map(s -> merger.hasNew(request.posts))
                    .subscribe((hasNewPosts) -> {
                        view.setHasNewPosts(hasNewPosts);
                        view.setBusy(false);
                    });
        }

        public void applyNew() {
            merger.mergeFirstPage(request.posts)
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.setHasNewPosts(false);
                        view.reloadPosts(posts, merger.getDivider());
                    });
        }

        public void loadMore() {
            view.setBusy(true);

            request = getPostsForTagRequest();
            request.requestAsync()
                    .flatMap(s -> merger.mergeNextPage(request.posts))
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, merger.getDivider());
                        view.setBusy(false);
                    });
        }

        public void reloadFirstPage() {
            view.setBusy(true);
            request = getPostsForTagRequest();
            request.requestAsync()
                    .flatMap(s -> getRepository().clearAsync())
                    .flatMap(s -> merger.mergeFirstPage(request.posts))
                    .flatMap(s -> getFromRepository())
                    .subscribe(posts -> {
                        view.reloadPosts(posts, posts.size());
                        view.setBusy(false);
                    });
        }

        private PostsForTagRequest getPostsForTagRequest() {
            return new PostsForTagRequest(null, null);
        }

        private Observable<List<Post>> getFromRepository() {
            return getRepository().queryAsync();
        }

        private Repository<Post> getRepository() {
            return new Repository<>("posts", 1);
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

        void reloadPosts(List<Post> posts, int divider);

        void setHasNewPosts(boolean hasNewPosts);
    }
}