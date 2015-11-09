package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 9/26/15.
 */
public class Tag implements Serializable {

    static final String MARK_USERNAME = "username:";

    public String title;
    public Image image;

    public String getId() {
        return title == null ? null : title.toLowerCase();
    }

    public String getUsername() {
        return title.substring(MARK_USERNAME.length());
    }

    public boolean isFavorite() {
        return title != null && title.startsWith(MARK_USERNAME);
    }

    public static Tag makeFavorite(String username) {
        return new Tag() {
            {
                title = MARK_USERNAME + username;
            }
        };
    }

    public static Tag makeFeatured() {
        return new Tag();
    }
}