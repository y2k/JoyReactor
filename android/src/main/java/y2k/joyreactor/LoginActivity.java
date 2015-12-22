package y2k.joyreactor;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.LoginPresenter;


public class LoginActivity extends AppCompatActivity implements LoginPresenter.View {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LoginPresenter presenter = ServiceLocator.getInstance().provideLoginPresenter(this);

        findViewById(R.id.login).setOnClickListener(v -> presenter.login(
                "" + ((TextView) findViewById(R.id.username)).getText(),
                "" + ((TextView) findViewById(R.id.password)).getText()));
    }

    @Override
    public void setBusy(boolean isBusy) {
        findViewById(R.id.progress).setVisibility(isBusy ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError() {
        Toast.makeText(this, R.string.unknown_error_occurred, Toast.LENGTH_LONG).show();
    }

    @Override
    public void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}