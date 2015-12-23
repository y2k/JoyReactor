package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 28/09/15.
 */
public class Comment implements Serializable {

    public long postId;

    public String text;
    public String userImage;

    public long id;
    public long parentId;

    public int replies;
    public float rating;

    public UserImage getUserImage() {
        return userImage == null ? new UserImage() : new UserImage(userImage);
    }

    public Image getAttachment() {
        return attachment;
    }

    public void setAttachment(String url, int width, int height) {
        attachment = new Image(url, width, height);
    }

    private Image attachment;
}