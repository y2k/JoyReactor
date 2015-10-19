package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by y2k on 10/18/15.
 */
public class TagImageRequest {

    private TagImageIdCache cache = new TagImageIdCache();

    public String request(String tag) throws IOException {
        String clearTag = tag.toLowerCase();
        String imageId = TagPreloadedImages.get(clearTag);
        if (imageId == null) imageId = cache.get(clearTag);
        if (imageId == null) {
            imageId = getFromWeb(clearTag);
            if (imageId != null) cache.put(clearTag, imageId);
        }

        if (imageId == null) throw new IllegalStateException();
        return imageId;
    }

    private String getFromWeb(String tag) throws IOException {
        try {
            Document doc = HttpClient.getInstance().getDocument("http://joyreactor.cc/tag/" + URLEncoder.encode(tag));
            return doc.select("img.blog_avatar").first().attr("src");
        } catch (RuntimeException e) {
            throw new RuntimeException("tag = " + tag, e);
        }
    }

    static class TagImageIdCache {

        Map<String, String> sMap = new ConcurrentHashMap<>();

        public String get(String key) {
            return sMap.get(key);
        }

        public void put(String key, String value) {
            sMap.put(key, value);
        }
    }
}