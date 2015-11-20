package y2k.joyreactor.repository;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import y2k.joyreactor.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by y2k on 11/20/15.
 */
public class MessageThreadQuery extends Repository.Query<Message> {

    private Set<String> usersAlreadyAdded = new HashSet<>();

    @Override
    public boolean compare(Message message) {

        System.out.println("QUERY | " + message);

        if (usersAlreadyAdded.contains(message.userName))
            return false;
        usersAlreadyAdded.add(message.userName);
        return true;
    }
}