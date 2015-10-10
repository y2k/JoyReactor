package y2k.joyreactor.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 10/11/15.
 */
class CookieStorage {

    private static final Pattern COOKIE_PATTERN = Pattern.compile("(.+?)=([^;]+)");
    private Map<String, String> storage = new HashMap<>();

    public void attach(HttpURLConnection connection) {
        if (storage.isEmpty()) return;

        StringBuilder cookie = new StringBuilder();
        for (String key : storage.keySet())
            cookie.append(key).append("=").append(storage.get(key)).append("; ");
        connection.addRequestProperty("Cookie", cookie.toString());
    }

    public void grab(HttpURLConnection connection) {
        List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
        if (cookies == null || cookies.isEmpty()) return;

        for (String c : cookies) {
            Matcher m = COOKIE_PATTERN.matcher(c);
            if (!m.find()) throw new IllegalStateException(c);
            storage.put(m.group(1), m.group(2));
        }
    }

    public void clear() {
        storage.clear();
    }
}