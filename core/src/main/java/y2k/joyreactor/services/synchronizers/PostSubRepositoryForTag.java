//package y2k.joyreactor.services.synchronizers;
//
//import rx.Observable;
//import y2k.joyreactor.Post;
//import y2k.joyreactor.Tag;
//import y2k.joyreactor.TagPost;
//import y2k.joyreactor.services.repository.PostsForTagQuery;
//import y2k.joyreactor.services.repository.Repository;
//
//import java.util.List;
//
///**
// * Created by y2k on 11/8/15.
// */
//class PostSubRepositoryForTag {
//
//    private Repository<Post> postRepository = new Repository<>(Post.class);
//    private Repository<TagPost> tagPostRepository = new Repository<>(TagPost.class);
//
//    private Tag tag;
//
//    public PostSubRepositoryForTag(Tag tag) {
//        this.tag = tag;
//    }
//
//    public Observable<Void> clearAsync() {
//        return postRepository
//                .deleteWhereAsync(new y2k.joyreactor.services.repository.PostsForTagQuery(tag))
//                .flatMap(s -> tagPostRepository.deleteWhereAsync(new y2k.joyreactor.services.repository.TagPostsForTagQuery(tag)));
//    }
//
//    public Observable<List<Post>> queryAsync() {
//        return postRepository.queryAsync(new PostsForTagQuery(tag));
//    }
//}