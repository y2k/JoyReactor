package y2k.joyreactor.image;

import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.uikit.*;
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

    private CGSize _imageSize;

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
        setMaxMinZoomScalesForCurrentBounds();

        // Step 1: restore zoom scale, first making sure it is within the allowable range.
        double maxZoomScale = Math.max(getMinimumZoomScale(), _scaleToRestoreAfterResize);
        setZoomScale(Math.min(getMaximumZoomScale(), maxZoomScale));

        // Step 2: restore center point, first making sure it is within the allowable range.

        // 2a: convert our desired center point back to our own coordinate space
        CGPoint boundsCenter = convertPointFromView(_pointToCenterAfterResize, _zoomView);

        // 2b: calculate the content offset that would yield that center point
        CGPoint offset = new CGPoint(
                boundsCenter.getX() - getBounds().getSize().getWidth() / 2.0,
                boundsCenter.getY() - getBounds().getSize().getHeight() / 2.0);

        // 2c: restore offset, adjusted to be within the allowable range
        CGPoint maxOffset = maximumContentOffset();
        CGPoint minOffset = minimumContentOffset();

        double realMaxOffset = Math.min(maxOffset.getX(), offset.getX());
        offset.setX(Math.max(minOffset.getX(), realMaxOffset));

        realMaxOffset = Math.min(maxOffset.getY(), offset.getY());
        offset.setY(Math.max(minOffset.getY(), realMaxOffset));

        setContentOffset(offset);
    }

    private void setMaxMinZoomScalesForCurrentBounds() {
        // TODO:

        CGSize boundsSize = getBounds().getSize();

        // calculate min/max zoomscale
        double xScale = boundsSize.getWidth() / _imageSize.getWidth(); // the scale needed to perfectly fit the image width-wise
        double yScale = boundsSize.getHeight() / _imageSize.getHeight(); // the scale needed to perfectly fit the image height-wise

        // fill width if the image and phone are both portrait or both landscape; otherwise take smaller scale
        boolean imagePortrait = _imageSize.getHeight() > _imageSize.getWidth();
        boolean phonePortrait = boundsSize.getHeight() > boundsSize.getWidth();
        double minScale = imagePortrait == phonePortrait ? xScale : Math.min(xScale, yScale);

        // on high resolution screens we have double the pixel density, so we will be seeing every pixel if we limit the
        // maximum zoom scale to 0.5.
        double maxScale = 1.0 / UIScreen.getMainScreen().getScale();

        // don't let minScale exceed maxScale. (If the image is smaller than the screen, we don't want to force it to be zoomed.)
        if (minScale > maxScale) minScale = maxScale;

        setMaximumZoomScale(maxScale);
        setMinimumZoomScale(minScale);
    }

    private CGPoint maximumContentOffset() {
        CGSize contentSize = getContentSize();
        CGSize boundsSize = getBounds().getSize();
        return new CGPoint(
                contentSize.getWidth() - boundsSize.getWidth(),
                contentSize.getHeight() - boundsSize.getHeight());
    }

    private CGPoint minimumContentOffset() {
        return CGPoint.Zero();
    }
}