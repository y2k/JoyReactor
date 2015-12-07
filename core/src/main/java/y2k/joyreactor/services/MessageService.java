package y2k.joyreactor.services;

import rx.Observable;
import y2k.joyreactor.Message;
import y2k.joyreactor.services.repository.MessageForUserQuery;
import y2k.joyreactor.services.repository.MessageThreadQuery;
import y2k.joyreactor.services.repository.Repository;
import y2k.joyreactor.services.synchronizers.PrivateMessageSynchronizer;

import java.util.List;

/**
 * Created by y2k on 12/8/15.
 */
public class MessageService {

    private PrivateMessageSynchronizer fetcher;
    private Repository<Message> repository;

    public MessageService(PrivateMessageSynchronizer fetcher, Repository<Message> repository) {
        this.fetcher = fetcher;
        this.repository = repository;
    }

    public Observable<List<Message>> queryAsync(String username) {
        return repository.queryAsync(new MessageForUserQuery(username));
    }

    public Observable<List<Message>> get() {
        return getFromRepo().mergeWith(fetcher.execute().flatMap(s -> getFromRepo()));
    }

    private Observable<List<Message>> getFromRepo() {
        return repository.queryAsync(new MessageThreadQuery());
    }
}