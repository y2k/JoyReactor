package y2k.joyreactor;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by y2k on 9/26/15.
 */
public class ImageRequest {

    private String imageUrl;

    public ImageRequest setUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public void load(Action1<byte[]> callback) {
        Schedulers.io().createWorker().schedule(() -> {
            try {

                URL url = new URL("http", "api-i-twister.net", 8010, "/cache/fit?width=150&height=150&url=" + imageUrl);
                InputStream stream = null;
                byte[] buf = new byte[64 * 1024];
                try {
                    stream = url.openConnection().getInputStream();
                    for (int pos = 0; pos < buf.length; ) {
                        int count = stream.read(buf, pos, buf.length - pos);
                        if (count < 0) break;
                        pos += count;
                    }
                } finally {
                    if (stream != null) stream.close();
                }

                ForegroundScheduler.getInstance().createWorker().schedule(() -> callback.call(buf));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
