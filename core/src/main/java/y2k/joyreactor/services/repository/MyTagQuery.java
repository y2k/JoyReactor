package y2k.joyreactor.services.repository;

import y2k.joyreactor.Tag;

/**
 * Created by y2k on 11/25/15.
 */
@Deprecated
public class MyTagQuery extends Repository.Query<Tag> {

    @Override
    public boolean compare(Tag tag) {
        return tag.isMine();
    }
}