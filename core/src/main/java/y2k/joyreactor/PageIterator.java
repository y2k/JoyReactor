package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by igor on 10/1/15.
 */
public class PageIterator {

    private static final String START_URL = "http://joyreactor.cc/private/list";
    private Document page;

    public boolean hasNext() {
        return getUrlForNext() != null;
    }

    public Document next() throws IOException {
        return page = new HttpClient().getDocument(getUrlForNext());
    }

    private String getUrlForNext() {
        if (page == null) return START_URL;
        Element nextNode = page.select("a.hasNext").first();
        return nextNode == null ? null : nextNode.absUrl("href");
    }
}