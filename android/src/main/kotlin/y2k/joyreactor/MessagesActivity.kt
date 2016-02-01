package y2k.joyreactor

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

/**
 * Created by y2k on 11/13/15.
 */
class MessagesActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_messages)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        ActionBarDrawerToggle(this, findViewById(R.id.drawer_layout) as DrawerLayout,
            toolbar, R.string.app_name, R.string.app_name).syncState()
    }
}