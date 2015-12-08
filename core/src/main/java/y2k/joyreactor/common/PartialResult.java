package y2k.joyreactor.common;

/**
 * Created by y2k on 08/12/15.
 */
public class PartialResult<T> {

    public T result;

    public int progress;
    public int max;

    public static <T> PartialResult<T> complete(T data) {
        PartialResult<T> result = new PartialResult<>();
        result.result = data;
        return result;
    }

    public static <T> PartialResult<T> inProgress(int progress, int max) {
        PartialResult<T> result = new PartialResult<>();
        result.progress = progress;
        result.max = max;
        return result;
    }
}