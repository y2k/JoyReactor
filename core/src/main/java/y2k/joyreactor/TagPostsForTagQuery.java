package y2k.joyreactor;

/**
 * Created by y2k on 11/9/15.
 */
public class TagPostsForTagQuery implements Repository.Query<TagPostMapping.TagPost> {

    public TagPostsForTagQuery(Tag tag) {
        // TODO
    }

    @Override
    public boolean compare(TagPostMapping.TagPost row) {
        // TODO
        return false;
    }
}