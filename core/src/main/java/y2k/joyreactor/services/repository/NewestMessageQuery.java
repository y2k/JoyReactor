package y2k.joyreactor.services.repository;

import y2k.joyreactor.Message;

/**
 * Created by y2k on 11/19/15.
 */
@Deprecated
public class NewestMessageQuery extends Repository.Query<Message> {

    private boolean isMine;
    private Message prev;

    public NewestMessageQuery(boolean isMine) {
        this.isMine = isMine;
    }

    @Override
    public boolean compare(Message message) {
        if (message.isMine != isMine)
            return false;

        if (prev == null)
            return true;

        boolean result = prev.date.compareTo(message.date) < 0;
        prev = message;
        return result;
    }
}