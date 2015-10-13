package y2k.joyreactor.platform;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIGraphics;
import org.robovm.apple.uikit.UIImage;
import org.robovm.apple.uikit.UIScreen;
import y2k.joyreactor.images.BaseImageRequest;

import java.io.File;

/**
 * Created by y2k on 12/10/15.
 */
public class ImageRequest extends BaseImageRequest<UIImage> {

    @Override
    protected UIImage decode(File path) {
        UIImage image = new UIImage(path);
        forceLoadInMemory(image);
        return image;
    }

    private void forceLoadInMemory(UIImage image) {
        UIGraphics.beginImageContext(new CGSize(100, 100));
        image.draw(CGPoint.Zero());
        UIGraphics.endImageContext();
    }

    @Override
    public BaseImageRequest<UIImage> setSize(int width, int height) {
        double scale = Math.min(2, UIScreen.getMainScreen().getScale());
        return super.setSize((int) (scale * width), (int) (scale * height));
    }
}