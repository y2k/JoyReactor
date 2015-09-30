package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;

/**
 * Created by y2k on 9/30/15.
 */
public class LoginRequest {

    String username;
    String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Observable<Void> request() {
        return ObservableUtils.create(() -> {
            Document doc = new HttpClient()
                    .beginForm()
                    .put("signin[username]", username)
                    .put("signin[password]", password)
                    .put("signin[remember]", "on")
                    .put("signin[_csrf_token]", getCsrf())
                    .send("http://joyreactor.cc/login");
            if (doc.getElementById("logout") == null)
                throw new IllegalStateException();
            return null;
        });
    }

    private String getCsrf() throws IOException {
        Document document = new HttpClient().getDocument("http://joyreactor.cc/login");
        return document.getElementById("signin__csrf_token").attr("value");
    }
}