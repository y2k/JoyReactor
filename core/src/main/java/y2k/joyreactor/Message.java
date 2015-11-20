package y2k.joyreactor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by y2k on 10/1/15.
 */
public class Message implements Serializable {

    public String text;
    public Date date;
    public boolean isMine;

    public String userName;
    public String userImage;

    public UserImage getUserImage() {
        return userImage == null ? new UserImage() : new UserImage(new Image(userImage));
    }
}