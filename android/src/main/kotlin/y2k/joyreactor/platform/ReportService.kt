package y2k.joyreactor.platform

import android.content.Intent
import y2k.joyreactor.App
import y2k.joyreactor.R

/**
 * Created by y2k on 2/27/16.
 */
class ReportService {

    private val context = App.instance

    fun createFeedback() {
        val intent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf("joyreactor.feedbacks@gmail.com"))
            .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.write_feedback))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .setType("text/html")

        val prefEmailClients = arrayOf(
            "com.google.android.apps.inbox",
            "com.google.android.gm",
            "com.android.email")

        val packageName = context.packageManager
            .queryIntentActivities(intent, 0)
            .map { it.activityInfo.packageName }
            .firstOrNull { prefEmailClients.contains(it) }
        intent.setPackage(packageName)

        context.startActivity(intent)
    }
}