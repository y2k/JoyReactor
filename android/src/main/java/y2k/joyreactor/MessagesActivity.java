package y2k.joyreactor;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by y2k on 11/13/15.
 */
public class MessagesActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new ActionBarDrawerToggle(this, (DrawerLayout) findViewById(R.id.drawer_layout),
                toolbar, R.string.app_name, R.string.app_name).syncState();

    }
}