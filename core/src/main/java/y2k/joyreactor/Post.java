package y2k.joyreactor;

import java.util.Date;

/**
 * Created by y2k on 9/27/15.
 */
public class Post {

    public String title;
    public String image;

    public int width;
    public int height;

    public String userImage;
    public String userName;

    public Date created;
    public String id;

    public int commentCount;
    public float rating;

    public boolean isAnimated() {
        return image != null && image.endsWith(".gif");
    }

    public float getAspect() {
        return getAspect(1);
    }

    public float getAspect(float min) {
        float aspect = height == 0 ? 1 : (float) width / height;
        return Math.min(2, Math.max(min, aspect));
    }
}