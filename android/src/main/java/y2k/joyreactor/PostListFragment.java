package y2k.joyreactor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.ocpsoft.prettytime.PrettyTime;
import y2k.joyreactor.common.ComplexViewHolder;
import y2k.joyreactor.common.ItemDividerDecoration;
import y2k.joyreactor.common.Optional;
import y2k.joyreactor.platform.ImageRequest;
import y2k.joyreactor.presenters.PostListPresenter;

import java.util.List;

/**
 * Created by y2k on 9/26/15.
 */
public class PostListFragment extends Fragment {

    PostListPresenter presenter;
    PostAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        view.findViewById(R.id.error).setVisibility(View.GONE);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        list.setAdapter(adapter = new PostAdapter());
        list.addItemDecoration(new ItemDividerDecoration(list));

        presenter = new PostListPresenter(new PostListPresenter.View() {

            @Override
            public void setBusy(boolean isBusy) {
                // TODO:
            }

            @Override
            public void reloadPosts(List<Post> posts, Integer divider) {
                adapter.reloadData(posts, Optional.ofNullable(divider));
            }

            @Override
            public void setHasNewPosts(boolean hasNewPosts) {
                ((ReloadButton) getView().findViewById(R.id.apply)).setVisibility(hasNewPosts);
            }
        });

        view.findViewById(R.id.apply).setOnClickListener(v -> presenter.applyNew());
        return view;
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

    class PostAdapter extends RecyclerView.Adapter<ComplexViewHolder> {

        private PrettyTime prettyTime = new PrettyTime();
        private Optional<Integer> divider = Optional.empty();
        private List<Post> posts;

        @Override
        public int getItemViewType(int position) {
            return divider.isPresent() && divider.get() == position ? 1 : 0;
        }

        @Override
        public ComplexViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
            return itemType == 0 ? new PostViewHolder(viewGroup) : new DividerHolder(viewGroup);
        }

        @Override
        public void onBindViewHolder(ComplexViewHolder h, int position) {
            h.bind();
        }

        @Override
        public int getItemCount() {
            return (posts == null ? 0 : posts.size()) + (divider.isPresent() ? 1 : 0);
        }

        public void reloadData(List<Post> posts, Optional<Integer> divider) {
            this.posts = posts;
            this.divider = divider;
            notifyDataSetChanged();
        }

        private Post getPost(int position) {
            if (divider.isPresent() && position > divider.get())
                position--;
            return posts.get(position);
        }

        class PostViewHolder extends ComplexViewHolder {

            FixedAspectPanel imagePanel;
            WebImageView image;
            WebImageView userImage;
            View videoMark;
            TextView commentCount;
            TextView time;
            TextView userName;

            public PostViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_feed, parent, false));

                image = (WebImageView) itemView.findViewById(R.id.image);
                imagePanel = (FixedAspectPanel) itemView.findViewById(R.id.imagePanel);
                userImage = (WebImageView) itemView.findViewById(R.id.userImage);
                videoMark = itemView.findViewById(R.id.videoMark);
                commentCount = (TextView) itemView.findViewById(R.id.commentCount);
                time = (TextView) itemView.findViewById(R.id.time);
                userName = (TextView) itemView.findViewById(R.id.userName);

                itemView.findViewById(R.id.card).setOnClickListener(
                        v -> presenter.postClicked(posts.get(getAdapterPosition())));
                itemView.findViewById(R.id.videoMark).setOnClickListener(
                        v -> presenter.playClicked(posts.get(getAdapterPosition())));
            }

            @Override
            public void bind() {
                Post i = getPost(getAdapterPosition());

                if (i.image == null) {
                    imagePanel.setVisibility(View.GONE);
                } else {
                    imagePanel.setVisibility(View.VISIBLE);
                    imagePanel.setAspect(i.image.getAspect(0.5f));

                    new ImageRequest()
                            .setUrl(i.image)
                            .setSize(200, (int) (200 / i.image.getAspect(0.5f)))
                            .to(i.image, image::setImageBitmap);
                }

                userImage.setImage(i.getUserImage().toImage());
                userName.setText(i.userName);
                videoMark.setVisibility(i.image != null && i.image.isAnimated() ? View.VISIBLE : View.GONE);

                commentCount.setText("" + i.commentCount);
                time.setText(prettyTime.format(i.created));
            }
        }

        class DividerHolder extends ComplexViewHolder {

            public DividerHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_post_divider, parent, false));
                ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
                itemView.findViewById(R.id.dividerButton).setOnClickListener(v -> presenter.loadMore());
            }
        }
    }
}