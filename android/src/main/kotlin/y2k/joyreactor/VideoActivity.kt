package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.viewmodel.VideoViewModel

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val vm = ServiceLocator.resolve<VideoViewModel>()
        bindingBuilder(this) {
            muteVideoView(R.id.video, vm.videoFile)
            visibility(R.id.progress, vm.isBusy)
        }
    }
}