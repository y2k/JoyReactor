package y2k.joyreactor.services.repository;

import y2k.joyreactor.Message;

import java.util.Collections;
import java.util.List;

/**
 * Created by y2k on 11/20/15.
 */
@Deprecated
public class MessageForUserQuery extends Repository.Query<Message> {

    private String name;

    public MessageForUserQuery(String name) {
        this.name = name;
    }

    @Override
    public boolean compare(Message message) {
        return name.equals(message.userName);
    }

    @Override
    public void sort(List<Message> items) {
        Collections.sort(items, (l, r) -> r.date.compareTo(l.date));
    }
}