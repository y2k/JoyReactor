package y2k.joyreactor.repository;

import rx.Observable;
import y2k.joyreactor.platform.Platform;
import y2k.joyreactor.common.IoUtils;
import y2k.joyreactor.common.ObservableUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by y2k on 10/29/15.
 */
public class Repository<T> {

    private static final ExecutorService sSingleAccessExecutor = Executors.newSingleThreadExecutor();

    private File directory;

    public Repository(Class<T> cls) {
        String name = cls.getSimpleName().toLowerCase() + "s";
        directory = new File(new File(Platform.Instance.getCurrentDirectory(), "repositories"), name);
        directory.mkdirs();
    }

    public void insertAll(List<T> list) {
        insertAllAsync(list).toBlocking().single();
    }

    public Observable<Void> insertAllAsync(List<T> newRows) {
        return queryAsync()
                .map(rows -> {
                    List<T> result = new ArrayList<>(rows);
                    result.addAll(newRows);
                    return result;
                }).flatMap(this::replaceAllAsync);
    }

    public void deleteWhere(Query<T> query) {
        deleteWhereAsync(query).toBlocking().single();
    }

    public Observable<Void> deleteWhereAsync(Query<T> query) {
        return queryAsync(query).flatMap(this::replaceAllAsync);
    }

    public Observable<T> queryFirstAsync(Query query) {
        return ObservableUtils.create(() -> queryFirst(query));
    }

    public T queryFirst(Query<T> query) {
        List<T> list = queryAsync(query).toBlocking().first();
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public Observable<List<T>> queryAsync() {
        return queryAsync(new Query<T>() {

            @Override
            public boolean compare(T row) {
                return true;
            }
        });
    }

    @SuppressWarnings("unchecked")
    public Observable<List<T>> queryAsync(Query<T> query) {
        return query
                .initialize()
                .flatMap(s -> ObservableUtils.create(() -> innerQuery(query), sSingleAccessExecutor))
                .map(s -> {
                    query.sort((List) s);
                    return s;
                });
    }

    private List<T> innerQuery(Query<T> query) throws IOException, ClassNotFoundException {
        List<T> result = new ArrayList<>();
        for (File f : directory.listFiles()) {
            T row = readObject(f);
            if (query.compare(row)) result.add(row);
        }
        return result;
    }

    public Observable<Void> replaceAllAsync(List<T> rows) {
        return ObservableUtils.create(() -> {
            for (File f : directory.listFiles())
                f.delete();

            for (int i = 0; i < rows.size(); i++)
                writeObject(new File(directory, "" + i), rows.get(i));
        }, sSingleAccessExecutor);
    }

    public void insertOrUpdate(Query<T> query, T row) throws Exception {
        executeSync(() -> {
            query.initialize().toBlocking().single();

            File file = null;
            String[] fileNames = directory.list();
            for (String name : fileNames) {
                T old = readObject(new File(directory, name));
                if (query.compare(old)) {
                    file = new File(directory, name);
                    break;
                }
            }

            if (file == null) {
                int rowID = 0;
                for (String name : fileNames)
                    rowID = Math.max(rowID, Integer.parseInt(name));

                rowID++;
                setRowID(row, rowID);
                writeObject(new File(directory, "" + rowID), row);
            } else {
                file.delete();
                setRowID(row, Integer.parseInt(file.getName()));
                writeObject(file, row);
            }
            return null;
        });
    }

    private void setRowID(T row, int rowID) throws Exception {
        row.getClass().getField("id").setInt(row, rowID);
    }

    @SuppressWarnings("unchecked")
    private T readObject(File f) throws IOException, ClassNotFoundException {
        ObjectInputStream stream = new ObjectInputStream(new FileInputStream(f));
        try {
            return (T) stream.readObject();
        } finally {
            IoUtils.close(stream);
        }
    }

    private void writeObject(File file, T row) throws Exception {
        ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file));
        try {
            stream.writeObject(row);
        } finally {
            IoUtils.close(stream);
        }
    }

    private void executeSync(Callable<Void> action) throws ExecutionException, InterruptedException {
        sSingleAccessExecutor.submit(action).get();
    }

    public static abstract class Query<TRow> {

        public abstract boolean compare(TRow row);

        public Observable<Void> initialize() {
            return Observable.just(null);
        }

        public void sort(List<TRow> items) {
        }
    }
}