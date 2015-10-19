package y2k.joyreactor.presenters;

import org.jsoup.nodes.Document;
import rx.Observable;
import y2k.joyreactor.Navigation;
import y2k.joyreactor.common.ObservableUtils;
import y2k.joyreactor.http.HttpClient;

import java.net.URLEncoder;

/**
 * Created by y2k on 08/10/15.
 */
public class AddTagPresenter {

    private View view;

    public AddTagPresenter(View view) {
        this.view = view;
    }

    public void addTag() {
        view.setIsBusy(true);
        new AddTagRequest(view.getTagName())
                .request()
                .subscribe(s -> {
                    view.setIsBusy(false);
                    Navigation.getInstance().closeAddTag();
                }, Throwable::printStackTrace);
    }

    public interface View {

        String getTagName();

        void setIsBusy(boolean isBusy);
    }

    static class AddTagRequest {

        private String tagName;

        AddTagRequest(String tagName) {
            this.tagName = tagName;
        }

        public Observable<Void> request() {
            return ObservableUtils.create(() -> {
                String tagUrl = "http://joyreactor.cc/tag/" + URLEncoder.encode(tagName);
                Document tagPage = HttpClient.getInstance().getDocument(tagUrl);
                String addTagLink = tagPage.select("a.change_favorite_link").first().absUrl("href");
                HttpClient.getInstance().getText(addTagLink);
                return null;
            });
        }
    }
}