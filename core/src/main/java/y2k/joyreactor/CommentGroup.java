package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 11/28/15.
 */
public class CommentGroup {

    private final Comment parent;
    private final List<Comment> children;

    public CommentGroup(List<Comment> children) {
        this(null, children);
    }

    public CommentGroup(Comment parent, List<Comment> children) {
        this.parent = parent;
        this.children = children;
    }

    public Comment get(int position) {
        if (parent == null) return children.get(position);
        if (position == 0) return parent;
        return children.get(position - 1);
    }

    public int size() {
        return children.size() + (parent == null ? 0 : 1);
    }

    public boolean isChild(int position) { // is need divider
        return parent != null && position > 0;
    }

    public int getId(int position) {
        if (parent == null) return children.get(position).id;
        if (position == 0) return parent.parentId;
        return children.get(position - 1).id;
    }
}