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
                PageIterator pages = new PageIterator("http://joyreactor.cc/private/list");
                while (pages.hasNext())
                    messageThreads.addAll(new Parser(pages.next()).parse());
                return messageThreads;
            });
        }

        private static class PageIterator {

            private String startUrl;
            private Document page;

            public PageIterator(String startUrl) {
                this.startUrl = startUrl;
            }

            public boolean hasNext() {
                return getUrlForNext() != null;
            }

            public Document next() throws IOException {
                return page = new HttpClient().getDocument(getUrlForNext());
            }

            private String getUrlForNext() {
                if (page == null) return startUrl;
                Element nextNode = page.select("a.hasNext").first();
                return nextNode == null ? null : nextNode.absUrl("href");
            }
        }

        private static class Parser {

            private Collection threads = new Collection();
            private Document document;

            Parser(Document document) {
                this.document = document;
            }

            Collection parse() {
                Elements articles = document.select("div.messages_wr > div.article");
                for (Element a : articles) {
                    MessageThread thread = new MessageThread();
                    thread.userName = a.select("div.mess_from > a").text();
                    if (!contains(thread.userName)) {
                        thread.userImage = new UserImageRequest(thread.userName).execute();
                        thread.lastMessage = a.select("div.mess_text").text();
                        thread.date = new Date(1000 * Long.parseLong(a.select("span[data-time]").attr("data-time")));
                        threads.add(thread);
                    }
                }
                return threads;
            }

            private boolean contains(String name) {
                for (MessageThread t : threads)
                    if (t.userName.equals(name)) return true;
                return false;
            }
        }
    }
}