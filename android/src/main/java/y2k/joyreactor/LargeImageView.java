package y2k.joyreactor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import y2k.joyreactor.common.ObjectUtils;
import y2k.joyreactor.common.ObservableUtils;

import java.io.File;

/**
 * Created by y2k on 12/8/15.
 */
public class LargeImageView extends ImageView {

    private File path;

    public LargeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setImage(File path) {
        if (ObjectUtils.equals(this.path, path)) return;
        this.path = path;

        ObservableUtils.func(() -> {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path.getAbsolutePath(), op);

            DisplayMetrics met = getResources().getDisplayMetrics();
            op.inJustDecodeBounds = false;
            op.inSampleSize = Math.max(
                    op.outWidth / met.widthPixels,
                    op.outHeight / met.heightPixels);

            return BitmapFactory.decodeFile(path.getAbsolutePath(), op);
        }).subscribe(this::setImageBitmap);
    }
}