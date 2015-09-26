package y2k.joyreactor.platform;

import android.os.Handler;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 9/26/15.
 */
public class HandlerScheduler extends Scheduler {

    Handler handler = new Handler();

    @Override
    public Worker createWorker() {
        return new HandlerWorker(handler);
    }

    static class HandlerWorker extends Scheduler.Worker {

        private Handler handler;

        public HandlerWorker(Handler handler) {
            this.handler = handler;
        }

        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            handler.postDelayed(action::call, unit.toMillis(delayTime));
            return null;
        }

        @Override
        public void unsubscribe() {

        }

        @Override
        public boolean isUnsubscribed() {
            return false;
        }
    }
}