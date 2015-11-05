package y2k.joyreactor.common;

import y2k.joyreactor.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 03/11/15.
 */
public class PostGenerator {

    public static final int PAGE_SIZE = 10;

    public static List<Post> getPageRange(int startPageId, int pageCount) {
        List<Post> result = new ArrayList<>();
        for (int i = 0; i < PAGE_SIZE * pageCount; i++)
            result.add(makePost("" + (PAGE_SIZE * startPageId + i)));
        return result;
    }

    private static Post makePost(String id) {
        Post p = new Post();
        p.id = id;
        return p;
    }
}