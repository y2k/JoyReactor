package y2k.joyreactor;

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
        }, MessageThreadsPresenter.MessageThreadSelected.class);
    }

    public void reply(String message) {
        // TODO:
    }

    public interface View {

        void updateMessages(Message[] messages);
    }
}