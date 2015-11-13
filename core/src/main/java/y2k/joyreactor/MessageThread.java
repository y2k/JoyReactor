package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.requests.MessageThreadRequest;

import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThread {

    public String userName;
    public String userImage;
    public String lastMessage;
    public Date date;

    public static Observable<List<MessageThread>> request() {
        return new MessageThreadRequest().request();
    }
}