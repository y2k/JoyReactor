package y2k.joyreactor.services.repository;

import rx.Observable;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPost;

import java.util.List;

/**
 * Created by y2k on 11/9/15.
 */
public class PostsForTagQuery extends Repository.Query<Post> {

    private Tag tag;
    private List<TagPost> links;

    public PostsForTagQuery(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean compare(Post row) {
        for (TagPost s : links)
            if (s.postId == row.id)
                return true;
        return false;
    }

    @Override
    public Observable<Void> initialize() {
        return new Repository<>(TagPost.class)
                .queryAsync(new TagPostsForTagQuery(tag))
                .map(links -> {
                    this.links = links;
                    return null;
                });
    }
}