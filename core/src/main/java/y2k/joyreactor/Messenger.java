package y2k.joyreactor;

import rx.functions.Action1;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by y2k on 01/10/15.
 */
public class Messenger {

    Map<Class, Observable> observers = new HashMap<>();

    public void send(Object message) {
        Observable observable = observers.get(message.getClass());
        if (observable != null) observable.notifyObservers(message);
    }

    public <T> void register(Object receiver, Action1<T> callback, Class type) {
        Observable observable = observers.get(type);
        if (observable == null) observers.put(type, observable = new Observable());
        observable.addObserver(new ObserverImpl<>(callback));
    }

    public void unregister(Object receiver) {
        // TODO:
    }

    public static Messenger getDefault() {
        return null;
    }

    private static class ObserverImpl<T> implements Observer {

        private Action1<T> callback;

        public ObserverImpl(Action1<T> callback) {
            this.callback = callback;
        }

        @Override
        public void update(Observable o, Object arg) {
            callback.call((T) arg);
        }
    }
}