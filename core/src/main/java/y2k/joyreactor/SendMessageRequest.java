package y2k.joyreactor;

import rx.Observable;

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
            new HttpClient()
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