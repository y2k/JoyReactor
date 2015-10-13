package y2k.joyreactor.images;

import rx.Observable;
import rx.Subscriber;
import y2k.joyreactor.ForegroundScheduler;
import y2k.joyreactor.http.HttpClient;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 12/10/15.
 */
class MultiTryDownloader {

    private static TaskExecutor DOWNLOAD_EXECUTOR = new TaskExecutor(3);
    private static final int MAX_TRY = 5;

    private File dir;
    private String url;

    MultiTryDownloader(File dir, String url) {
        this.dir = dir;
        this.url = url;
    }

    public Observable<File> downloadAsync() {
        return Observable.create(subscriber -> downloadAsync(0, subscriber));
    }

    private void downloadAsync(int tryNumber, Subscriber<? super File> subscriber) {
        ForegroundScheduler.getInstance().createWorker().schedule(() -> {
            DOWNLOAD_EXECUTOR.execute(() -> {
                try {
                    subscriber.onNext(downloadToTempFile());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    if (tryNumber > MAX_TRY) subscriber.onError(e);
                    else downloadAsync(tryNumber + 1, subscriber);
                }
            });
        }, 250 << tryNumber, TimeUnit.MILLISECONDS);
    }

    private File downloadToTempFile() throws IOException {
        File result = null;
        try {
            result = File.createTempFile("download_", null, dir);
            HttpClient.getInstance().downloadToFile(url, result);
            return result;
        } catch (IOException e) {
            if (result != null) result.delete();
            throw e;
        }
    }
}