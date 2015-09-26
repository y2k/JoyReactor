package y2k.joyreactor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by y2k on 9/26/15.
 */
public class PostLoader {

    public Observable<PostCollection> get() {
        Observable<PostCollection> result = Observable
                .create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
                    try {
                        Document doc = getDocument();

                        PostCollection posts = new PostCollection();
                        for (Element e : doc.select("div.postContainer"))
                            posts.add(new Post(e));

                        subscriber.onNext(posts);
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }));

        return result.observeOn(ForegroundScheduler.getInstance());
    }

    private Document getDocument() throws IOException {
        return Jsoup.connect("http://joyreactor.cc")
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko")
                .timeout(15000).get();
    }

    public static class PostCollection extends ArrayList<Post> {
    }

    public static class Post {

        public final String title;
        public final String image;

        public Post(Element element) {
            title = element.select("div.post_content").text();
            image = element.select("div.post_content img").attr("src");
        }
    }
}
