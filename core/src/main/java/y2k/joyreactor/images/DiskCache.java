package y2k.joyreactor.images;

import java.io.*;

/**
 * Created by y2k on 9/27/15.
 */
public class DiskCache {

    public ReadAction load(String url) {
        if (!urlToFile(url).exists()) return null;
        return new ReadAction() {

            InputStream stream;

            @Override
            public InputStream getStream() throws IOException {
                return stream = new FileInputStream(urlToFile(url));
            }

            @Override
            public void close() throws IOException {
                stream.close();
            }
        };
    }

    public WriteAction save(String url) {
        return new WriteAction() {

            File tmp;
            OutputStream stream;

            @Override
            public OutputStream getStream() throws IOException {
                tmp = File.createTempFile("image_", null, getCacheDirectory());
                return stream = new FileOutputStream(tmp);
            }

            @Override
            public void close() throws IOException {
                try {
                    stream.close();
                    tmp.renameTo(urlToFile(url));
                } finally {
                    tmp.delete();
                }
            }
        };
    }

    File urlToFile(String url) {
        return new File(getCacheDirectory(), "" + url.hashCode());
    }

    File getCacheDirectory() {
        String dir = System.getProperty("user.dir");
        System.out.println("Current dir = " + dir);
        return new File(dir);
    }

    public interface ReadAction {

        InputStream getStream() throws IOException;

        void close() throws IOException;
    }

    interface WriteAction {

        OutputStream getStream() throws IOException;

        void close() throws IOException;
    }
}
