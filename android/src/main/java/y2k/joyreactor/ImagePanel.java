package y2k.joyreactor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by y2k on 30/11/15.
 */
public class ImagePanel extends FrameLayout {

    private List<WebImageView> imageViews = new ArrayList<>();

    public ImagePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        View.inflate(context, R.layout.layout_post_images, this);
        loadImageViews();
    }

    private void loadImageViews() {
        ViewGroup root = (ViewGroup) getChildAt(0);
        for (int i = 0; i < root.getChildCount(); i++)
            imageViews.add((WebImageView) ((ViewGroup) root.getChildAt(i)).getChildAt(0));
    }

    public void setImages(List<Image> images) {
        for (int i = 0; i < Math.min(images.size(), imageViews.size()); i++)
            imageViews.get(i).setImage(images.get(i));

        for (int i = 0; i < imageViews.size(); i++) {
            ((ViewGroup) imageViews.get(i).getParent())
                    .setVisibility(i < images.size() ? View.VISIBLE : View.GONE);
        }
    }
}