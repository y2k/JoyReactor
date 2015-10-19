package y2k.joyreactor.common;

import rx.Scheduler;

/**
 * Created by y2k on 9/26/15.
 */
public class ForegroundScheduler {

    private static Scheduler instance;

    public static Scheduler getInstance() {
        return instance;
    }

    public static void setInstance(Scheduler instance) {
        ForegroundScheduler.instance = instance;
    }
}
