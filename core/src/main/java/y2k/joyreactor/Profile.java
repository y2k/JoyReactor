package y2k.joyreactor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/30/15.
 */
public class Profile {

    public String userName;
    public String userImage;
    public float rating;
    public int stars;
    public float progressToNewStar;

    static Observable<Profile> requestMine() {
        return request(getMyUserName());
    }

    private static String getMyUserName() {
        return "Krowly"; // TODO:
    }

    static Observable<Profile> request(String username) {
        return new HttpClient()
                .getDocumentAsync(getUrl(username))
                .map(document -> new ProfileParser(document).parse());
    }

    private static String getUrl(String username) {
        return "http://joyreactor.cc/user/" + username;
    }

    static class ProfileParser {

        private Document document;

        ProfileParser(Document document) {
            this.document = document;
        }

        Profile parse() {
            Profile profile = new Profile();
            profile.userName = getAvatarNode().attr("alt");
            profile.userImage = getAvatarNode().attr("src");
            profile.progressToNewStar = getProgressToNewStar();
            profile.rating = Float.parseFloat(document.select("#rating-text > b").text());
            profile.stars = document.select(".star-row-0 > .star-0").size();
            return profile;
        }

        private Element getAvatarNode() {
            return document.select("img.avatar").first();
        }

        private float getProgressToNewStar() {
            String style = document.select("div.poll_res_bg_active").first().attr("style");
            Matcher m = Pattern.compile("width:(\\d+)%;").matcher(style);
            if (!m.find()) throw new IllegalStateException();
            return Float.parseFloat(m.group(1));
        }
    }
}