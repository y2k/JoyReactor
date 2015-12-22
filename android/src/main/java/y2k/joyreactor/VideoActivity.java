package y2k.joyreactor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.VideoView;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.VideoPresenter;

import java.io.File;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = (VideoView) findViewById(R.id.video);
        videoView.setOnPreparedListener(mp -> mp.setLooping(true));

        ServiceLocator.getInstance().provideVideoPresenter(
                new VideoPresenter.View() {

                    @Override
                    public void showVideo(File videoFile) {
                        videoView.setVideoPath(videoFile.getAbsolutePath());
                        videoView.start();
                    }

                    @Override
                    public void setBusy(boolean isBusy) {
                        findViewById(R.id.progress).setVisibility(isBusy ? View.VISIBLE : View.GONE);
                    }
                });
    }
}