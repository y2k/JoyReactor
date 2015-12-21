package y2k.joyreactor.services.repository;

import y2k.joyreactor.Image;
import y2k.joyreactor.SimilarPost;

/**
 * Created by y2k on 01/12/15.
 */
public class SimilarPostQuery extends Repository.Query<SimilarPost> {

    private int postId;

    public SimilarPostQuery(int postId) {
        this.postId = postId;
    }

    @Override
    public boolean compare(SimilarPost row) {
        return row.parentPostId == postId;
    }
}