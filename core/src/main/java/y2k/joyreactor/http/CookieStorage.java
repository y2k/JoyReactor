package y2k.joyreactor.http;

import y2k.joyreactor.Platform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 10/11/15.
 */
class CookieStorage {

    private static Pattern COOKIE_PATTERN = Pattern.compile("(.+?)=([^;]+)");

    private File cookieFile;
    private Map<String, String> map = new ConcurrentHashMap<>();

    public CookieStorage(String name) {
        cookieFile = new File(Platform.Instance.getCurrentDirectory(), name);
    }

    public synchronized void attach(HttpURLConnection connection) throws IOException {
        lazyInitialize();
        if (map.isEmpty()) return;

        StringBuilder cookie = new StringBuilder();
        for (String key : map.keySet())
            cookie.append(key).append("=").append(map.get(key)).append("; ");
        connection.addRequestProperty("Cookie", cookie.toString());
    }

    public synchronized void grab(HttpURLConnection connection) throws IOException {
        lazyInitialize();

        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if (cookies == null || cookies.isEmpty()) return;
        for (String c : cookies) {
            Matcher m = COOKIE_PATTERN.matcher(c);
            if (!m.find()) throw new IllegalStateException(c);
            map.put(m.group(1), m.group(2));
        }

        dump();
    }

    private void lazyInitialize() throws IOException {
        if (!cookieFile.exists()) return;

        String data = new FileStringReader(cookieFile).readAll();
        Matcher m = COOKIE_PATTERN.matcher(data);
        while (m.find())
            map.put(m.group(1), m.group(2));
    }

    private void dump() throws IOException {
        if (map.isEmpty()) return;

        StringBuilder cookie = new StringBuilder();
        for (String key : map.keySet())
            cookie.append(key).append("=").append(map.get(key)).append("; ");

        new FileStringReader(cookieFile).writeAll(cookie.toString());
    }

    public synchronized void clear() {
        cookieFile.delete();
        map.clear();
    }

    private static class FileStringReader {

        private File file;

        public FileStringReader(File file) {
            this.file = file;
        }

        public String readAll() throws IOException {
            FileInputStream in = new FileInputStream(file);
            try {
                byte[] buf = new byte[(int) file.length()];
                in.read(buf);
                return new String(buf);
            } finally {
                in.close();
            }
        }

        public void writeAll(String data) throws IOException {
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(data.getBytes());
            } finally {
                out.close();
            }
        }
    }
}