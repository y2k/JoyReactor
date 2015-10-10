package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/26/15.
 */
public class PostRequest {

    public List<Post> posts;
    public String nextPageId;

    private String tagId;
    private String pageId;

    public PostRequest(String tagId, String pageId) {
        this.tagId = tagId;
        this.pageId = pageId;
    }

    public void request() throws IOException {
        Document doc = new HttpClient().getDocument(buildUrl());

        posts = new ArrayList<>();
        for (Element e : doc.select("div.postContainer"))
            posts.add(newPost(e));

        Element next = doc.select("a.next").first();
        if (next != null) nextPageId = extractNumber(next.attr("href"));
    }

    private String buildUrl() {
        String url = "http://joyreactor.cc";
        if (tagId != null) url += "/tag/" + URLEncoder.encode(tagId);
        if (pageId != null) url += "/" + pageId;
        return url;
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
        result.id = extractNumber(element.id());
        return result;
    }

    private String extractNumber(String text) {
        Matcher m = Pattern.compile("\\d+").matcher(text);
        if (!m.find()) throw new IllegalStateException();
        return m.group();
    }
}