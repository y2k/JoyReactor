package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThread {

    String username;
    String userImage;
    String lastMessage;
    Date date;

    public static Observable<List<MessageThread>> request() {
        return new MessageThreadRequest().request();
    }

    private static class MessageThreadRequest extends ArrayList<MessageThread> {

        Observable<List<MessageThread>> request() {
            return ObservableUtils.create(() -> {
                List<MessageThread> threads = new ArrayList<>();
                new MessagePageIterator()
                        .observable()
                        .flatMap(s -> new Parser(s).parse())
                        .filter(s -> !alreadyAdded(threads, s.username))
                        .forEach(threads::add);
                return threads;
            });
        }

        private boolean alreadyAdded(List<MessageThread> threads, String name) {
            for (MessageThread t : threads)
                if (t.username.equals(name)) return true;
            return false;
        }

        private static class Parser {

            private Document document;

            Parser(Document document) {
                this.document = document;
            }

            Observable<MessageThread> parse() {
                return Observable
                        .from(document.select("div.messages_wr > div.article"))
                        .map(s -> {
                            MessageThread thread = new MessageThread();
                            thread.username = s.select("div.mess_from > a").text();
                            thread.userImage = new UserImageRequest(thread.username).execute();
                            thread.lastMessage = s.select("div.mess_text").text();
                            thread.date = new Date(1000 * Long.parseLong(s.select("span[data-time]").attr("data-time")));
                            return thread;
                        });
            }
        }
    }
}