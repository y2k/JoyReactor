package y2k.joyreactor.services.requests;

import org.jsoup.nodes.Document;
import rx.Observable;
import y2k.joyreactor.Image;
import y2k.joyreactor.Profile;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 19/10/15.
 */
public class ProfileRequestFactory {

    public Observable<Profile> request() {
        return new UserNameRequest()
                .request()
                .flatMap(username -> ObservableUtils.create(() -> {
                    Document page = HttpClient.getInstance().getDocument(getUrl(username));
                    return new ProfileParser(page).parse();
                }));
    }

    private String getUrl(String username) {
        if (username == null) throw new RuntimeException();
        return "http://joyreactor.cc/user/" + username;
    }

    private static class ProfileParser {

        private Document document;

        ProfileParser(Document document) {
            this.document = document;
        }

        Profile parse() {
            Profile profile = new Profile();
            profile.userName = document.select("div.sidebarContent > div.user > span").text();
            profile.userImage = new Image(document.select("div.sidebarContent > div.user > img").attr("src"), 0, 0);
            profile.progressToNewStar = getProgressToNewStar();
            profile.rating = Float.parseFloat(document.select("#rating-text > b").text());
            profile.stars = document.select(".star-row-0 > .star-0").size();
            return profile;
        }

        private float getProgressToNewStar() {
            String style = document.select("div.poll_res_bg_active").first().attr("style");
            Matcher m = Pattern.compile("width:(\\d+)%;").matcher(style);
            if (!m.find()) throw new IllegalStateException();
            return Float.parseFloat(m.group(1));
        }
    }
}