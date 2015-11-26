package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import y2k.joyreactor.presenters.VideoPresenter;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        new VideoPresenter(new VideoPresenter.View() {

            @Override
            public void showVideo(File videoFile) {
                // TODO:
            }

            @Override
            public void setBusy(boolean isBusy) {
                // TODO:
            }
        });
    }
}
