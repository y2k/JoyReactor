package y2k.joyreactor.images;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import y2k.joyreactor.ForegroundScheduler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by y2k on 9/26/15.
 */
public class ImageRequest {

    private static DiskCache cache = new DiskCache();
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

                byte[] image = getFromCache(url);
                if (image == null)
                    image = new Downloader().getFromWeb(url);
                if (image == null)
                    throw new Exception();

                final byte[] finalImage = image;
                ForegroundScheduler.getInstance().createWorker().schedule(() -> callback.call(finalImage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private byte[] getFromCache(URL url) throws Exception {
        DiskCache.ReadAction in = null;
        try {
            in = cache.load("" + url);
            if (in == null) return null;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            copy(in.getStream(), out);
            return out.toByteArray();
        } finally {
            if (in != null) in.close();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4 * 1024];
        int count;
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
    }

    class Downloader {

        private byte[] getFromWeb(URL url) throws Exception {
            for (int i = 0; i < 5; i++)
                if (tryDownload(url, i)) break;
            return getFromCache(url);
        }

        private boolean tryDownload(URL url, int tryCount) throws InterruptedException, IOException {
            InputStream in = null;
            DiskCache.WriteAction out = null;
            try {
                in = url.openConnection().getInputStream();
                out = cache.save("" + url);
                copy(in, out.getStream());
                return true;
            } catch (Exception e) {
                Thread.sleep(500 << tryCount);
            } finally {
                if (in != null) in.close();
                if (out != null) out.close();
            }
            return false;
        }
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
