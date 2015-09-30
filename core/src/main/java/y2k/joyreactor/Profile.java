package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;

import java.io.IOException;
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

    static Observable<Profile> request() {
        return ObservableUtils.create(() -> new ProfileRequest("Krowly").execute());
    }

    static class ProfileRequest {

        private String username;

        ProfileRequest(String username) {
            this.username = username;
        }

        Profile execute() throws IOException {
            Document doc = new HttpClient().getDocument(getUrl());
            ProfileParser parser = new ProfileParser(doc);

            Profile profile = new Profile();
            profile.userName = username;
            profile.userImage = parser.getUserImage();
            profile.progressToNewStar = parser.getProgressToNewStar();
            profile.rating = parser.getRating();
            profile.stars = parser.getStars();

            return profile;
        }

        private String getUrl() {
            return "http://joyreactor.cc/user/" + username;
        }

        static class ProfileParser {

            private Document document;

            ProfileParser(Document document) {
                this.document = document;
            }

            String getUserImage() {
                return document.select("img.avatar").first().attr("src");
            }

            float getProgressToNewStar() {
                String style = document.select("div.poll_res_bg_active").first().attr("style");
                Matcher m = Pattern.compile("width:(\\d+)%;").matcher(style);
                if (!m.find()) throw new IllegalStateException();
                return Float.parseFloat(m.group(1));
            }

            float getRating() {
                return Float.parseFloat(document.select("#rating-text > b").text());
            }

            int getStars() {
                return document.select(".star-row-0 > .star-0").size();
            }
        }
    }
}