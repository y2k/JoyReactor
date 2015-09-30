package y2k.joyreactor;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by y2k on 9/30/15.
 */
public class Profile {

    public String userName;
    public String userImage;
    public float rating;
    public int stars;
    public float progressToNewStar;

    public static Observable<Profile> request() {
        Observable<Profile> result = Observable.create(subscriber -> Schedulers.io().createWorker().schedule(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Profile profile = new Profile();
            profile.userName = "y2k";
            profile.userImage = "http://img0.joyreactor.cc/pics/avatar/user/6396";
            profile.progressToNewStar = 0.3f;
            profile.rating = 99.9f;
            profile.stars = 5;

            subscriber.onNext(profile);
        }));

        return result.observeOn(ForegroundScheduler.getInstance());
    }
}