package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 28/09/15.
 */
public class Comment implements Serializable {

    public int postId;

    public String text;
    public String userImage;

    public int id;
    public int parentId;

    public int childCount;
    public float rating;

    public UserImage getUserImage() {
        return userImage == null ? new UserImage() : new UserImage(new Image(userImage));
    }
}