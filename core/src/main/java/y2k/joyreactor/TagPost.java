package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 11/24/15.
 */
public class TagPost implements Serializable {

    public int tagId;
    public int postId;

    public TagPost(int tagId, int postId) {
        this.tagId = tagId;
        this.postId = postId;
    }
}