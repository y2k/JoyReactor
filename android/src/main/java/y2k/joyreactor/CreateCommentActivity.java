package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import y2k.joyreactor.presenters.CreateCommentPresenter;

public class CreateCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        TextView nameView = (TextView) findViewById(R.id.userName);
        TextView textView = (TextView) findViewById(R.id.text);

        CreateCommentPresenter presenter = new CreateCommentPresenter(new CreateCommentPresenter.View() {

            @Override
            public void setIsBusy(boolean isBusy) {
                // TODO
            }

            @Override
            public void setUser(Profile profile) {
                ((WebImageView) findViewById(R.id.userImage)).setImage(profile.userImage);

                nameView.setText(profile.userName);
                nameView.setAlpha(0);
                nameView.animate().alpha(1);
            }
        });

        findViewById(R.id.send).setOnClickListener(v -> presenter.create("" + textView.getText()));
    }
}