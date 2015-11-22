package y2k.joyreactor;

/**
 * Created by y2k on 28/09/15.
 */
public class Comment {

    public int postId;

    public String text;
    public Image userAvatar;

    public int id;
    public int parentId;

    public int childCount;
    public float rating;

    public UserImage getUserImage() {
        return new UserImage(userAvatar);
    }
}