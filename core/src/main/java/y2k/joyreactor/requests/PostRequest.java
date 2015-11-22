package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import y2k.joyreactor.Comment;
import y2k.joyreactor.Post;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.List;

/**
 * Created by y2k on 11/21/15.
 */
public class PostRequest {

    private PostCommentsRequest commentsRequest = new PostCommentsRequest();
    private Post post;

    public Post getPost() {
        return post;
    }

    public List<Comment> getComments() {
        return commentsRequest.getComments();
    }

    public void request(String postId) throws IOException {
        Document page = HttpClient.getInstance().getDocument(getPostUrl(postId));

        Element postNode = page.select("div.post_content").first();
        post = PostsForTagRequest.newPost(postNode); // TODO:

        commentsRequest.request(page);
    }

    private String getPostUrl(String postId) {
        return "http://anime.reactor.cc/post/" + postId; // TODO:
    }
}