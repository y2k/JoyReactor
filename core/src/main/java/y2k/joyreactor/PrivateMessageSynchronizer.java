package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.repository.MessageForDateQuery;
import y2k.joyreactor.repository.Repository;
import y2k.joyreactor.requests.MessageListRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 11/17/15.
 */
public class PrivateMessageSynchronizer {

    private MessageListRequest request = new MessageListRequest();
    private Repository<Message> messageRepository = new Repository<>(Message.class);
    private Repository<MessageThread> threadRepository = new Repository<>(MessageThread.class);

    private Date mineOldest;
    private Date theirOldest;

    public Observable async() {
        return ObservableUtils.create(() -> {
            while (true) {
                request.execute();

                updateLastMessageDates();
                boolean needLoadNext = isNeedLoadNext();

                new Saver().save(request.getMessages());

                if (request.getNextPage() == null)
                    break;
                if (!needLoadNext)
                    break;
            }
        });
    }

    private void updateLastMessageDates() {
        mineOldest = theirOldest = null;
        for (Message m : request.getMessages()) {
            if (m.isMine) mineOldest = m.date;
            else theirOldest = m.date;
        }
    }

    private boolean isNeedLoadNext() {
        if (mineOldest != null) {
            if (messageRepository.queryFirst(new MessageForDateQuery(mineOldest, true)) == null)
                return true;
        }
        if (theirOldest != null) {
            if (messageRepository.queryFirst(new MessageForDateQuery(theirOldest, false)) == null)
                return true;
        }
        return false;
    }

    class Saver {

        public void save(List<Message> messages) {
            // TODO:




        }
    }
}