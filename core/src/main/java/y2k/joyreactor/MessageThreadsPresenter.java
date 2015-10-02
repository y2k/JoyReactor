package y2k.joyreactor;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    private List<MessageThread> threads;

    public MessageThreadsPresenter(View view) {
        view.setIsBusy(true);
        MessageThread.request()
                .subscribe(threads -> {
                    view.setIsBusy(false);
                    view.reloadData(threads);
                }, Throwable::printStackTrace);
    }

    public void selectThread(int index) {
        Messenger.getDefault().send(new ThreadSelectedMessage(threads.get(index)));
    }

    public static class ThreadSelectedMessage {

        final MessageThread thread;

        public ThreadSelectedMessage(MessageThread thread) {
            this.thread = thread;
        }
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void reloadData(List<MessageThread> threads);
    }
}