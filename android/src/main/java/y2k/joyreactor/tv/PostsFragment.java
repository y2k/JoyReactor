package y2k.joyreactor.tv;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.ViewGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import y2k.joyreactor.App;
import y2k.joyreactor.Post;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.platform.ImageRequest;
import y2k.joyreactor.presenters.PostListPresenter;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class PostsFragment extends VerticalGridFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(adapter);

        ServiceLocator.getInstance().providePostListPresenter(
                new PostListPresenter.View() {

                    @Override
                    public void setBusy(boolean isBusy) {
                        // TODO:
                    }

                    @Override
                    public void reloadPosts(@NotNull List<? extends Post> posts, @Nullable Integer divider) {
                        adapter.clear();
                        adapter.addAll(0, posts);
                    }

                    @Override
                    public void setHasNewPosts(boolean hasNewPosts) {
                        // TODO:
                    }
                });
    }

    public static class CardPresenter extends Presenter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            ImageCardView cardView = new ImageCardView(parent.getContext());
            cardView.setFocusable(true);
            cardView.setFocusableInTouchMode(true);
            return new ViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ImageCardView v = (ImageCardView) viewHolder.view;
            Post p = (Post) item;
            v.setTitleText(p.getTitle());
            new ImageRequest()
                    .setSize(200, 200)
                    .setUrl(p.getImage())
                    .to(v, bitmap -> v.setMainImage(new RenderDrawable(bitmap, 200, 200)));
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }

        static class RenderDrawable extends Drawable {

            private final Bitmap bitmap;
            private final float width;
            private final float height;

            public RenderDrawable(Bitmap bitmap, int width, int height) {
                this.bitmap = bitmap;
                this.width = width * App.getInstance().getResources().getDisplayMetrics().density;
                this.height = height * App.getInstance().getResources().getDisplayMetrics().density;
            }

            @Override
            public int getIntrinsicWidth() {
                return (int) width;
            }

            @Override
            public int getIntrinsicHeight() {
                return (int) height;
            }

            @Override
            public void draw(Canvas canvas) {
                if (bitmap != null) {
                    canvas.save();
                    canvas.scale(width / bitmap.getWidth(), height / bitmap.getHeight());
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    canvas.restore();
                }
            }

            @Override
            public void setAlpha(int alpha) {
                // ignore
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
                // ignore
            }

            @Override
            public int getOpacity() {
                return 0;
            }
        }
    }
}