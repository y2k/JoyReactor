package y2k.joyreactor;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    private MessageThread.Collection threads;

    public MessageThreadsPresenter(View view) {
        view.setIsBusy(true);
        MessageThread.Collection
                .request()
                .subscribe(threads -> {
                    view.setIsBusy(false);
                    view.reloadData(threads);
                });
    }

    public void selectThread(int index) {
        Messenger.getDefault().send(new MessageThreadSelected(threads.get(index)));
    }

    public static class MessageThreadSelected {

        final MessageThread thread;

        public MessageThreadSelected(MessageThread thread) {
            this.thread = thread;
        }
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void reloadData(MessageThread.Collection threads);
    }
}