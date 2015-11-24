package y2k.joyreactor.services.repository;

import y2k.joyreactor.Comment;

/**
 * Created by y2k on 11/22/15.
 */
public class CommentsForPostQuery extends Repository.Query<Comment> {

    private int postId;
    private int parentCommentId;

    public CommentsForPostQuery(int postId) {
        this(postId, -1);
    }

    public CommentsForPostQuery(int postId, int parentCommentId) {
        this.postId = postId;
        this.parentCommentId = parentCommentId;
    }

    @Override
    public boolean compare(Comment comment) {
        if (parentCommentId >= 0 && comment.parentId != parentCommentId)
            return false;
        return comment.postId == postId;
    }
}