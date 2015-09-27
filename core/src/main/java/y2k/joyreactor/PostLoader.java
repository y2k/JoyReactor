package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by y2k on 9/26/15.
 */
public class PostLoader {

    public Post.Collection getPosts() throws IOException {
        Document doc = getDocument();

        Post.Collection posts = new Post.Collection();
        for (Element e : doc.select("div.postContainer"))
            posts.add(newPost(e));

        return posts;
    }

    private Document getDocument() throws IOException {
        return Jsoup.connect("http://joyreactor.cc")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko")
                .timeout(15000).get();
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
        return result;
    }
}