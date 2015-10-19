package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import y2k.joyreactor.common.PersistentMap;
import y2k.joyreactor.http.HttpClient;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by y2k on 10/18/15.
 */
public class TagImageRequest {

    private PersistentMap cache = new PersistentMap("tag-images.1.dat");

    public String request(String tag) throws IOException {
        String clearTag = tag.toLowerCase();
        String imageId = TagPreloadedImages.get(clearTag);
        if (imageId == null) imageId = cache.get(clearTag);
        if (imageId == null) {
            imageId = getFromWeb(clearTag);
            if (imageId != null) cache.put(clearTag, imageId).flush();
        }

        if (imageId == null) throw new IllegalStateException();
        return imageId;
    }

    private String getFromWeb(String tag) throws IOException {
        Document doc = HttpClient.getInstance().getDocument("http://joyreactor.cc/tag/" + URLEncoder.encode(tag));
        String result = doc.select("img.blog_avatar").first().attr("src");
        System.out.println("Not found in cache | " + tag + " | " + result);
        return result;
    }
}