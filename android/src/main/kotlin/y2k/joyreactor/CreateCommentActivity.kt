package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.compatAnimate
import y2k.joyreactor.presenters.CreateCommentPresenter

class CreateCommentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_comment)

        val nameView = findViewById(R.id.userName) as TextView
        val textView = findViewById(R.id.text) as TextView
        val sendButton = findViewById(R.id.send)
        val progress = findViewById(R.id.progress)

        val presenter = ServiceLocator.resolve(
            object : CreateCommentPresenter.View {

                override fun setIsBusy(isBusy: Boolean) {
                    // TODO
                    if (isBusy) {
                        progress.visibility = View.VISIBLE
                        progress.alpha = 0f
                        progress.compatAnimate().alpha(1f)

                        sendButton.compatAnimate().alpha(0f).withEndAction { sendButton.visibility = View.INVISIBLE }
                    } else {
                        sendButton.visibility = View.VISIBLE
                        sendButton.alpha = 0f
                        sendButton.compatAnimate().alpha(1f)

                        progress.compatAnimate().alpha(0f).withEndAction { progress.visibility = View.GONE }
                    }
                }

                override fun setUser(profile: Profile) {
                    (findViewById(R.id.userImage) as WebImageView).setImage(profile.userImage)

                    nameView.text = profile.userName
                    nameView.alpha = 0f
                    nameView.compatAnimate().alpha(1f)
                }
            })

        sendButton.setOnClickListener { v -> presenter.create("" + textView.text) }
    }
}