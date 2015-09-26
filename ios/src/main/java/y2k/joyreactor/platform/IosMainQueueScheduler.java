package y2k.joyreactor.platform;

import org.robovm.apple.dispatch.DispatchQueue;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 9/26/15.
 */
public class IosMainQueueScheduler extends Scheduler {

    @Override
    public Scheduler.Worker createWorker() {
        return new IosMainThreadWorker();
    }

    static class IosMainThreadWorker extends Scheduler.Worker {

        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            System.out.println("Schedule (delay = " + delayTime + ", unit = " + unit + ")");
            DispatchQueue.getMainQueue().after(delayTime, unit, action::call);
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
