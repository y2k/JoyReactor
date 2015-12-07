package y2k.joyreactor.presenters;

import y2k.joyreactor.Message;
import y2k.joyreactor.services.MessageService;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    public MessageThreadsPresenter(View view, MessageService service) {
        view.setIsBusy(true);
        service.get()
                .subscribe(threads -> {
                    view.setIsBusy(false);
                    view.reloadData(threads);
                }, Throwable::printStackTrace);
    }

    public void selectThread(int index) {
        // TODO:
//        Messenger.getInstance().send(new ThreadSelectedMessage(threads.get(index)));
    }

    public static class ThreadSelectedMessage {

        final Message thread;

        public ThreadSelectedMessage(Message thread) {
            this.thread = thread;
        }
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void reloadData(List<Message> threads);
    }
}