package y2k.joyreactor.presenters;

import rx.functions.Action0;
import rx.functions.Action1;
import y2k.joyreactor.Messenger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 9/27/15.
 */
public abstract class Presenter {

    ActivateMessageHolder messages = new ActivateMessageHolder();

    protected ActivateMessageHolder getMessages() {
        return messages;
    }

    public void activate() {
        messages.activate();
    }

    public void deactivate() {
        messages.deactivate();
    }

    class ActivateMessageHolder {

        private List<Action0> actions = new ArrayList<>();

        <T> void add(Action1<T> callback, Class<T> type) {
            actions.add(() -> Messenger.getInstance().register(Presenter.this, callback, type));
        }

        void activate() {
            for (Action0 s : actions)
                s.call();
        }

        void deactivate() {
            Messenger.getInstance().unregister(Presenter.this);
        }
    }
}