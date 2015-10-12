package y2k.joyreactor.platform;

import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIImageView;
import y2k.joyreactor.images.BaseImageRequest;

import java.io.File;

/**
 * Created by y2k on 12/10/15.
 */
public class ImageRequest extends BaseImageRequest<UIImage> {

    @Override
    protected UIImage decode(File path) {
        return new UIImage(path);
    }

//    @Override
//    protected void setImage(Object target, UIImage image) {
//        ((UIImageView) target).setImage(image);
//    }
}