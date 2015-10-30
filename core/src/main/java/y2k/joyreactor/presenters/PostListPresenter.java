package y2k.joyreactor.presenters;

import y2k.joyreactor.*;
import y2k.joyreactor.common.Messages;
import y2k.joyreactor.requests.PostsForTagRequest;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListPresenter extends Presenter {

    //    private PostListService service;
    private View view;

    public PostListPresenter(View view) {
        this.view = view;

        getMessages().add(this::currentTagChanged, Messages.TagSelected.class);
//        reloadPosts();
//        loadMore();

        view.setBusy(true);
        Repository<Post> repository = new Repository<>("posts", 1);
        repository
                .getAllAsync()
                .subscribe(posts -> {
                    view.reloadPosts(posts);
                    PostsForTagRequest req = new PostsForTagRequest(null, null);
                    req.requestAsync()
                            .subscribe(ignore -> {
                                new PostMerger(repository).merge(req.posts);
                                repository
                                        .getAllAsync()
                                        .subscribe(posts2 -> {
                                            view.reloadPosts(posts2);
                                            view.setBusy(false);
                                        });
                            });
                });
    }

    private void currentTagChanged(Messages.TagSelected m) {
//        service.setCurrentTag(m.tag);
//        service = new PostListService(m.tag);

        view.reloadPosts(null);
        loadMore();
    }

    public void loadMore() {
        view.setBusy(true);
//        service.loadNextPageAsync()
//                .subscribe(s -> reloadPosts());
    }

    public void reloadFirstPage() {
//        service.reset().subscribe(s -> loadMore());
//        service = new PostListService(service.getTag());
    }

    public void postClicked(Post post) {
        Navigation.getInstance().openPost(post);
    }

//    private void reloadPosts() {
//        view.setBusy(true);
//        service.getList()
//                .subscribe(data -> {
//                    view.reloadPosts(data);
//                    view.setBusy(false);
//                }, Throwable::printStackTrace);
//    }

    public void playClicked(Post post) {
        if (post.isAnimated()) Navigation.getInstance().openVideo(post);
        else Navigation.getInstance().openImageView(post);
    }

    public interface View {

        void setBusy(boolean isBusy);

        void reloadPosts(List<Post> posts);
    }
}