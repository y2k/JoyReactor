package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import y2k.joyreactor.Message;
import y2k.joyreactor.http.HttpClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by y2k on 11/17/15.
 */
public class MessageListRequest {

    public List<Message> getMessages() {
        return messages;
    }

    public String getNextPage() {
        return nextPage;
    }

    private List<Message> messages = new ArrayList<>();
    private String nextPage;

    public void execute(String page) throws Exception {
        String url = page != null ? page : "http://joyreactor.cc/private/list";
        Document document = HttpClient.getInstance().getDocument(url);

        for (Element s : document.select("div.messages_wr > div.article")) {
            Message m = new Message();
            m.userName = s.select("div.mess_from > a").text();
            m.userImage = new UserImageRequest(m.userName).execute();
            m.text = s.select("div.mess_text").text();
            m.date = new Date(1000 * Long.parseLong(s.select("span[data-time]").attr("data-time")));
            m.isMine = s.select("div.mess_reply").isEmpty();
            messages.add(m);
        }

        Element nextNode = document.select("a.next").first();
        nextPage = nextNode == null ? null : nextNode.absUrl("href");

        loadUserImages();
    }

    private void loadUserImages() {
        for (Message m : messages)
            m.userImage = new UserImageRequest(m.userName).execute();
    }
}