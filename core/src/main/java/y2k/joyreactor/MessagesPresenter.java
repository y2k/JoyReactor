package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessagesPresenter extends Presenter {

    private View view;

    public MessagesPresenter(View view) {
        this.view = view;
    }

    @Override
    public void activate() {
        super.activate();
        Messenger.getDefault().register(this, m -> {
            Message.request(m.thread.userName)
                    .subscribe(view::updateMessages, Throwable::printStackTrace);
        }, MessageThreadsPresenter.ThreadSelectedMessage.class);
    }

    public void reply(String message) {
        // TODO:
    }

    public interface View {

        void updateMessages(List<Message> messages);
    }
}