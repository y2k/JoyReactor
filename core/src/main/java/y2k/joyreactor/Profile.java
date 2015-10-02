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
        return new ProfileRequest().request();
    }

    private static class ProfileRequest {

        public Observable<Profile> request() {
            return getMyUserName()
                    .flatMap(username -> new HttpClient().getDocumentAsync(getUrl(username)))
                    .map(document -> new ProfileParser(document).parse());
        }

        private Observable<String> getMyUserName() {
            return new HttpClient()
                    .getDocumentAsync("http://joyreactor.cc/donate")
                    .map(document -> document.select("a#settings").text());
        }

        private String getUrl(String username) {
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
                profile.userImage = document.select("div.sidebarContent > div.user > img").attr("src");
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
}