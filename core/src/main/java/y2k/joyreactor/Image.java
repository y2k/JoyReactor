package y2k.joyreactor;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by y2k on 10/29/15.
 */
public class Image implements Serializable {

    public String url;
    private int width;
    private int height;

    public Image() {
    }

    public Image(String url) {
        this(url, 0, 0);
    }

    public Image(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String fullUrl(String format) {
        try {
            return toURL(0, 0, format).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String thumbnailUrl(int width, int height) {
        try {
            return toURL(width, height, null).toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    URL toURL(int width, int height, String format) throws MalformedURLException {
        if (width == 0 || height == 0)
            return new URL("http", "api-i-twister.net", 8010, "/cache/original?url=" + url + getFormatPart(format));

        return new URL(
                "http", "api-i-twister.net", 8010,
                "/cache/fit?bgColor=ffffff&width=" + width + "&height=" + height + "&url=" + url);
    }

    private String getFormatPart(String format) {
        return format == null ? "" : "&format=" + format;
    }

    public boolean isAnimated() {
        return url != null && url.endsWith(".gif");
    }

    public float getAspect() {
        return getAspect(0);
    }

    public float getAspect(float min) {
        float aspect = height == 0 ? 1 : (float) width / height;
        return Math.min(2, Math.max(min, aspect));
    }
}