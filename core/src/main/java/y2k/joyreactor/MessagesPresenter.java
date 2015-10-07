package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessagesPresenter extends Presenter {

    private View view;

    public MessagesPresenter(View view) {
        this.view = view;

        // FIXME:
        reloadMessages(getUsername());
    }

    @Override
    public void activate() {
        super.activate();
        Messenger.getInstance().register(this,
                m -> reloadMessages(m.thread.username),
                MessageThreadsPresenter.ThreadSelectedMessage.class);
    }

    public void reply(String message) {
        view.setIdBusy(true);
        new SendMessageRequest(getUsername())
                .request(message)
                .subscribe(s -> reloadMessages(getUsername()), Throwable::printStackTrace);
    }

    private void reloadMessages(String username) {
        view.setIdBusy(true);
        Message.request(username)
                .subscribe((messages) -> {
                    view.updateMessages(messages);
                    view.setIdBusy(false);
                }, Throwable::printStackTrace);
    }

    private String getUsername() {
        return "user500"; // TODO:
    }

    public interface View {

        void updateMessages(List<Message> messages);

        void setIdBusy(boolean isBusy);
    }
}