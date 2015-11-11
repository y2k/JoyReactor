package y2k.joyreactor.common;

/**
 * Created by y2k on 11/11/15.
 */
public class ObjectUtils {

    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}