package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 11/24/15.
 */
public class TagPost implements Serializable {

    public String tagId;
    public String postId;

    public TagPost(String tagId, String postId) {
        this.tagId = tagId;
        this.postId = postId;
    }
}