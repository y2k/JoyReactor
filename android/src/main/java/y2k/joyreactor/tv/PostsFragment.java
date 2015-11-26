package y2k.joyreactor.tv;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v17.leanback.app.VerticalGridFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.ViewGroup;
import y2k.joyreactor.Post;
import y2k.joyreactor.presenters.PostListPresenter;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class PostsFragment extends VerticalGridFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(4);
        setGridPresenter(gridPresenter);

        ArrayObjectAdapter adapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(adapter);

        new PostListPresenter(new PostListPresenter.View() {

            @Override
            public void setBusy(boolean isBusy) {
                // TODO:
            }

            @Override
            public void reloadPosts(List<Post> posts, Integer divider) {
                // TODO:
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
            cardView.setBackgroundColor(Color.GREEN);
            cardView.setFocusable(true);
            cardView.setFocusableInTouchMode(true);
            return new ViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ImageCardView v = (ImageCardView) viewHolder.view;
            Post p = (Post) item;
            v.setTitleText(p.title);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }
}