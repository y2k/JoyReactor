package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import rx.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThread {

    String userName;
    String userImage;
    String lastMessage;
    Date date;

    public static class Collection extends ArrayList<MessageThread> {

        public static Observable<Collection> request() {
            return ObservableUtils.create(() -> {
                Collection messageThreads = new Collection();
                String url = "http://joyreactor.cc/private/list";
                while (url != null) {
                    Document page = new HttpClient().getDocument(url);
                    messageThreads.addAll(getMessageThreads(page));
                    Element nodeNext = page.select("a.next").first();
                    url = nodeNext == null ? null : nodeNext.absUrl("href");
                }
                return messageThreads;
            });
        }

        private static Collection getMessageThreads(Document doc) throws IOException {

            Elements articles = doc.select("div.messages_wr > div.article");
            Collection messageThreads = new Collection();
            for (Element a : articles) {
                MessageThread thread = new MessageThread();
                thread.userName = a.select("div.mess_from > a").text();
                if (!contains(messageThreads, thread.userName)) {
                    thread.userImage = new UserImageRequest(thread.userName).execute();
                    thread.lastMessage = a.select("div.mess_text").text();
                    thread.date = new Date(1000 * Long.parseLong(a.select("span[data-time]").attr("data-time")));
                    messageThreads.add(thread);
                }
            }

            return messageThreads;
        }

        private static boolean contains(Collection threads, String name) {
            for (MessageThread t : threads)
                if (t.userName.equals(name)) return true;
            return false;
        }
    }
}