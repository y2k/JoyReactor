package y2k.joyreactor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private CounterStore counterStore = new CounterStore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final TextView counterTextView = (TextView) findViewById(R.id.counterTextView);
        final Button counterButton = (Button) findViewById(R.id.counterButton);

        counterButton.setOnClickListener((view) -> {
            counterStore.add(1);
            counterTextView.setText("Click Nr. " + counterStore.get());
        });
    }
}
