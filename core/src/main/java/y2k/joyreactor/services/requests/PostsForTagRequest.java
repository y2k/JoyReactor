package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rx.Observable;
import y2k.joyreactor.Image;
import y2k.joyreactor.Post;
import y2k.joyreactor.Tag;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/26/15.
 */
public class PostsForTagRequest {

    private String nextPageId;
    private List<Post> posts;
    private String url;

    private PostsForTagRequest(Tag tagId, String pageId) {
        url = new UrlBuilder().build(tagId, pageId);
    }

    public Observable<Void> requestAsync() {
        return ObservableUtils.create(this::request);
    }

    public void request() throws IOException {
        Document doc = HttpClient.getInstance().getDocument(url);

        posts = new ArrayList<>();
        for (Element e : doc.select("div.postContainer"))
            getPosts().add(newPost(e));

        Element next = doc.select("a.next").first();
        if (next != null) nextPageId = extractNumberFromEnd(next.attr("href"));
    }

    static Post newPost(Element element) {
        Post result = new Post();
        result.setTitle(element.select("div.post_content").text());

        new ThumbnailParser(element).load(result);
        if (result.getImage() == null) new YoutubeThumbnailParser(element).load(result);
        if (result.getImage() == null) new VideoThumbnailParser(element).load(result);

        result.setUserName(element.select("div.uhead_nick > a").text());
        result.setUserImage(element.select("div.uhead_nick > img").attr("src"));
        result.setServerId(extractNumberFromEnd(element.id()));

        PostParser parser = new PostParser(element);
        result.setCreated(parser.getCreated());
        result.setCommentCount(parser.getCommentCount());
        result.setRating(parser.getRating());

        return result;
    }

    private static String extractNumberFromEnd(String text) {
        Matcher m = Pattern.compile("\\d+$").matcher(text);
        if (!m.find()) throw new IllegalStateException();
        return m.group();
    }

    public List<Post> getPosts() {
        return posts;
    }

    public String getNextPageId() {
        return nextPageId;
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
                post.setImage(new Image(
                        hasFull(img)
                                ? img.parent().attr("href").replaceAll("(/full/).+(-\\d+\\.)", "$1$2")
                                : img.attr("src").replaceAll("(/post/).+(-\\d+\\.)", "$1$2"),
                        Integer.parseInt(img.attr("width")),
                        Integer.parseInt(img.attr("height"))));
            }
        }

        private boolean hasFull(Element img) {
            return "a".equals(img.parent().tagName());
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


            Matcher m = SRC_PATTERN.matcher(iframe.attr("src"));
            if (!m.find()) throw new IllegalStateException(iframe.attr("src"));
            post.setImage(new Image(
                    "http://img.youtube.com/vi/" + m.group(1) + "/0.jpg",
                    Integer.parseInt(iframe.attr("width")),
                    Integer.parseInt(iframe.attr("height"))));
        }
    }

    static class VideoThumbnailParser {

        private Element element;

        public VideoThumbnailParser(Element element) {
            this.element = element;
        }

        public void load(Post post) {
            Element video = element.select("video[poster]").first();
            if (video == null) return;

            try {
                post.setImage(new Image(
                        element.select("span.video_gif_holder > a").first().attr("href")
                                .replaceAll("(/post/).+(-)", "$1$2"),
                        Integer.parseInt(video.attr("width")),
                        Integer.parseInt(video.attr("height"))));
            } catch (Exception e) {
                System.out.println("ELEMENT | " + video);
                throw e;
            }
        }
    }

    public static class Factory {

        public PostsForTagRequest make(Tag tagId, String pageId) {
            return new PostsForTagRequest(tagId, pageId);
        }
    }
}