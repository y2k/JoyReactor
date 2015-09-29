package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Date;

/**
 * Created by y2k on 9/26/15.
 */
public class PostRequest {

    public Post.Collection getPosts() throws IOException {
        Document doc = new HttpClient().getDocument("http://joyreactor.cc");

        Post.Collection posts = new Post.Collection();
        for (Element e : doc.select("div.postContainer"))
            posts.add(newPost(e));

        return posts;
    }

    private Post newPost(Element element) {
        Post result = new Post();
        result.title = element.select("div.post_content").text();
        Element img = element.select("div.post_content img").first();
        if (img != null && img.hasAttr("width")) {
            result.image = img.attr("src");
            result.width = Integer.parseInt(img.attr("width"));
            result.height = Integer.parseInt(img.attr("height"));
        }

        result.userName = element.select("div.uhead_nick > a").text();
        result.userImage = element.select("div.uhead_nick > img").attr("src");

        result.created = new Date(1000L * Long.parseLong(element.select("span.date > span").attr("data-time")));

        return result;
    }
}