package y2k.joyreactor;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import sun.misc.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by y2k on 9/26/15.
 */
public class ImageRequest {

    private UrlBuilder urlBuilder = new UrlBuilder();

    public ImageRequest setUrl(String imageUrl) {
        urlBuilder.url = imageUrl;
        return this;
    }

    public ImageRequest setSize(int width, int height) {
        urlBuilder.width = width;
        urlBuilder.height = height;
        return this;
    }

    public void load(Action1<byte[]> callback) {
        Schedulers.io().createWorker().schedule(() -> {
            try {

                URL url = urlBuilder.build();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                InputStream stream = null;
                try {
                    URLConnection connection = url.openConnection();
                    stream = connection.getInputStream();
                    byte[] buf = new byte[4 * 1024];
                    int count;
                    while ((count = stream.read(buf)) != -1)
                        result.write(buf, 0, count);
                } finally {
                    if (stream != null) stream.close();
                }

                ForegroundScheduler.getInstance().createWorker().schedule(() -> callback.call(result.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    class UrlBuilder {

        String url;
        int width;
        int height;

        private URL build() throws MalformedURLException {
            return new URL(
                    "http", "api-i-twister.net", 8010,
                    "/cache/fit?width=" + width + "&height=" + height + "&url=" + url);
        }
    }
}
