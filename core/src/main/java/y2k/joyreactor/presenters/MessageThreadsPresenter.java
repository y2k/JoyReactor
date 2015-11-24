package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.Message;
import y2k.joyreactor.synchronizers.PrivateMessageSynchronizer;
import y2k.joyreactor.services.repository.MessageThreadQuery;
import y2k.joyreactor.services.repository.Repository;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    private Repository<Message> repository;
    private PrivateMessageSynchronizer fetcher;

    public MessageThreadsPresenter(View view) {
        this(view, new Repository<>(Message.class), new PrivateMessageSynchronizer());
    }

    public MessageThreadsPresenter(View view, Repository<Message> repository, PrivateMessageSynchronizer fetcher) {
        this.repository = repository;
        this.fetcher = fetcher;

        view.setIsBusy(true);
        get().subscribe(threads -> {
            view.setIsBusy(false);
            view.reloadData(threads);
        }, Throwable::printStackTrace);
    }

    private Observable<List<Message>> get() {
        return getFromRepo().mergeWith(fetcher.execute().flatMap(s -> getFromRepo()));
    }

    private Observable<List<Message>> getFromRepo() {
        return repository.queryAsync(new MessageThreadQuery());
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