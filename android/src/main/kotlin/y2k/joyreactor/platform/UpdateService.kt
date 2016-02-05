package y2k.joyreactor.platform

import org.json.JSONArray
import rx.Observable
import y2k.joyreactor.BuildConfig
import y2k.joyreactor.common.ioObservable
import java.net.URL

/**
 * Created by y2k on 05/02/16.
 */
class UpdateService() {

    fun checkHasUpdates(): Observable<Boolean> {
        return ioObservable {
            val json = URL("https://api.github.com/repos/y2k/JoyReactor/releases").readText()
            val newVersion = JSONArray(json).getJSONObject(0).getString("tag_name")
            BuildConfig.VERSION_NAME != newVersion
        }
    }

    fun update(): Observable<Unit> {
        return ioObservable {
            Thread.sleep(3000)
        }
    }
}