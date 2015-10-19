package y2k.joyreactor.common;

import java.io.*;

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

    public static String readAll(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        try {
            byte[] buf = new byte[(int) file.length()];
            in.read(buf);
            return new String(buf);
        } finally {
            in.close();
        }
    }

    public static void writeAll(File file, String data) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(data.getBytes());
        } finally {
            out.close();
        }
    }
}