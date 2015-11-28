package y2k.joyreactor.platform;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import y2k.joyreactor.*;

/**
 * Created by y2k on 10/19/15.
 */
public class AndroidNavigation extends Navigation {

    static Post sPostArgument; // FIXME:
    static String sPostIdArgument = "2294127"; // FIXME:
    Activity currentActivity;

    public AndroidNavigation(Application app) {
        app.registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
    }

    @Override
    public void switchProfileToLogin() {
        currentActivity.startActivity(new Intent(currentActivity, LoginActivity.class));
        currentActivity.finish();
    }

    @Override
    public void switchLoginToProfile() {
        currentActivity.startActivity(new Intent(currentActivity, ProfileActivity.class));
        currentActivity.finish();
    }

    @Override
    public void closeCreateComment() {
        // TODO:
    }

    @Override
    public void closeAddTag() {
        AddTagDialogFragment.dismiss((AppCompatActivity) currentActivity);
    }

    @Override
    public void openPost(Post post) {
        sPostArgument = post;
        currentActivity.startActivity(new Intent(currentActivity, PostActivity.class));
    }

    @Override
    public Post getArgumentPost() {
        return sPostArgument;
    }

    @Override
    public void openPost(String postId) {
        sPostIdArgument = postId;
        currentActivity.startActivity(new Intent(currentActivity, PostActivity.class));
    }

    @Override
    public String getArgumentPostId() {
        return sPostIdArgument;
    }

    @Override
    public void openBrowser(String url) {
        currentActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void openVideo(String postId) {
        // TODO:
        sPostIdArgument = postId;
        currentActivity.startActivity(new Intent(currentActivity, VideoActivity.class));
    }

    @Override
    public void openImageView(Post post) {
        // TODO:
    }

    private class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityResumed(Activity activity) {
            currentActivity = activity;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            currentActivity = null;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}