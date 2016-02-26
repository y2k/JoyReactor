package y2k.joyreactor.widget

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.util.AttributeSet
import android.util.Log
import android.widget.VideoView

/**
 * Created by y2k on 26/02/16.
 */
class MuteVideoView(context: Context?, attrs: AttributeSet?) :
    VideoView(MuteAudioContext(context), attrs) {

    class MuteAudioContext(base: Context?) : ContextWrapper(base) {

        override fun sendBroadcast(intent: Intent?) {
            // Ignore stop music broadcast for Android < 5.0
        }

        override fun getSystemService(name: String?): Any? {
            Log.i("MuteVideoView", "getSystemService | " + name)
            if (name == Context.AUDIO_SERVICE) {
//                val init = AudioManager::class.java.getConstructor(Context::class.java)
//                return init.newInstance(null)
            }
            return super.getSystemService(name)
        }
    }
}