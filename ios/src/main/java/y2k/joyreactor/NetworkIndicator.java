package y2k.joyreactor;

import org.robovm.apple.uikit.UIApplication;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.TimeUnit;

/**
 * Created by y2k on 10/13/15.
 */
public class NetworkIndicator {

    private Subscriber<? super Object> subscriber;

    public NetworkIndicator() {
        Observable
                .create(subscriber -> this.subscriber = subscriber)
                .buffer(500, TimeUnit.MILLISECONDS)
                .filter(s -> s.size() > 0)
                .map(s -> s.get(s.size() - 1))
                .observeOn(ForegroundScheduler.getInstance())
                .subscribe(s -> update((Boolean) s));
    }

    public void setEnabled(boolean isEnabled) {
        subscriber.onNext(isEnabled);
    }

    private void update(boolean isEnabled) {
        UIApplication.getSharedApplication().setNetworkActivityIndicatorVisible(isEnabled);
    }
}