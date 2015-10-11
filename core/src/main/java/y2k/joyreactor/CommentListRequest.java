package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/29/15.
 */
public class CommentListRequest {

    private String postId;
    private int commentId;

    public CommentListRequest(String postId, int commentId) {
        this.postId = postId;
        this.commentId = commentId;
    }

    public Observable<List<Comment>> request() {
        return ObservableUtils.create(() -> Observable
                .from(getAllComments())
                .filter(s -> s.parentId == commentId)
                .toSortedList((a, b) -> (int) (b.rating - a.rating))
                .toBlocking()
                .single());
    }

    private List<Comment> getAllComments() throws IOException {
        Document doc = new HttpClient().getDocument("http://anime.reactor.cc/post/" + postId);
        List<Comment> result = new ArrayList<>();
        for (Element node : doc.select("div.comment")) {
            Comment comment = new Comment();
            comment.text = node.select("div.txt > div").first().text();
            comment.userAvatar = node.select("img.avatar").attr("src");
            comment.id = Integer.parseInt(node.select("span.comment_rating").attr("comment_id"));
            comment.rating = Float.parseFloat(node.select("span.comment_rating").text().trim());

            Element parent = node.parent();
            if ("comment_list".equals(parent.className()))
                comment.parentId = NumberExtractor.get(parent.id());

            result.add(comment);
        }
        ChildrenCounter.compute(result);
        return result;
    }

    private static class NumberExtractor {

        static final Pattern NUMBER = Pattern.compile("\\d+");

        static int get(String text) {
            Matcher m = NUMBER.matcher(text);
            return m.find() ? Integer.parseInt(m.group()) : 0;
        }
    }

    private static class ChildrenCounter {

        static void compute(List<Comment> comments) {
            // TODO: оптимизировать
            for (int i = 0; i < comments.size() - 1; i++) {
                Comment c = comments.get(i);
                for (int n = i + 1; n < comments.size(); n++)
                    if (comments.get(n).parentId == c.id) c.childCount++;
            }
        }
    }
}