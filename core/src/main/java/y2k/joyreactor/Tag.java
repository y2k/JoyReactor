package y2k.joyreactor;

import java.io.Serializable;

/**
 * Created by y2k on 9/26/15.
 */
public class Tag implements Serializable {

    public String title;
    public String image;

    public String getId() {
        return title.toLowerCase();
    }
}