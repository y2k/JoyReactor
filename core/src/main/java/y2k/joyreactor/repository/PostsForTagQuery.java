package y2k.joyreactor.repository;

import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;

/**
 * Created by y2k on 11/9/15.
 */
public class PostsForTagQuery implements Repository.Query<Post> {

    private Tag tag;

    public PostsForTagQuery(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean compare(Post row) {
        throw new UnsupportedOperationException();
    }
}