package y2k.joyreactor.services.repository;

import y2k.joyreactor.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by y2k on 30/11/15.
 */
public class TopCommentsQuery extends Repository.Query<Comment> {

    private int postId;
    private int maxCount;

    public TopCommentsQuery(int postId, int maxCount) {
        this.postId = postId;
        this.maxCount = maxCount;
    }

    @Override
    public boolean compare(Comment comment) {
        if (comment.postId != postId) return false;
        if (comment.parentId != 0) return false;
        return true;
    }

    @Override
    public void sort(List<Comment> items) {
        Collections.sort(items, (l, r) -> Float.compare(r.rating, l.rating));
    }

    @Override
    public List<Comment> haven(List<Comment> items) {
        return items.size() <= maxCount
                ? items
                : new ArrayList<>(items.subList(0, maxCount));
    }
}