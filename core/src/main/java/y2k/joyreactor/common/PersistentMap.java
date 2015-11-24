package y2k.joyreactor.common;

import y2k.joyreactor.platform.Platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by y2k on 19/10/15.
 */
public class PersistentMap {

    private File cookieFile;
    private Map<String, String> map = new ConcurrentHashMap<>();

    public PersistentMap(String name) {
        cookieFile = new File(Platform.Instance.getCurrentDirectory(), name);

        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() throws IOException {
        if (!cookieFile.exists()) return;

        String data = IoUtils.readAll(cookieFile);
        BufferedReader reader = new BufferedReader(new StringReader(data));
        String line;
        while ((line = reader.readLine()) != null)
            map.put(line, reader.readLine());
    }

    public void flush() throws IOException {
        if (map.isEmpty()) return;

        StringBuilder cookie = new StringBuilder();
        for (String key : map.keySet())
            cookie.append(key).append("\n").append(map.get(key)).append("\n");

        IoUtils.writeAll(cookieFile, cookie.toString());
    }

    public synchronized void clear() {
        cookieFile.delete();
        map.clear();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public PersistentMap put(String key, String value) {
        map.put(key, value);
        return this;
    }

    public String get(String key) {
        return map.get(key);
    }

    public Set<String> keySet() {
        return map.keySet();
    }
}