package y2k.joyreactor.presenters;

import rx.Observable;
import y2k.joyreactor.MessageThread;
import y2k.joyreactor.PrivateMessageSynchronizer;
import y2k.joyreactor.common.Messenger;
import y2k.joyreactor.repository.Repository;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    private Repository<MessageThread> repository;
    private PrivateMessageSynchronizer fetcher;

    private List<MessageThread> threads;

    public MessageThreadsPresenter(View view) {
        this(view, new Repository<>(MessageThread.class), new PrivateMessageSynchronizer());
    }

    public MessageThreadsPresenter(View view, Repository<MessageThread> repository, PrivateMessageSynchronizer fetcher) {
        this.repository = repository;
        this.fetcher = fetcher;

        view.setIsBusy(true);
        request().subscribe(threads -> {
            view.setIsBusy(false);
            view.reloadData(threads);
        }, Throwable::printStackTrace);
    }

    public Observable<List<MessageThread>> request() {
        return repository.queryAsync()
                .mergeWith(fetcher.async().flatMap(s -> repository.queryAsync()));
    }

    public void selectThread(int index) {
        Messenger.getInstance().send(new ThreadSelectedMessage(threads.get(index)));
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