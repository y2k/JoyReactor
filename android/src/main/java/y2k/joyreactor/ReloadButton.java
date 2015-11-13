package y2k.joyreactor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by y2k on 9/26/15.
 */
public class ReloadButton extends FrameLayout {

    public ReloadButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_reload_button, this);
    }

    public void setVisibility(boolean visibility) {
        setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        findViewById(R.id.innerReloadButton).setOnClickListener(l);
    }
}