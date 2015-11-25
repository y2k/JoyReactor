package y2k.joyreactor.tv;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.app.RowsFragment;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by y2k on 11/25/15.
 */
public class PostsFragment extends VerticalGridFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        setAdapter(new ArrayObjectAdapter(new CardPresenter()));
    }

    public static class CardPresenter extends Presenter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            ImageCardView cardView = new ImageCardView(parent.getContext()) {
                @Override
                public void setSelected(boolean selected) {
//                    updateCardBackgroundColor(this, selected);
                    super.setSelected(selected);
                }
            };

            cardView.setFocusable(true);
            cardView.setFocusableInTouchMode(true);
//            updateCardBackgroundColor(cardView, false);
            return new ViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }
    }
}