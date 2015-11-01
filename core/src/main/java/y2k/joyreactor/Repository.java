package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.common.IoUtils;
import y2k.joyreactor.common.ObservableUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 10/29/15.
 */
public class Repository<T> {

    private List<T> inMemoryCache = new ArrayList<>();
    private File file;

    public Repository(String name, int version) {
        file = new File(new File(Platform.Instance.getCurrentDirectory(), "repositories"), name + "." + version + ".dat");
        file.getParentFile().mkdirs();
    }

    public Observable<Void> clearAsync() {
        return ObservableUtils.create(this::clear);
    }

    public void clear() {
        inMemoryCache.clear();
        dumpAll();
    }

    public void add(T row) {
        inMemoryCache.add(row);
        dumpAll();
    }

    public void replaceAll(List<T> rows) {
        inMemoryCache.clear();
        inMemoryCache.addAll(rows);
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

    public Observable<List<T>> queryAsync() {
        return ObservableUtils.create(this::query);
    }

    public List<T> query() {
        if (inMemoryCache.isEmpty()) loadFromFile();
        return new ArrayList<>(inMemoryCache);
    }

    private void loadFromFile() {
        if (!file.exists())
            return;
        try {
            ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
            try {
                while (true)
                    inMemoryCache.add((T) stream.readObject());
            } catch (EOFException e) {
            } finally {
                IoUtils.close(stream);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}