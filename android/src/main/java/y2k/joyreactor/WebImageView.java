package y2k.joyreactor;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import y2k.joyreactor.platform.ImageRequest;

/**
 * Created by y2k on 9/26/15.
 */
public class WebImageView extends ImageView {

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(Image image) {
        new ImageRequest()
                .setSize(getSize(), getSize())
                .setUrl(image)
                .to(this, this::setImageBitmap);
    }

    private int getSize() {
        return (int) (getLayoutParams().width * getResources().getDisplayMetrics().density);
    }
}