package y2k.joyreactor.repository;

import y2k.joyreactor.Message;

import java.util.Date;

/**
 * Created by y2k on 11/19/15.
 */
public class MessageForDateQuery extends Repository.Query<Message> {

    private Date date;
    private boolean isMine;

    public MessageForDateQuery(Date date, boolean isMine) {
        this.date = date;
        this.isMine = isMine;
    }

    @Override
    public boolean compare(Message message) {
        if (message.isMine != isMine)
            return false;
        if (message.date.compareTo(date) != 0)
            return false;
        return true;
    }
}