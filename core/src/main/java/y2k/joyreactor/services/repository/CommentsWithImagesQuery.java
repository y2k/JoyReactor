package y2k.joyreactor.services.repository;

import y2k.joyreactor.Comment;

/**
 * Created by y2k on 30/11/15.
 */
@Deprecated
public class CommentsWithImagesQuery extends Repository.Query<Comment> {

    private int postId;
    private int maxCount;
    private int count;

    public CommentsWithImagesQuery(int postId, int maxCount) {
        this.postId = postId;
        this.maxCount = maxCount;
    }

    @Override
    public boolean compare(Comment comment) {
        if (count >= maxCount) return false;
        if (comment.postId != postId) return false;
        if (comment.getAttachment() == null) return false;

        count++;
        return true;
    }
}