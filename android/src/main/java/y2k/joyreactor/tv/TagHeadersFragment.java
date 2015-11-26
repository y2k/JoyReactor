package y2k.joyreactor.tv;

import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.app.OnHeaderViewSelectedListenerImpl;
import android.support.v17.leanback.widget.*;
import y2k.joyreactor.R;
import y2k.joyreactor.Tag;
import y2k.joyreactor.presenters.TvPresenter;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class TagHeadersFragment extends HeadersFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setOnHeaderViewSelectedListener(new OnHeaderViewSelectedListenerImpl() {

            @Override
            public void onHeaderSelected(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                // TODO:
            }
        });

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

            private void addRow(ArrayObjectAdapter adapter, String title) {
                ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter();
                adapter.add(new ListRow(new HeaderItem(title), listRowAdapter));
            }
        });
    }
}