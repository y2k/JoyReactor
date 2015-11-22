package y2k.joyreactor;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by y2k on 9/27/15.
 */
public class Post implements Serializable {

    public int id;

    public String title;
    public Image image;

    public Image userImage;
    public String userName;

    public Date created;
    public String serverId;

    public int commentCount;
    public float rating;

    @Deprecated
    public boolean isAnimated() {
        return image != null && image.isAnimated();
    }

    @Deprecated
    public float getAspect() {
        return image == null ? 1 : image.getAspect(1);
    }

    @Deprecated
    public float getAspect(float min) {
        return image == null ? min : image.getAspect(min);
    }
}