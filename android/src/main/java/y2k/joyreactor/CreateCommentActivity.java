package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.CreateCommentPresenter;

public class CreateCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);

        TextView nameView = (TextView) findViewById(R.id.userName);
        TextView textView = (TextView) findViewById(R.id.text);
        View sendButton = findViewById(R.id.send);
        View progress = findViewById(R.id.progress);

        CreateCommentPresenter presenter = ServiceLocator.INSTANCE.provideCreateCommentPresenter(
                new CreateCommentPresenter.View() {

                    @Override
                    public void setIsBusy(boolean isBusy) {
                        // TODO
                        if (isBusy) {
                            progress.setVisibility(View.VISIBLE);
                            progress.setAlpha(0);
                            progress.animate().alpha(1);

                            sendButton.animate().alpha(0).withEndAction(() -> sendButton.setVisibility(View.INVISIBLE));
                        } else {
                            sendButton.setVisibility(View.VISIBLE);
                            sendButton.setAlpha(0);
                            sendButton.animate().alpha(1);

                            progress.animate().alpha(0).withEndAction(() -> progress.setVisibility(View.GONE));
                        }
                    }

                    @Override
                    public void setUser(Profile profile) {
                        ((WebImageView) findViewById(R.id.userImage)).setImage(profile.getUserImage());

                        nameView.setText(profile.getUserName());
                        nameView.setAlpha(0);
                        nameView.animate().alpha(1);
                    }
                });

        sendButton.setOnClickListener(v -> presenter.create("" + textView.getText()));
    }
}