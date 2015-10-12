package y2k.joyreactor;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by y2k on 10/12/15.
 */
public class IoUtils {

    public static void close(Closeable... sourceList) {
        for (Closeable source : sourceList) {
            try {
                if (source != null) source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4 * 1024];
        int count;
        while ((count = in.read(buf)) != -1)
            out.write(buf, 0, count);
    }
}