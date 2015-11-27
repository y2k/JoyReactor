package y2k.joyreactor.tv;

import android.os.Bundle;
import android.support.v17.leanback.app.HeadersFragment;
import android.support.v17.leanback.app.OnHeaderViewSelectedListenerImpl;
import android.support.v17.leanback.widget.*;
import y2k.joyreactor.R;
import y2k.joyreactor.Tag;
import y2k.joyreactor.presenters.TagListPresenter;

import java.util.List;

/**
 * Created by y2k on 11/25/15.
 */
public class TagHeadersFragment extends HeadersFragment {

    TagListPresenter presenter;
    List<Tag> tags;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setOnHeaderViewSelectedListener(new OnHeaderViewSelectedListenerImpl() {

            @Override
            public void onHeaderSelected(RowHeaderPresenter.ViewHolder viewHolder, Row row) {
                if (row.getId() == 0) presenter.selectedFeatured();
                else if (row.getId() == 1) presenter.selectedFavorite();
                else presenter.selectTag(tags.get((int) (row.getId() - 2)));
            }
        });

        presenter = new TagListPresenter(new TagListPresenter.View() {

            @Override
            public void reloadData(List<Tag> tags) {
                TagHeadersFragment.this.tags = tags;
                ArrayObjectAdapter adapter = new ArrayObjectAdapter(new ListRowPresenter());

                addRow(adapter, getString(R.string.feed));
                addRow(adapter, getString(R.string.favorite));
                for (Tag tag : tags)
                    addRow(adapter, tag.title);

                setAdapter(adapter);
            }

            private void addRow(ArrayObjectAdapter adapter, String title) {
                adapter.add(new Row(adapter.size(), new HeaderItem(title)));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.activate();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.deactivate();
    }
}