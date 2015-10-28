package y2k.joyreactor;

import y2k.joyreactor.common.IoUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 10/29/15.
 */
public class Repository<T> {

    private List<T> inMemoryCache = new ArrayList<>();
    private File file;

    public Repository(String name) {
        file = new File(Platform.Instance.getCurrentDirectory(), name);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!file.exists())
            return;

        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            try {
                while (stream.available() > 0)
                    inMemoryCache.add((T) stream.readObject());
            } finally {
                IoUtils.close(stream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        inMemoryCache.clear();
        dumpAll();
    }

    public void add(T row) {
        inMemoryCache.add(row);
        dumpAll();
    }

    private void dumpAll() {
        try {
            if (inMemoryCache.isEmpty()) {
                file.delete();
            } else {
                ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
                try {
                    for (T row : inMemoryCache) stream.writeObject(row);
                } finally {
                    IoUtils.close(stream);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getAll() {
        return inMemoryCache;
    }
}