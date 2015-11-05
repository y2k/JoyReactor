package y2k.joyreactor.common;

import y2k.joyreactor.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 03/11/15.
 */
public class PostGenerator {

    @Deprecated
    public static List<Post> getPage(int startIndex) {
        return getPages(startIndex, 1);
    }

    @Deprecated
    public static List<Post> getPages(int startIndex, int pageCount) {
        List<Post> result = new ArrayList<>();
        for (int i = 0; i < 10 * pageCount; i++)
            result.add(makePost("" + (startIndex + i)));
        return result;
    }

    public static List<Post> getPageRange(int startPageId, int pageCount) {
        List<Post> result = new ArrayList<>();
        for (int i = 0; i < 10 * pageCount; i++)
            result.add(makePost("" + (10 * startPageId + i)));
        return result;
    }

    private static Post makePost(String id) {
        Post p = new Post();
        p.id = id;
        return p;
    }
}