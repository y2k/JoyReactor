package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.ProfilePresenter;

public class ProfileActivity extends AppCompatActivity implements ProfilePresenter.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProfilePresenter presenter = ServiceLocator.getInstance().provideProfilePresenter(this);
        findViewById(R.id.logout).setOnClickListener(v -> presenter.logout());
    }

    @Override
    public void setProfile(Profile profile) {
        ((WebImageView) findViewById(R.id.avatar)).setImage(profile.getUserImage());
        ((TextView) findViewById(R.id.rating)).setText("" + profile.getRating());
        ((RatingBar) findViewById(R.id.stars)).setRating(profile.getStars());
        ((ProgressBar) findViewById(R.id.nextStarProgress)).setProgress((int) profile.getProgressToNewStar());
    }

    @Override
    public void setBusy(boolean isBusy) {
        // TODO:
        findViewById(R.id.progress).setVisibility(isBusy ? View.VISIBLE : View.GONE);
    }
}