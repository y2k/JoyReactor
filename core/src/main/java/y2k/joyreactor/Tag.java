package y2k.joyreactor;

/**
 * Created by y2k on 9/26/15.
 */
public class Tag {

    public String title;
    public String image;

    public String getId() {
        return title.toLowerCase();
    }
}