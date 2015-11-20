package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.repository.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 11/8/15.
 */
public class PostSubRepositoryForTag {

    private Repository<Post> postRepository = new Repository<>(Post.class);
    private Repository<TagPost> tagPostRepository = new Repository<>(TagPost.class);

    private Tag tag;

    public PostSubRepositoryForTag(Tag tag) {
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
                .flatMap(s -> postRepository.insertAllAsync(posts))
                .map(s -> {
                    List<TagPost> links = new ArrayList<>();
                    for (Post p : posts)
                        links.add(new TagPost(tag.getId(), p.id));
                    return links;
                }).flatMap(tagPostRepository::insertAllAsync);
    }

    public static class TagPost implements Serializable {

        public String tagId;
        public String postId;

        public TagPost(String tagId, String postId) {
            this.tagId = tagId;
            this.postId = postId;
        }
    }
}