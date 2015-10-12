package y2k.joyreactor.platform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import y2k.joyreactor.images.BaseImageRequest;

import java.io.File;

/**
 * Created by y2k on 10/12/15.
 */
public class ImageRequest extends BaseImageRequest<Bitmap> {

    @Override
    protected Bitmap decode(File path) {
        return BitmapFactory.decodeFile(path.getAbsolutePath());
    }
}