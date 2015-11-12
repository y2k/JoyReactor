package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import y2k.joyreactor.presenters.ProfilePresenter;

public class ProfileActivity extends AppCompatActivity implements ProfilePresenter.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new ProfilePresenter(this);
    }

    @Override
    public void setProfile(Profile profile) {
        // TODO:
    }

    @Override
    public void setProgress(boolean isProgress) {
        // TODO:
    }
}