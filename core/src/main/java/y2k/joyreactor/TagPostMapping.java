package y2k.joyreactor;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/8/15.
 */
public class TagPostMapping {

    private y2k.joyreactor.repository.Repository<Post> postRepository = new y2k.joyreactor.repository.Repository<>(Post.class);
    private y2k.joyreactor.repository.Repository<TagPost> tagPostRepository = new y2k.joyreactor.repository.Repository<>(TagPost.class);

    private Tag tag;

    public TagPostMapping(Tag tag) {
        this.tag = tag;
    }

    public Observable<Void> clearAsync() {
        return postRepository
                .deleteWhere(new y2k.joyreactor.repository.PostsForTagQuery(tag))
                .flatMap(s -> tagPostRepository.deleteWhere(new y2k.joyreactor.repository.TagPostsForTagQuery(tag)));
    }

    public Observable<List<Post>> queryAsync() {
        return postRepository.queryAsync(new y2k.joyreactor.repository.PostsForTagQuery(tag));
    }

    public Observable<Void> replaceAllAsync(List<Post> posts) {
        return clearAsync()
                .flatMap(s -> postRepository.insertAll(posts))
                .map(s -> {
                    List<TagPost> links = new ArrayList<>();
                    for (Post p : posts)
                        links.add(new TagPost(tag.getId(), p.id));
                    return links;
                }).flatMap(tagPostRepository::insertAll);
    }

    public static class TagPost {

        public String tagId;
        public String postId;

        public TagPost(String tagId, String postId) {
            this.tagId = tagId;
            this.postId = postId;
        }
    }
}