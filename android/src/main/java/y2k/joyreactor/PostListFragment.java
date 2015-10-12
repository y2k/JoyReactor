package y2k.joyreactor;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import y2k.joyreactor.platform.ImageRequest;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListFragment extends Fragment implements PostListPresenter.View {

    private PostAdapter adapter;

    @Override
    public void setBusy(boolean isBusy) {
        // TODO:
    }

    @Override
    public void reloadPosts(List<Post> posts) {
        adapter.reloadData(posts);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        view.findViewById(R.id.error).setVisibility(View.GONE);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        list.setAdapter(adapter = new PostAdapter());

        new PostListPresenter(this);

        return view;
    }

    static class PostAdapter extends RecyclerView.Adapter {

        List<Post> posts;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feed, viewGroup, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            Holder h = (Holder) viewHolder;
            Post i = posts.get(position);
            new ImageRequest()
                    .setUrl(i.image)
                    .to(h.image, data -> h.image.setImageBitmap(data));
        }

        @Override
        public int getItemCount() {
            return posts == null ? 0 : posts.size();
        }

        public void reloadData(List<Post> posts) {
            this.posts = posts;
            notifyDataSetChanged();
        }

        static class Holder extends RecyclerView.ViewHolder {

            WebImageView image;

            public Holder(View itemView) {
                super(itemView);
                image = (WebImageView) itemView.findViewById(R.id.image);
                image.setMinimumHeight((int) (itemView.getResources().getDisplayMetrics().density * 200));
            }
        }
    }
}