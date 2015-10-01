package y2k.joyreactor;

/**
 * Created by y2k on 01/10/15.
 */
public class MessagesPresenter {

    public MessagesPresenter(View view) {
        Messenger.getDefault().register(this, (MessageThreadsPresenter.MessageThreadSelected m) -> {
            // TODO:
        });
    }

    public void reply(String message) {
        // TODO:
    }

    public interface View {
        //
    }
}