package y2k.joyreactor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new ActionBarDrawerToggle(this, (DrawerLayout) findViewById(R.id.drawer_layout),
                toolbar, R.string.app_name, R.string.app_name).syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile)
            startActivity(new Intent(this, ProfileActivity.class));
        else if (item.getItemId() == R.id.messages)
            startActivity(new Intent(this, MessagesActivity.class));
        else if (item.getItemId() == R.id.addTag)
            AddTagDialogFragment.show(getSupportFragmentManager());
        else return super.onOptionsItemSelected(item);
        return true;
    }
}