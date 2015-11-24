package y2k.joyreactor.services.synchronizers;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPost;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.repository.TagPostsForTagQuery;
import y2k.joyreactor.services.requests.PostsForTagRequest;

/**
 * Created by y2k on 03/11/15.
 */
public class PostListSynchronizer {

    private PostMerger merger;
    private PostsForTagRequest.Factory requestFactory;

    private PostsForTagRequest request;
    private Tag tag;
    private Repository<TagPost> tagPostRepository;

    PostListSynchronizer(Tag tag,
                         PostsForTagRequest.Factory requestFactory,
                         Repository<Post> postRepository,
                         Repository<TagPost> tagPostRepository) {
        this.tag = tag;
        this.tagPostRepository = tagPostRepository;
        this.merger = new PostMerger(tag, postRepository, tagPostRepository);
        this.requestFactory = requestFactory;
    }

    public Observable<Boolean> preloadNewPosts() {
        request = getPostsForTagRequest(null);
        return request
                .requestAsync()
                .flatMap(s -> merger.isUnsafeUpdate(request.getPosts()));
    }

    public Observable<Void> applyNew() {
        return merger.mergeFirstPage(request.getPosts());
    }

    public Integer getDivider() {
        return merger.getDivider();
    }

    public Observable<Void> loadNextPage() {
        request = getPostsForTagRequest(request.getNextPageId());
        return request
                .requestAsync()
                .flatMap(s -> merger.mergeNextPage(request.getPosts()));
    }

    public Observable<Void> reloadFirstPage() {
        request = getPostsForTagRequest(null);
        return request
                .requestAsync()
                .flatMap(_void -> tagPostRepository.deleteWhereAsync(new TagPostsForTagQuery(tag)))
                .flatMap(_void -> merger.mergeFirstPage(request.getPosts()));
    }

    private PostsForTagRequest getPostsForTagRequest(String pageId) {
        return requestFactory.make(tag, pageId);
    }

    public static class Factory {

        private PostsForTagRequest.Factory requestFactory = new PostsForTagRequest.Factory();

        private Repository<Post> postRepository;
        private Repository<TagPost> tagPostRepository;

        public Factory(Repository<Post> postRepository, Repository<TagPost> tagPostRepository) {
            this.postRepository = postRepository;
            this.tagPostRepository = tagPostRepository;
        }

        public PostListSynchronizer make(Tag tag) {
            return new PostListSynchronizer(tag, requestFactory, postRepository, tagPostRepository);
        }
    }
}