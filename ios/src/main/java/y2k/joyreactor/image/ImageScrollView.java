package y2k.joyreactor.image;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.UIImageView;
import org.robovm.apple.uikit.UIScrollView;
import org.robovm.apple.uikit.UIScrollViewDelegateAdapter;
import org.robovm.apple.uikit.UIView;
import org.robovm.objc.annotation.CustomClass;
import org.robovm.rt.bro.annotation.ByVal;

/**
 * Created by y2k on 10/25/15.
 */
@CustomClass("ImageScrollView")
public class ImageScrollView extends UIScrollView {

    private static final double FLT_EPSILON = 0.0000001;

    private int index;

    private UIImageView _zoomView;
    private CGPoint _pointToCenterAfterResize;
    private double _scaleToRestoreAfterResize;

    public ImageScrollView(CGRect frame) {
        super(frame);
        setShowsVerticalScrollIndicator(false);
        setShowsHorizontalScrollIndicator(false);
        setBouncesZoom(false);
        setDecelerationRate(UIScrollView.getFastDecelerationRate());
        setDelegate(new UIScrollViewDelegateAdapter() {

            @Override
            public UIView getViewForZooming(UIScrollView scrollView) {
                return _zoomView;
            }
        });
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();

        // center the zoom view as it becomes smaller than the size of the screen
        CGSize boundsSize = getBounds().getSize();
        CGRect frameToCenter = _zoomView.getFrame();

        // center horizontally
        if (frameToCenter.getSize().getWidth() < boundsSize.getWidth()) {
            frameToCenter.getOrigin().setX((boundsSize.getWidth() - frameToCenter.getSize().getWidth()) / 2);
        } else {
            frameToCenter.getOrigin().setX(0);
        }

        // center vertically
        if (frameToCenter.getSize().getHeight() < boundsSize.getHeight()) {
            frameToCenter.getOrigin().setY((boundsSize.getHeight() - frameToCenter.getSize().getHeight()) / 2);
        } else {
            frameToCenter.getOrigin().setY(0);
        }

        _zoomView.setFrame(frameToCenter);
    }

    @Override
    public void setFrame(@ByVal CGRect frame) {
        boolean sizeChanging = !frame.getSize().equals(getFrame().getSize());
        if (sizeChanging) prepareToResize();
        super.setFrame(frame);
        if (sizeChanging) recoverFromResizing();
    }

    private void prepareToResize() {
        CGPoint boundsCenter = new CGPoint(getBounds().getMidX(), getBounds().getMidY());
        _pointToCenterAfterResize = convertPointToView(boundsCenter, _zoomView);

        _scaleToRestoreAfterResize = getZoomScale();

        // If we're at the minimum zoom scale, preserve that by returning 0, which will be converted to the minimum
        // allowable scale when the scale is restored.
        if (_scaleToRestoreAfterResize <= getMinimumZoomScale() + FLT_EPSILON)
            _scaleToRestoreAfterResize = 0;
    }

    private void recoverFromResizing() {
        // TODO
    }
}