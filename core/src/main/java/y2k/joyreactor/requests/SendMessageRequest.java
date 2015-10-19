package y2k.joyreactor.requests;

import rx.Observable;
import y2k.joyreactor.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

/**
 * Created by y2k on 10/2/15.
 */
public class SendMessageRequest {

    private String username;

    public SendMessageRequest(String username) {
        this.username = username;
    }

    public Observable<Void> request(String message) {
        return ObservableUtils.create(() -> {
            HttpClient.getInstance()
                    .beginForm()
                    .put("username", username)
                    .put("text", message)
                    .putHeader("X-Requested-With", "XMLHttpRequest")
                    .putHeader("Referer", "http://joyreactor.cc/private/list")
                    .send("http://joyreactor.cc/private/create");
            return null;
        });
    }
}