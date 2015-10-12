package y2k.joyreactor.images;

import rx.Observable;
import rx.Subscriber;
import y2k.joyreactor.ForegroundScheduler;

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

//                    System.out.println("try download | " + url); // FIXME

                    subscriber.onNext(tryDownloadSync());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    if (tryNumber > MAX_TRY || !subscriber.isUnsubscribed()) subscriber.onError(e);
                    else downloadAsync(tryNumber + 1, subscriber);
                }
            });
        }, 250 << tryNumber, TimeUnit.MILLISECONDS);
    }

    private File tryDownloadSync() throws InterruptedException, IOException {
        InputStream in = null;
        OutputStream out = null;
        File result = null;
        try {
            in = new URL(url).openConnection().getInputStream();
            result = File.createTempFile("download_", null, dir);
            out = new FileOutputStream(result);
            copy(in, out);
            return result;
        } finally {
            BaseImageRequest.close(in, out);
            if (result != null) result.delete();
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4 * 1024];
        int count;
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
    }
}
