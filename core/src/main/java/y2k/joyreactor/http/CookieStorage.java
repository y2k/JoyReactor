package y2k.joyreactor.http;

import y2k.joyreactor.common.PersistentMap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 10/11/15.
 */
public class CookieStorage {

    private static Pattern COOKIE_PATTERN = Pattern.compile("(.+?)=([^;]+)");
    private PersistentMap map = new PersistentMap("cookies.1.dat");

    public synchronized void attach(HttpURLConnection connection) throws IOException {
        if (map.isEmpty()) return;

        StringBuilder cookie = new StringBuilder();
        for (String key : map.keySet())
            cookie.append(key).append("=").append(map.get(key)).append("; ");
        connection.addRequestProperty("Cookie", cookie.toString());
    }

    public synchronized void grab(HttpURLConnection connection) throws IOException {
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if (cookies == null || cookies.isEmpty()) return;
        for (String c : cookies) {
            Matcher m = COOKIE_PATTERN.matcher(c);
            if (!m.find()) throw new IllegalStateException(c);
            map.put(m.group(1), m.group(2));
        }
        map.flush();
    }

    public void clear() {
        map.clear();
    }
}