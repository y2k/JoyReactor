package y2k.joyreactor.common;

import y2k.joyreactor.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 03/11/15.
 */
public class PostGenerator {

    public static List<Post> getMockFirstPage(int startIndex) {
        List<Post> result = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            result.add(makePost("" + (startIndex + i)));
        return result;
    }

    private static Post makePost(String id) {
        Post p = new Post();
        p.id = id;
        return p;
    }
}