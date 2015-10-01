package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by igor on 10/1/15.
 */
public class Message {

    String userName;
    String userImage;
    String lastMessage;
    Date date;
    boolean isMine;

    public static Observable<Message[]> request(String name) {
        return ObservableUtils.create(() -> {

            List<Message> messages = new ArrayList<>();

            PageIterator iterator = new PageIterator();
            while (iterator.hasNext()) {
                // TODO:
                new Parser(iterator.next())
                        .parse()
                        .filter(s -> s.userName.equals(name))
                        .forEach(messages::add);
            }

            return messages.toArray(new Message[messages.size()]);
        });
    }

    public static class Parser {

        private Document document;

        Parser(Document document) {
            this.document = document;
        }

        Observable<Message> parse() {
            return Observable
                    .from(document.select("div.messages_wr > div.article"))
                    .map(s -> {
                        Message message = new Message();
                        message.userName = s.select("div.mess_from > a").text();
                        message.userImage = new UserImageRequest(message.userName).execute();
                        message.lastMessage = s.select("div.mess_text").text();
                        message.date = new Date(1000 * Long.parseLong(s.select("span[data-time]").attr("data-time")));
                        message.isMine = s.select("div.mess_reply").isEmpty();
                        return message;
                    });
        }
    }
}