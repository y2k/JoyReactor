package y2k.joyreactor.repository;

import y2k.joyreactor.Message;

/**
 * Created by y2k on 11/20/15.
 */
public class MessageForUser extends Repository.Query<Message> {

    private String name;

    public MessageForUser(String name) {
        this.name = name;
    }

    @Override
    public boolean compare(Message message) {
        return name.equals(message.userName);
    }
}