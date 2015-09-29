package y2k.joyreactor;

import java.io.File;

/**
 * Created by y2k on 29/09/15.
 */
public abstract class Platform {

    public static Platform Instance;

    public abstract File getCurrentDirectory();
}