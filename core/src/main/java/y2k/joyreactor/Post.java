package y2k.joyreactor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by y2k on 9/27/15.
 */
public class Post implements Serializable, Comparable<Post> {

    public int id;

    public String title;

    public Image image;

    public UserImage getUserImage() {
        return UserImage.fromUrl(userImage);
    }

    public String userImage;

    public String userName;

    public Date created;
    public String serverId;

    public int commentCount;
    public float rating;

    @Override
    public int compareTo(Post post) {
        return id - post.id;
    }
}