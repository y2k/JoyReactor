package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.isVisible
import y2k.joyreactor.model.Profile
import y2k.joyreactor.presenters.ProfilePresenter

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar.setDisplayHomeAsUpEnabled(true)

        val presenter = ServiceLocator.resolve(object : ProfilePresenter.View {

            override fun setProfile(profile: Profile) {
                (findViewById(R.id.avatar) as WebImageView).setImage(profile.userImage)
                (findViewById(R.id.rating) as TextView).text = "" + profile.rating
                (findViewById(R.id.stars) as RatingBar).rating = profile.stars.toFloat()
                (findViewById(R.id.nextStarProgress) as ProgressBar).progress = profile.progressToNewStar.toInt()
            }

            override fun setBusy(isBusy: Boolean) {
                // TODO:
                findViewById(R.id.progress).isVisible = isBusy
            }
        })

        findViewById(R.id.logout).setOnClickListener { presenter.logout() }
    }
}