package y2k.joyreactor.repository;

import rx.Observable;
import y2k.joyreactor.Platform;
import y2k.joyreactor.common.IoUtils;
import y2k.joyreactor.common.ObservableUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by y2k on 10/29/15.
 */
public class Repository<T> {

    private static final Executor sSingleAccessExecutor = Executors.newSingleThreadExecutor();

    private static final int VERSION = 1;

    private File file;

    public Repository(Class<T> cls) {
        String name = cls.getName().toLowerCase();
        file = new File(new File(Platform.Instance.getCurrentDirectory(), "repositories"), name + "." + VERSION + ".dat");
        file.getParentFile().mkdirs();
    }

    public Observable<Void> insertAll(List<T> newRows) {
        return queryAsync()
                .map(rows -> {
                    rows.addAll(newRows);
                    return rows;
                }).flatMap(this::replaceAllAsync);
    }

    public Observable<Void> deleteWhere(Query query) {
        return queryAsync(query).flatMap(this::replaceAllAsync);
    }

    public Observable<List<T>> queryAsync() {
        return queryAsync(new Query() {

            @Override
            public Observable<Void> init() {
                return Observable.just(null);
            }

            @Override
            public boolean compare(Object row) {
                return true;
            }
        });
    }

    public Observable<List<T>> queryAsync(Query query) {
        return ObservableUtils.create(() -> {
            if (!file.exists())
                return Collections.emptyList();

            try {
                ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file));
                List<T> buffer = new ArrayList<>();
                try {
                    while (true) {
                        T row = (T) stream.readObject();
                        if (query.compare(row)) buffer.add(row);
                    }
                } catch (EOFException e) {
                } finally {
                    IoUtils.close(stream);
                }
                return buffer;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, sSingleAccessExecutor);
    }

    public Observable<Void> replaceAllAsync(List<T> rows) {
        return ObservableUtils.create(() -> {
            try {
                if (rows.isEmpty()) {
                    file.delete();
                } else {
                    ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
                    try {
                        for (T row : rows) stream.writeObject(row);
                    } finally {
                        IoUtils.close(stream);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, sSingleAccessExecutor);
    }

    public interface Query<T> {

        boolean compare(T row);
    }
}