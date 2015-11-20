package y2k.joyreactor;

import rx.Observable;
import y2k.joyreactor.requests.MessageRequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 10/1/15.
 */
public class Message implements Serializable {

    public String text;
    public Date date;
    public boolean isMine;
    public String userName;

    public static Observable<List<Message>> request(String name) {
        return new MessageRequest().request(name);
    }
}