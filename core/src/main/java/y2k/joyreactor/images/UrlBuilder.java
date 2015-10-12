package y2k.joyreactor.images;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by y2k on 12/10/15.
 */
class UrlBuilder {

    String url;
    int width;
    int height;

    public URL build() throws MalformedURLException {
        return new URL(
                "http", "api-i-twister.net", 8010,
                "/cache/fit?width=" + width + "&height=" + height + "&url=" + url);
    }

    public String buildString() {
        try {
            return build().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}