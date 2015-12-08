package y2k.joyreactor.services.requests;

import rx.Observable;
import rx.schedulers.Schedulers;
import y2k.joyreactor.common.ForegroundScheduler;
import y2k.joyreactor.common.PartialResult;
import y2k.joyreactor.platform.Platform;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 16/10/15.
 */
public class OriginalImageRequestFactory {

    public Observable<File> request(String imageUrl) {
        return ObservableUtils.create(() -> {

            File file = new File(Platform.Instance.getCurrentDirectory(), "" + imageUrl.hashCode() + "." + getExtension(imageUrl));
            if (file.exists()) return file;

            try {
                HttpClient.getInstance().downloadToFile(imageUrl, file, null);
            } catch (Exception e) {
                file.delete();
                throw e;
            }

            return file;
        });
    }

    public Observable<PartialResult<File>> requestPartial(String imageUrl) {
        return Observable
                .<PartialResult<File>>create(subscriber -> {
                    // TODO

                    File file = new File(Platform.Instance.getCurrentDirectory(), "" + imageUrl.hashCode() + "." + getExtension(imageUrl));
                    if (file.exists()) subscriber.onNext(PartialResult.complete(file));

                    try {
                        HttpClient.getInstance().downloadToFile(
                                imageUrl, file,
                                (progress, max) -> subscriber.onNext(PartialResult.<File>inProgress(progress, max)));

                        subscriber.onNext(PartialResult.complete(file));
                    } catch (Exception e) {
                        file.delete();
                        subscriber.onError(e);
                    }

                })
                .subscribeOn(Schedulers.io())
                .observeOn(ForegroundScheduler.getInstance());
    }

    private static String getExtension(String imageUrl) {
        Matcher fm = Pattern.compile("format=([^&]+)").matcher(imageUrl);
        if (fm.find()) return fm.group(1);

        Matcher m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl);
        if (!m.find()) throw new IllegalStateException("can't find extension from url " + imageUrl);
        return m.group(1);
    }
}