package y2k.joyreactor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import y2k.joyreactor.presenters.TagsPresenter;

import java.util.List;

/**
 * Created by y2k on 11/12/15.
 */
public class MenuFragment extends Fragment implements TagsPresenter.View {

    TagsAdapter adapter;
    TagsPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setAdapter(adapter = new TagsAdapter());
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter = new TagsPresenter(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.activate();
    }

    @Override
    public void reloadData(List<Tag> tags) {
        adapter.updateData(tags);
    }

    private static class TagsAdapter extends RecyclerView.Adapter {

        private List<Tag> tags;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ViewHolder vh = (ViewHolder) holder;
            vh.title.setText(tags.get(position).title);
            vh.icon.setImage(tags.get(position).image);
        }

        @Override
        public int getItemCount() {
            return tags == null ? 0 : tags.size();
        }

        public void updateData(List<Tag> tags) {
            this.tags = tags;
            notifyDataSetChanged();
        }

        private static class ViewHolder extends RecyclerView.ViewHolder {

            TextView title;
            WebImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                title = (TextView) itemView.findViewById(R.id.title);
                icon = (WebImageView) itemView.findViewById(R.id.icon);
            }
        }
    }
}
