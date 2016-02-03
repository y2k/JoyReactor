package y2k.joyreactor

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import android.widget.Toast
import y2k.joyreactor.common.ServiceLocator
import y2k.joyreactor.presenters.LoginPresenter

class LoginActivity : AppCompatActivity(), LoginPresenter.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val presenter = ServiceLocator.resolve(this)

        findViewById(R.id.login).setOnClickListener {
            presenter.login(
                "" + (findViewById(R.id.username) as TextView).text,
                "" + (findViewById(R.id.password) as TextView).text)
        }
    }

    override fun setBusy(isBusy: Boolean) {
        findViewById(R.id.progress).visibility = if (isBusy) View.VISIBLE else View.GONE
    }

    override fun showError() {
        Toast.makeText(this, R.string.unknown_error_occurred, Toast.LENGTH_LONG).show()
    }

    override fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}