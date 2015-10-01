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
    HashMap<ObserverImpl, Object> registrations = new HashMap<>();

    public void send(Object message) {
        Observable observable = observers.get(message.getClass());
        if (observable != null) observable.notifyObservers(message);
    }

    public <T> void register(Object receiver, Action1<T> callback, Class<T> type) {
        Observable observable = observers.get(type);
        if (observable == null) observers.put(type, observable = new Observable());

        ObserverImpl<T> o = new ObserverImpl<>(callback);
        observable.addObserver(o);

        registrations.put(o, receiver);
    }

    public void unregister(Object receiver) {
        while (registrations.values().remove(receiver)) ;
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