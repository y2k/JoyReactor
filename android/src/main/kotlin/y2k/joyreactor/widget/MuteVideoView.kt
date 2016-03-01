package y2k.joyreactor.widget

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import java.io.File
import java.io.FileInputStream

/**
 * Created by y2k on 26/02/16.
 */
class MuteVideoView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs) {

    private val player = MediaPlayer()

    init {
        holder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                player.release()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
            }
        })
        player.setOnVideoSizeChangedListener { player, w, h ->
            val screenWidth = (parent as View).width
            val screenHeight = (parent as View).height
            val videoParams = layoutParams
            if (w > h) {
                videoParams.width = screenWidth
                videoParams.height = screenWidth * h / w
            } else {
                videoParams.width = screenHeight * w / h
                videoParams.height = screenHeight
            }
            layoutParams = videoParams
        }
    }

    fun play(file: File) {
        file.inputStream().use { player.setDataSource((it as FileInputStream).fd) }
        player.setDisplay(holder)
        player.isLooping = true
        player.prepare()
        player.start()
    }
}