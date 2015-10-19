package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;
import y2k.joyreactor.images.ImageThumbnailUrlBuilder;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 16/10/15.
 */
public class ImageRequest {

    private String imageUrl;

    public ImageRequest(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Observable<File> request() {
        return ObservableUtils.create(() -> {

            File file = new File(Platform.Instance.getCurrentDirectory(), "" + imageUrl.hashCode() + "." + getExtension());
            if (file.exists()) return file;

            try {
                HttpClient.getInstance().downloadToFile(getImageUrl(), file);
            } catch (Exception e) {
                file.delete();
                throw e;
            }

            return file;
        });
    }

    private String getImageUrl() {
        ImageThumbnailUrlBuilder urlBuilder = new ImageThumbnailUrlBuilder();
        urlBuilder.url = imageUrl;
        return urlBuilder.buildString();
    }

    private String getExtension() {
        Matcher m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl);
        if (!m.find()) throw new IllegalStateException("can't find extension from url " + imageUrl);
        return m.group(1);
    }
}