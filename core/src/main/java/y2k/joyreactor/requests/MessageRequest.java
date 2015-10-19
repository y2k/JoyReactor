package y2k.joyreactor.requests;

import org.jsoup.nodes.Document;
import rx.Observable;
import y2k.joyreactor.Message;
import y2k.joyreactor.common.ObservableUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 19/10/15.
 */
public class MessageRequest {

    public Observable<List<Message>> request(String name) {
        return ObservableUtils.create(() -> {
            List<Message> messages = new ArrayList<>();
            new MessagePageIterator()
                    .observable()
                    .flatMap(s -> new Parser(s, name).parse())
                    .forEach(messages::add);
            Collections.sort(messages, (left, right) -> right.date.compareTo(left.date));
            return messages;
        });
    }

    private static class Parser {

        private Document document;
        private String name;

        Parser(Document document, String name) {
            this.document = document;
            this.name = name;
        }

        Observable<Message> parse() {
            return Observable
                    .from(document.select("div.messages_wr > div.article"))
                    .filter(s -> name.equals(s.select("div.mess_from > a").text()))
                    .map(s -> {
                        Message message = new Message();
                        message.text = s.select("div.mess_text").text();
                        message.date = new Date(1000 * Long.parseLong(s.select("span[data-time]").attr("data-time")));
                        message.isMine = s.select("div.mess_reply").isEmpty();
                        return message;
                    });
        }
    }
}