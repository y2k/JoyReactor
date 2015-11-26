package y2k.joyreactor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by y2k on 9/26/15.
 */
public class FixedAspectPanel extends ViewGroup {

    private float aspect;

    public FixedAspectPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAspect(float aspect) {
        this.aspect = aspect;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++)
            getChildAt(i).layout(l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w, h;
        if (MeasureSpec.getSize(widthMeasureSpec) != 0) {
            w = MeasureSpec.getSize(widthMeasureSpec);
            h = (int) (w / aspect);
        } else if (MeasureSpec.getSize(widthMeasureSpec) != 0) {
            h = MeasureSpec.getSize(heightMeasureSpec);
            w = (int) (h * aspect);
        } else {
            throw new IllegalStateException();
        }
        setMeasuredDimension(w, h);

        for (int i = 0; i < getChildCount(); i++)
            getChildAt(i).measure(
                    MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
    }
}