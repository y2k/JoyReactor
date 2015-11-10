package y2k.joyreactor.repository;

import y2k.joyreactor.Tag;
import y2k.joyreactor.TagPostMapping;

/**
 * Created by y2k on 11/9/15.
 */
public class TagPostsForTagQuery extends Repository.Query<TagPostMapping.TagPost> {

    private Tag tag;

    public TagPostsForTagQuery(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean compare(TagPostMapping.TagPost row) {
        return row.tagId.equals(tag.getId());
    }
}