package y2k.joyreactor.repository;

import y2k.joyreactor.Comment;

/**
 * Created by y2k on 11/22/15.
 */
public class CommentsForPostQuery extends Repository.Query<Comment> {

    public CommentsForPostQuery(int postId) {
        // FIXME:
    }

    public CommentsForPostQuery(int postId, int parentCommentId) {
        // FIXME:
    }

    @Override
    public boolean compare(Comment comment) {
        return false;
    }
}