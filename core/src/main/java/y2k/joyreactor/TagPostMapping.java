package y2k.joyreactor;

import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/8/15.
 */
public class TagPostMapping {

    private Repository<Post> postRepository = new Repository<>(Post.class);
    private Repository<TagPost> tagPostRepository = new Repository<>(TagPost.class);

    private Tag tag;

    public TagPostMapping(Tag tag) {
        this.tag = tag;
    }

    public Observable<Void> clearAsync() {
        return postRepository
                .deleteWhere(new PostsForTagQuery(tag))
                .flatMap(s -> tagPostRepository.deleteWhere(new TagPostsForTagQuery(tag)));
    }

    public Observable<List<Post>> queryAsync() {
        return postRepository.queryAsync(new PostsForTagQuery(tag));
    }

    public Observable<Void> replaceAllAsync(List<Post> posts) {
        return clearAsync()
                .flatMap(s -> postRepository.insertOrUpdate(posts))
                .map(s -> {
                    List<TagPost> links = new ArrayList<>();
                    for (Post p : posts)
                        links.add(new TagPost(tag.getId(), p.id));
                    return links;
                }).flatMap(tagPostRepository::insertOrUpdate);
    }

    static class TagPost {

        public String tagId;
        public String postId;

        public TagPost(String tagId, String postId) {
            this.tagId = tagId;
            this.postId = postId;
        }
    }
}