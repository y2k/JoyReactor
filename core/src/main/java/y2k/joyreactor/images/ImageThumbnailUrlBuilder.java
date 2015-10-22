package y2k.joyreactor.images;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by y2k on 12/10/15.
 */
public class ImageThumbnailUrlBuilder {

    public String url;
    public int width;
    public int height;
    public String format;

    public URL build() throws MalformedURLException {
        if (width == 0 || height == 0)
            return new URL("http", "api-i-twister.net", 8010, "/cache/original?url=" + url + getFormatPart());

        return new URL(
                "http", "api-i-twister.net", 8010,
                "/cache/fit?width=" + width + "&height=" + height + "&url=" + url);
    }

    private String getFormatPart() {
        return format == null ? "" : "&format=" + format;
    }

    public String buildString() {
        try {
            return build().toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}