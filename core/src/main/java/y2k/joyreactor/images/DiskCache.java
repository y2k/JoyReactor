package y2k.joyreactor.images;

import rx.Observable;
import y2k.joyreactor.IoUtils;
import y2k.joyreactor.Platform;

import java.io.*;

/**
 * Created by y2k on 9/27/15.
 */
class DiskCache {

    public ReadAction load(String url) {
        if (!urlToFile(url).exists()) return null;
        return new ReadAction() {

            @Deprecated
            InputStream stream;

            @Override
            @Deprecated
            public InputStream getStream() throws IOException {
                return stream = new FileInputStream(urlToFile(url));
            }

            @Override
            public File getPath() {
                return urlToFile(url);
            }

            @Override
            public void close() throws IOException {
                IoUtils.close(stream);
            }
        };
    }

    @Deprecated
    public WriteAction save(String url) {
        return new WriteAction() {

            File tmp;
            FileOutputStream stream;

            @Override
            public OutputStream getStream() throws IOException {
                tmp = File.createTempFile("image_", null, getCacheDirectory());
                return stream = new FileOutputStream(tmp);
            }

            @Override
            public void close() throws IOException {
                try {
                    IoUtils.close(stream);
                    tmp.renameTo(urlToFile(url));
                } finally {
                    tmp.delete();
                }
            }
        };
    }

    public Observable<?> putAsync(File newImageFile, String url) {
        // TODO: оптимизировать
        newImageFile.renameTo(urlToFile(url));
        return Observable.just(null);
    }

    File urlToFile(String url) {
        return new File(getCacheDirectory(), "" + url.hashCode());
    }

    File getCacheDirectory() {
        return Platform.Instance.getCurrentDirectory();
    }

    interface ReadAction extends Closeable {

        @Deprecated
        InputStream getStream() throws IOException;

        File getPath();
    }

    @Deprecated
    interface WriteAction extends Closeable {

        OutputStream getStream() throws IOException;
    }
}
