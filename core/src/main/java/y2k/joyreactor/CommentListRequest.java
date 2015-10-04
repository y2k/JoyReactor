package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/29/15.
 */
public class CommentListRequest {

    public final Comment.Collection comments = new Comment.Collection();

    private final int postId;

    public CommentListRequest(int postId) {
        this.postId = postId;
    }

    public void populate() throws IOException {
        Document doc = new HttpClient().getDocument("http://anime.reactor.cc/post/" + postId);

        for (Element node : doc.select("div.comment")) {
            Comment comment = new Comment();
            comment.text = node.select("div.txt > div").first().text();
            comment.userAvatar = node.select("img.avatar").attr("src");
            comment.id = Integer.parseInt(node.select("span.comment_rating").attr("comment_id"));
            comment.rating = Float.parseFloat(node.select("span.comment_rating").text().trim());

            Element parent = node.parent();
            if ("comment_list".equals(parent.className()))
                comment.parentId = NumberExtractor.get(parent.id());

            comments.add(comment);
        }
    }

    static class NumberExtractor {

        static final Pattern NUMBER = Pattern.compile("\\d+");

        public static int get(String text) {
            Matcher m = NUMBER.matcher(text);
            return m.find() ? Integer.parseInt(m.group()) : 0;
        }
    }
}