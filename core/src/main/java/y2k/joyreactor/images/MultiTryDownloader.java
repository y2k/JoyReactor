package y2k.joyreactor.images;

import rx.Observable;
import rx.Subscriber;
import y2k.joyreactor.ForegroundScheduler;
import y2k.joyreactor.IoUtils;

import java.io.*;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
                    subscriber.onNext(tryDownloadSync());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    if (tryNumber > MAX_TRY) subscriber.onError(e);
                    else downloadAsync(tryNumber + 1, subscriber);
                }
            });
        }, 250 << tryNumber, TimeUnit.MILLISECONDS);
    }

    private File tryDownloadSync() throws IOException {
        InputStream in = null;
        OutputStream out = null;
        File result = null;
        try {
            in = new URL(url).openConnection().getInputStream();
            result = File.createTempFile("download_", null, dir);
            out = new FileOutputStream(result);
            IoUtils.copy(in, out);
            return result;
        } catch (IOException e) {
            IoUtils.close(in, out);
            if (result != null) result.delete();
            throw e;
        } finally {
            IoUtils.close(in, out);
        }
    }
}