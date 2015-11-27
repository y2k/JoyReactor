package y2k.joyreactor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import y2k.joyreactor.presenters.PostPresenter;

import java.util.List;

public class PostActivity extends AppCompatActivity {

    PostPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        CommentAdapter adapter;
        list.setAdapter(adapter = new CommentAdapter());

        presenter = new PostPresenter(new PostPresenter.View() {

            @Override
            public void updateComments(List<Comment> comments) {
                adapter.updatePostComments(comments);
            }

            @Override
            public void updatePostImage(Post post) {
                adapter.updatePostDetails(post);
            }

            @Override
            public void setIsBusy(boolean isBusy) {
                // TODO:
            }

            @Override
            public void showImageSuccessSavedToGallery() {
                Toast.makeText(getApplicationContext(), R.string.image_saved_to_gallery, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.openInBrowser) presenter.openPostInBrowser();
        else if (item.getItemId() == R.id.saveImageToGallery) presenter.saveImageToGallery();
        else super.onOptionsItemSelected(item);
        return true;
    }

    static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

        private List<Comment> comments;
        private Post post;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == 0 ? new HeaderViewHolder(parent) : new CommentViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position - 1);
        }

        @Override
        public int getItemCount() {
            return 1 + (comments == null ? 0 : comments.size());
        }

        public void updatePostDetails(Post post) {
            this.post = post;
            notifyDataSetChanged();
        }

        public void updatePostComments(List<Comment> comments) {
            this.comments = comments;
            notifyDataSetChanged();
        }

        static abstract class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View view) {
                super(view);
            }

            public abstract void bind(int position);
        }

        class HeaderViewHolder extends ViewHolder {

            public HeaderViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_post, parent, false));
            }

            @Override
            public void bind(int position) {
                // TODO
            }
        }

        class CommentViewHolder extends ViewHolder {

            TextView rating;
            TextView text;
            TextView replies;
            WebImageView avatar;

            public CommentViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment, parent, false));
                rating = (TextView) itemView.findViewById(R.id.rating);
                text = (TextView) itemView.findViewById(R.id.text);
                avatar = (WebImageView) itemView.findViewById(R.id.avatar);
                replies = (TextView) itemView.findViewById(R.id.replies);
            }

            @Override
            public void bind(int position) {
                // TODO
                Comment c = comments.get(position);
                text.setText(c.text);
                avatar.setImage(c.getUserImage().toImage());
                rating.setText("" + c.rating);
                replies.setText("" + c.replies);
            }
        }
    }
}