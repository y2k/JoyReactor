package y2k.joyreactor.services.repository;

import y2k.joyreactor.Comment;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by y2k on 07/12/15.
 */
public class TwoLeverCommentQuery extends Repository.Query<Comment> {

    private Set<Integer> firstLevelComments = new HashSet<>();
    private int postId;

    public TwoLeverCommentQuery(int postId) {
        this.postId = postId;
    }

    @Override
    public boolean compare(Comment comment) {
        if (comment.postId != postId)
            return false;
        if (comment.parentId == 0) {
            firstLevelComments.add(comment.id);
            return true;
        }
        return firstLevelComments.contains(comment.parentId);
    }
}