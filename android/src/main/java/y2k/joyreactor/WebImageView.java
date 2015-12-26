package y2k.joyreactor;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import kotlin.Unit;
import y2k.joyreactor.platform.ImageRequest;

/**
 * Created by y2k on 9/26/15.
 */
public class WebImageView extends ImageView {

    private static Image EMPTY = new Image("", 0, 0);

    private Invalidator invalidator = new Invalidator();

    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(Image image) {
        invalidator.invalidateStateChanges(image == null ? EMPTY : image);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        invalidator.invalidateStateChanges(null);
    }

    private class Invalidator {

        private Image image;
        private int lastWidth;
        private int lastHeight;

        public void invalidateStateChanges(Image newImage) {
            if (newImage == EMPTY) {
                invalidateToEmpty();
            } else {
                if (newImage == null) invalidateOnChangeSize();
                else invalidateOnImageChanged(newImage);
            }
        }

        private void invalidateToEmpty() {
            if (this.image == EMPTY) return;
            setImageDrawable(null);
            new ImageRequest()
                    .setUrl(null)
                    .to(this, bitmap -> Unit.INSTANCE);
        }

        private void invalidateOnChangeSize() {
            if (image == null) return;
            if (lastWidth == getWidth() && lastHeight == getHeight()) return;

            lastWidth = getWidth();
            lastHeight = getHeight();

            setImageDrawable(null);
            new ImageRequest()
                    .setUrl(image)
                    .setSize(lastWidth, lastHeight)
                    .to(this, bitmap -> {
                        setImageBitmap(bitmap);
                        return Unit.INSTANCE;
                    });
        }

        private void invalidateOnImageChanged(Image newImage) {
            if (image != null && image.equals(newImage)) return;

            image = newImage;

            if (lastWidth == 0 || lastHeight == 0) return;

            lastWidth = getWidth();
            lastHeight = getHeight();

            setImageDrawable(null);
            new ImageRequest()
                    .setUrl(image)
                    .setSize(lastWidth, lastHeight)
                    .to(this, bitmap -> {
                        setImageBitmap(bitmap);
                        return Unit.INSTANCE;
                    });
        }
    }
}