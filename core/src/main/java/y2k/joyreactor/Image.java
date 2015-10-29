package y2k.joyreactor;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by y2k on 10/29/15.
 */
public class Image {

    public String url;
    public int width;
    public int height;

    public URL toURL(int width, int height, String format) throws MalformedURLException {
        if (width == 0 || height == 0)
            return new URL("http", "api-i-twister.net", 8010, "/cache/original?url=" + url + getFormatPart(format));

        return new URL(
                "http", "api-i-twister.net", 8010,
                "/cache/fit?width=" + width + "&height=" + height + "&url=" + url);
    }

    private String getFormatPart(String format) {
        return format == null ? "" : "&format=" + format;
    }
}
