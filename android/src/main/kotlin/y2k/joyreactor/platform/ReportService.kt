package y2k.joyreactor.platform

import android.content.Intent
import android.net.Uri
import y2k.joyreactor.App
import y2k.joyreactor.BuildConfig
import y2k.joyreactor.R

/**
 * Created by y2k on 2/27/16.
 */
class ReportService {

    private val context = App.instance

    fun createFeedback() {
        val emailIntent = createEmailIntent() ?: return
        val telegramIntent = createTelegramIntent()

        val chooser = Intent
            .createChooser(emailIntent, context.getString(R.string.send_feedback_by))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(telegramIntent))
        context.startActivity(chooser)
    }

    private fun createEmailIntent(): Intent? {
        val emailIntent = Intent(Intent.ACTION_SEND)
            .putExtra(Intent.EXTRA_EMAIL, arrayOf("joyreactor.feedbacks@gmail.com"))
            .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.write_feedback))
            .setType("text/html")

        val prefEmailClients = arrayOf(
            "com.google.android.apps.inbox",
            "com.google.android.gm",
            "com.android.email")

        val packageName = context.packageManager
            .queryIntentActivities(emailIntent, 0)
            .map { it.activityInfo.packageName }
            .firstOrNull { prefEmailClients.contains(it) }
        emailIntent.setPackage(packageName)
        return emailIntent
    }

    private fun createTelegramIntent(): Intent {
        return Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://telegram.me/${BuildConfig.FEEDBACK_TELEGRAM}"))
            .setPackage("org.telegram.messenger")
    }
}