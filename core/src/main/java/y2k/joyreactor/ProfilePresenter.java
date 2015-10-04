package y2k.joyreactor;

import org.jsoup.nodes.Document;
import rx.Observable;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by y2k on 9/30/15.
 */
public class ProfilePresenter {

    private final View view;

    public ProfilePresenter(View view) {
        this.view = view;

        this.view.setProgress(true);
        new ProfileRequest()
                .request()
                .subscribe(profile -> {
                    this.view.setProfile(profile);
                    this.view.setProgress(false);
                }, e -> {
                    e.printStackTrace();
                    Navigation.getInstance().switchProfileToLogin();
                });
    }

    public void logout() {
        new HttpClient().clearCookies();
        Navigation.getInstance().switchProfileToLogin();
    }

    public interface View {

        void setProfile(Profile profile);

        void setProgress(boolean isProgress);
    }

    private static class ProfileRequest {

        public Observable<Profile> request() {
            return new UsernameRequest()
                    .request()
                    .flatMap(username -> ObservableUtils.create(() -> {
                        Document page = new HttpClient().getDocument(getUrl(username));
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