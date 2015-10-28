package y2k.joyreactor;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.Method;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.rt.bro.annotation.ByVal;
import org.robovm.rt.bro.annotation.Library;

/**
 * Created by y2k on 28/10/15.
 */
@NativeClass("ImageScrollView")
@Library(Library.INTERNAL)
public class ImageScrollView extends UIView {

    public ImageScrollView(@ByVal CGRect frame) {
        super((SkipInit) null);
        this.initObject(this.init(frame));
    }

    @Method(selector="displayTiledImageNamed:size:")
    public native int displayTiledImageNamed(String name, @ByVal CGSize frame);
}