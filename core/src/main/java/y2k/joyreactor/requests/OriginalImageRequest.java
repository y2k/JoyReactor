package y2k.joyreactor.requests;

import rx.Observable;
import y2k.joyreactor.Platform;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 16/10/15.
 */
public class OriginalImageRequest {

    private String imageUrl;

    public OriginalImageRequest(String imageUrl) {
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
        return imageUrl;
    }

    private String getExtension() {
        Matcher fm = Pattern.compile("format=([^&]+)").matcher(imageUrl);
        if (fm.find()) return fm.group(1);

        Matcher m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl);
        if (!m.find()) throw new IllegalStateException("can't find extension from url " + imageUrl);
        return m.group(1);
    }
}