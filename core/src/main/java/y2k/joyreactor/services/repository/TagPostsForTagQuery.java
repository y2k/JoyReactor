package y2k.joyreactor.services.repository;

import y2k.joyreactor.Tag;
import y2k.joyreactor.common.ObjectUtils;

/**
 * Created by y2k on 11/9/15.
 */
public class TagPostsForTagQuery extends Repository.Query<PostSubRepositoryForTag.TagPost> {

    private Tag tag;

    public TagPostsForTagQuery(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean compare(PostSubRepositoryForTag.TagPost row) {
        return ObjectUtils.equals(row.tagId, tag.getId());
    }
}