package y2k.joyreactor;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by y2k on 9/26/15.
 */
public class RoundBorderLayout extends FrameLayout {

    private int[] lastLayout = new int[2];

    private Paint clipPaint;
    private Paint borderPaint;

    private Canvas clipCanvas;

    private RectF rect = new RectF();

    public RoundBorderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        clipPaint = new Paint();
        clipPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(0x40808080);
        borderPaint.setStrokeWidth(1);

        borderPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int w = right - left;
        int h = bottom - top;
        if (lastLayout[0] != w && lastLayout[1] != h)
            updateLayoutCanvas(w, h);
    }

    private void updateLayoutCanvas(int w, int h) {
        Bitmap canvasBitmap;
        if (w > 0 && h > 0) {
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            clipCanvas = new Canvas(canvasBitmap);
            rect.set(0, 0, w, h);
            clipPaint.setShader(new BitmapShader(canvasBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        } else {
            clipCanvas = null;
            clipPaint = null;
            rect = null;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (clipCanvas != null) {
            super.dispatchDraw(clipCanvas);
            canvas.drawOval(rect, clipPaint);
            canvas.drawOval(rect, borderPaint);
        }
    }
}