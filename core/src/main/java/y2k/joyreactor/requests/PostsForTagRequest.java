package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import y2k.joyreactor.Post;
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
public class PostsForTagRequest {

    public List<Post> posts;
    public String nextPageId;

    private String tagId;
    private String pageId;

    public PostsForTagRequest(String tagId, String pageId) {
        this.tagId = tagId;
        this.pageId = pageId;
    }

    public void request() throws IOException {
        Document doc = HttpClient.getInstance().getDocument(buildUrl());

        posts = new ArrayList<>();
        for (Element e : doc.select("div.postContainer"))
            posts.add(newPost(e));

        Element next = doc.select("a.next").first();
        if (next != null) nextPageId = extractNumber(next.attr("href"));
    }

    private String buildUrl() {
        String url = "http://joyreactor.cc/";
        if (tagId != null) url += "tag/" + URLEncoder.encode(tagId);
        if (pageId != null) url += "/" + pageId;
        return url;
    }

    private Post newPost(Element element) {
        Post result = new Post();
        result.title = element.select("div.post_content").text();

        new ThumbnailParser(element).load(result);
        if (result.image == null) new YoutubeThumbnailParser(element).load(result);
        if (result.image == null) new VideoThumbnailParser(element).load(result);

        result.userName = element.select("div.uhead_nick > a").text();
        result.userImage = element.select("div.uhead_nick > img").attr("src");
        result.id = extractNumber(element.id());

        PostParser parser = new PostParser(element);
        result.created = parser.getCreated();
        result.commentCount = parser.getCommentCount();
        result.rating = parser.getRating();

        return result;
    }

    private String extractNumber(String text) {
        Matcher m = Pattern.compile("\\d+").matcher(text);
        if (!m.find()) throw new IllegalStateException();
        return m.group();
    }

    static class PostParser {

        private static final Pattern COMMENT_COUNT_REGEX = Pattern.compile("\\d+");
        private static final Pattern RATING_REGEX = Pattern.compile("[\\d\\.]+");
        private Element element;

        PostParser(Element element) {
            this.element = element;
        }

        int getCommentCount() {
            Element e = element.select("a.commentnum").first();
            Matcher m = COMMENT_COUNT_REGEX.matcher(e.text());
            if (!m.find()) throw new IllegalStateException();
            return Integer.parseInt(m.group());
        }

        float getRating() {
            Element e = element.select("span.post_rating > span").first();
            Matcher m = RATING_REGEX.matcher(e.text());
            return m.find() ? Float.parseFloat(m.group()) : 0;
        }

        Date getCreated() {
            Elements e = element.select("span.date > span");
            return new Date(1000L * Long.parseLong(e.attr("data-time")));
        }
    }

    static class ThumbnailParser {

        private Element element;

        ThumbnailParser(Element element) {
            this.element = element;
        }

        public void load(Post post) {
            Element img = element.select("div.post_content img").first();
            if (img != null && img.hasAttr("width")) {
                String image = img.attr("src");
                post.image = image.replaceAll("(/post/).+(-\\d+\\.)", "$1$2");
                post.width = Integer.parseInt(img.attr("width"));
                post.height = Integer.parseInt(img.attr("height"));
            }
        }
    }

    static class YoutubeThumbnailParser {

        private static final Pattern SRC_PATTERN = Pattern.compile("/embed/([^\\?]+)");
        private Element element;

        YoutubeThumbnailParser(Element element) {
            this.element = element;
        }

        public void load(Post post) {
            Element iframe = element.select("iframe.youtube-player").first();
            if (iframe == null) return;

            post.width = Integer.parseInt(iframe.attr("width"));
            post.height = Integer.parseInt(iframe.attr("height"));

            Matcher m = SRC_PATTERN.matcher(iframe.attr("src"));
            if (!m.find()) throw new IllegalStateException(iframe.attr("src"));
            post.image = "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg";
        }
    }

    private class VideoThumbnailParser {

        private Element element;

        public VideoThumbnailParser(Element element) {
            this.element = element;
        }

        public void load(Post post) {
            Element video = element.select("video[poster]").first();
            if (video == null) return;

            try {
                post.width = Integer.parseInt(video.attr("width"));
                post.height = Integer.parseInt(video.attr("height"));
                post.image = video.attr("poster").replaceAll("(/static/).+(-\\d+\\.)", "$1$2");
                post.mediaUrl = post.image.replaceAll("[^\\.]+$", "gif");
            } catch (Exception e) {
                System.out.println("ELEMENT | " + video);
                throw e;
            }
        }
    }
}