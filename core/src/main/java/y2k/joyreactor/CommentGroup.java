package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 07/12/15.
 */
public interface CommentGroup {

    Comment get(int position);

    int size();

    boolean isChild(int position);

    long getId(int position);

    /**
     * Created by y2k on 11/28/15.
     */
    class OneLevel implements CommentGroup {

        private Comment parent;
        private List<Comment> children;

        public OneLevel(List<Comment> children) {
            this(null, children);
        }

        public OneLevel(Comment parent, List<Comment> children) {
            this.parent = parent;
            this.children = children;
        }

        @Override
        public Comment get(int position) {
            if (parent == null) return children.get(position);
            if (position == 0) return parent;
            return children.get(position - 1);
        }

        @Override
        public int size() {
            return children.size() + (parent == null ? 0 : 1);
        }

        @Override
        public boolean isChild(int position) { // is need divider
            return parent != null && position > 0;
        }

        @Override
        public long getId(int position) {
            if (parent == null) return children.get(position).id;
            if (position == 0) return parent.parentId;
            return children.get(position - 1).id;
        }
    }

    class TwoLevel implements CommentGroup {

        private List<Comment> comments;

        public TwoLevel(List<Comment> comments) {
            this.comments = comments;
        }

        @Override
        public boolean isChild(int position) {
            return comments.get(position).parentId != 0;
        }

        @Override
        public Comment get(int position) {
            return comments.get(position);
        }

        @Override
        public int size() {
            return comments.size();
        }

        @Override
        public long getId(int position) {
            return comments.get(position).id;
        }
    }
}