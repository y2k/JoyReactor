package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.VideoView
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.VideoPresenter

import java.io.File

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val videoView = findViewById(R.id.video) as VideoView
        videoView.setOnPreparedListener { it.isLooping = true }

        ServiceLocator.resolve(
            object : VideoPresenter.View {

                override fun showVideo(videoFile: File) {
                    videoView.setVideoPath(videoFile.absolutePath)
                    videoView.start()
                }

                override fun setBusy(isBusy: Boolean) {
                    findViewById(R.id.progress).visibility = if (isBusy) View.VISIBLE else View.GONE
                }
            })
    }
}