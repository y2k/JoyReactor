package y2k.joyreactor

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.common.bindingBuilder
import y2k.joyreactor.common.find
import y2k.joyreactor.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(find<Toolbar>(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vm = ServiceLocator.resolve<ProfileViewModel>()
        bindingBuilder(this) {
            visibility(R.id.progress, vm.isBusy)

            webImageView(R.id.avatar, vm.userImage)
            textView(R.id.rating, vm.rating)
            ratingBar(R.id.stars, vm.stars)
            progressBar(R.id.nextStarProgress, vm.nextStarProgress)

            command(R.id.logout, { vm.logout() })
        }
    }
}