package y2k.joyreactor;

/**
 * Created by y2k on 11/9/15.
 */
public class PostsForTagQuery implements Repository.Query<Post> {

    public PostsForTagQuery(Tag tag) {
        // TODO:
    }

    @Override
    public boolean compare(Post row) {
        // TODO:
        return false;
    }
}