package y2k.joyreactor.tv;

import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.*;
import android.view.View;
import android.view.ViewGroup;
import y2k.joyreactor.R;
import y2k.joyreactor.Tag;
import y2k.joyreactor.presenters.TvPresenter;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class MainFragment extends BrowseFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUI();

        new TvPresenter(new TvPresenter.View() {

            @Override
            public void updateTags(List<Tag> tags) {
                ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());

                addRow(adapter, getString(R.string.feed));
                addRow(adapter, getString(R.string.favorite));
                for (Tag tag : tags)
                    addRow(adapter, tag.title);

                setAdapter(adapter);
            }
        });
    }

    private void addRow(ArrayObjectAdapter adapter, String title) {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        adapter.add(new ListRow(new HeaderItem(title), listRowAdapter));
    }

    private void setupUI() {
        setTitle(getString(R.string.app_name));

        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(android.R.color.holo_orange_light));
    }

    static class CardPresenter extends Presenter {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            return new ViewHolder(new View(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }
}