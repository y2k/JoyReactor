package y2k.joyreactor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by y2k on 10/18/15.
 */
public class TagImageRequest {

    private static Map<String, String> TAGS = new HashMap<>();

    static {
        TAGS.put("", "");
    }

    public String request(String tag) {
        String id = TAGS.get(tag);
        return id == null ? null : "http://img0.reactor.cc/pics/avatar/tag/" + id;
    }
}