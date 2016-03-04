package y2k.joyreactor.common

import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

/**
 * Created by y2k on 3/4/16.
 */
open class BaseActivity : AppCompatActivity() {

    var menuHolder = MenuHolder()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return menuHolder.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuHolder.onOptionsItemSelected(item)
    }
}