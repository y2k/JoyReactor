package y2k.joyreactor

import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        ActionBarDrawerToggle(this, findViewById(R.id.drawer_layout) as DrawerLayout,
            toolbar, R.string.app_name, R.string.app_name).syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile -> startActivity(Intent(this, ProfileActivity::class.java))
            R.id.messages -> startActivity(Intent(this, MessagesActivity::class.java))
            R.id.addTag -> AddTagDialogFragment.show(supportFragmentManager)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}