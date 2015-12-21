package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import y2k.joyreactor.*;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 11/21/15.
 */
public class PostRequest {

    private static final Pattern POST_ID = Pattern.compile("/post/(\\d+)");

    private Post post;
    private PostCommentsRequest commentsRequest = new PostCommentsRequest();
    private List<SimilarPost> similarPosts = new ArrayList<>();
    private List<Attachment> attachments = new ArrayList<>();

    public List<SimilarPost> getSimilarPosts() {
        return similarPosts;
    }

    public Post getPost() {
        return post;
    }

    public List<Comment> getComments() {
        return commentsRequest.getComments();
    }

    public void request(String postId) throws IOException {
        Document page = HttpClient.getInstance().getDocument(getPostUrl(postId));

        Element postNode = page.select("div.postContainer").first();
        post = PostsForTagRequest.newPost(postNode); // TODO:

        commentsRequest.request(page);

        for (Element e : page.select(".similar_post img")) {
            SimilarPost similarPost = new SimilarPost(getPostId(e.parent().attr("href")));
            similarPost.setImage(new Image(e.absUrl("src"), 0, 0));
            similarPosts.add(similarPost);
        }

        Elements imgElement = postNode.select("div.image > img");
        if (imgElement.size() > 1)
            for (Element e : imgElement.subList(1, imgElement.size() - 1)) {
                Attachment a = new Attachment();
                a.image = new Image(e.absUrl("src"),
                        Integer.parseInt(e.attr("width")),
                        Integer.parseInt(e.attr("height")));
                attachments.add(a);
            }
    }

    private String getPostId(String href) {
        Matcher m = POST_ID.matcher(href);
        if (!m.find()) throw new IllegalStateException();
        return m.group(1);
    }

    private String getPostUrl(String postId) {
        return "http://anime.reactor.cc/post/" + postId; // TODO:
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }
}