package y2k.joyreactor;

import java.util.ArrayList;

/**
 * Created by y2k on 9/26/15.
 */
public class Tag {

    public String title;
    public String image;

    public String getId() {
        return title.toLowerCase();
    }

    public static class Collection extends ArrayList<Tag> {
    }
}