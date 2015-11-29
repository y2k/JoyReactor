package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import y2k.joyreactor.Comment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/29/15.
 */
class PostCommentsRequest {

    private List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public void request(Document doc) throws IOException {
        List<Comment> result = new ArrayList<>();
        for (Element node : doc.select("div.comment")) {
            Comment comment = new Comment();
            comment.text = node.select("div.txt > div").first().text();
            comment.userImage = node.select("img.avatar").attr("src");
            comment.id = Integer.parseInt(node.select("span.comment_rating").attr("comment_id"));
            comment.rating = Float.parseFloat(node.select("span.comment_rating").text().trim());

            Element parent = node.parent();
            if ("comment_list".equals(parent.className()))
                comment.parentId = NumberExtractor.get(parent.id());

            Element imgElement = node.select("div.image > img").first();
            if (imgElement != null)
                comment.setAttachment(
                        clearImageUrl(imgElement.absUrl("src")),
                        Integer.parseInt(imgElement.attr("width")),
                        Integer.parseInt(imgElement.attr("height")));

            result.add(comment);
        }

        ChildrenCounter.compute(result);
        comments = result;
    }

    private String clearImageUrl(String url) {
        return url.replaceAll("(/comment/).+(-\\d+\\.[\\w\\d]+)$","$1$2");
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
                    if (comments.get(n).parentId == c.id) c.replies++;
            }
        }
    }
}