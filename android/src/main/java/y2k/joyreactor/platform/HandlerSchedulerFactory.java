package y2k.joyreactor.platform;

import android.os.Handler;
import android.os.Looper;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by y2k on 9/26/15.
 */
public class HandlerSchedulerFactory {

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public Scheduler make() {
        return Schedulers.from(command -> sHandler.post(command));
    }
}