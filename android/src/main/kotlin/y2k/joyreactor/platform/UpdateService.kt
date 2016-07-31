package y2k.joyreactor.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.json.JSONObject
import y2k.joyreactor.BuildConfig
import y2k.joyreactor.common.BackgroundWorks
import y2k.joyreactor.common.async.CompletableFuture
import y2k.joyreactor.common.async.async_
import y2k.joyreactor.common.async.runAsync
import y2k.joyreactor.common.downloadToFileAsync
import y2k.joyreactor.services.MemoryBuffer
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Created by y2k on 05/02/16.
 */
class UpdateService(
    private val backgroundWorks: BackgroundWorks,
    private val context: Context,
    private val stateStorage: MemoryBuffer) {

    private val url = "https://api.github.com/repos/y2k/JoyReactor/releases/latest"
    private val key = "update-app"

    fun isCheckInProgress(): Boolean = backgroundWorks.getStatus(key).isInProgress

    fun requestDownloadUpdate(): String {
        async_ {
            if (backgroundWorks.getStatus(key).isInProgress) return@async_
            backgroundWorks.markWorkStarted(key, key)
            try {
                if (BuildConfig.VERSION_CODE >= updateVersion) {
                    if (System.currentTimeMillis() - lastCheck > TimeUnit.MINUTES.toMillis(15)) {
                        val info = await(getGitHubInformationAboutLastRelease())
                        if (BuildConfig.VERSION_CODE < info.version) {
                            if (info.version > updateVersion) {
                                await(URL(info.downloadUrl)
                                    .openConnection()
                                    .apply { addRequestProperty("Accept", "application/octet-stream") }
                                    .downloadToFileAsync(File(context.externalCacheDir, "update.apk")))
                                updateVersion = info.version
                            }
                        }
                    }
                }
                backgroundWorks.markWorkFinished(key, key)
            } catch (e: Exception) {
                backgroundWorks.markWorkFinished(key, key, e)
            }
        }
        return key
    }

    private var updateVersion: Int
        get() = stateStorage.getInt("update-version") ?: 0
        set(value) {
            stateStorage["update-version"] = value
        }

    private var lastCheck: Long
        get() = stateStorage.getLong("last-check-for-update") ?: 0L
        set(value) {
            stateStorage["last-check-for-update"] = value
        }

    private fun getGitHubInformationAboutLastRelease(): CompletableFuture<ReleaseInfo> {
        return runAsync {
            val info = URL(url).readText().let { JSONObject(it) }
            ReleaseInfo(
                version = info.getString("tag_name").buildNumber,
                downloadUrl = info.getJSONArray("assets").getJSONObject(0).getString("url"))
        }
    }

    fun hasFileToInstall() = BuildConfig.VERSION_CODE < updateVersion

    private val String.buildNumber: Int
        get() = split('.').last().toInt()

    fun installUpdate() {
        val file = File(context.externalCacheDir, "update.apk")
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }

    data class ReleaseInfo(val version: Int, val downloadUrl: String)
}