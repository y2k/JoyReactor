package y2k.joyreactor;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import y2k.joyreactor.common.ComplexViewHolder;
import y2k.joyreactor.common.DependencyInjection;
import y2k.joyreactor.platform.ImageRequest;
import y2k.joyreactor.presenters.PostPresenter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    PostPresenter presenter;
    File imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        Adapter adapter;
        list.setAdapter(adapter = new Adapter());

        ProgressBar imageProgress = (ProgressBar) findViewById(R.id.imageProgress);

        findViewById(R.id.createComment).setOnClickListener(v -> presenter.replyToPost());

        presenter = DependencyInjection.getInstance().providePostPresenter(
                new PostPresenter.View() {

                    @Override
                    public void updateComments(CommentGroup comments) {
                        adapter.updatePostComments(comments);
                    }

                    @Override
                    public void updatePostInformation(Post post) {
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

                    @Override
                    public void updatePostImages(List<? extends Image> images) {
                        adapter.updatePostImages(images);
                    }

                    @Override
                    public void updateSimilarPosts(List<? extends SimilarPost> similarPosts) {
                        adapter.updateSimilarPosts(similarPosts);
                    }

                    @Override
                    public void updatePostImage(File imagePath) {
                        PostActivity.this.imagePath = imagePath;
                        adapter.notifyItemChanged(0);
                        imageProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void updateImageDownloadProgress(int progress, int maxProgress) {
                        imageProgress.setProgress(progress);
                        imageProgress.setMax(maxProgress);
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
        if (item.getItemId() == R.id.reply) presenter.replyToPost();
        else if (item.getItemId() == R.id.openInBrowser) presenter.openPostInBrowser();
        else if (item.getItemId() == R.id.saveImageToGallery) saveImageToGallery();
        else super.onOptionsItemSelected(item);
        return true;
    }

    private void saveImageToGallery() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            presenter.saveImageToGallery();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
        }
    }

    class Adapter extends RecyclerView.Adapter<ComplexViewHolder> {

        private CommentGroup comments;
        private Post post;
        private List<? extends Image> images = Collections.emptyList();
        private List<? extends SimilarPost> similarPosts = Collections.emptyList();

        public Adapter() {
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            return position == 0 ? 0 : comments.get(position - 1).id;
        }

        @Override
        public int getItemViewType(int position) {
            return Math.min(1, position);
        }

        @Override
        public ComplexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) return new HeaderViewHolder(parent);
            return new CommentViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ComplexViewHolder holder, int position) {
            holder.bind();
        }

        @Override
        public int getItemCount() {
            return 1 + (comments == null ? 0 : comments.size());
        }

        public void updatePostComments(CommentGroup comments) {
            this.comments = comments;
            notifyDataSetChanged();
        }

        public void updatePostDetails(Post post) {
            this.post = post;
            notifyItemChanged(0);
        }

        public void updatePostImages(List<? extends Image> images) {
            this.images = images;
            notifyItemChanged(0);
        }

        public void updateSimilarPosts(List<? extends SimilarPost> similarPosts) {
            this.similarPosts = similarPosts;
            notifyItemChanged(0);
        }

        class HeaderViewHolder extends ComplexViewHolder {

            LargeImageView image;
            ImagePanel imagePanel;
            ImagePanel similar;
            FixedAspectPanel posterPanel;

            public HeaderViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_post, parent, false));
                image = (LargeImageView) itemView.findViewById(R.id.image);
                imagePanel = (ImagePanel) itemView.findViewById(R.id.images);
                similar = (ImagePanel) itemView.findViewById(R.id.similar);
                posterPanel = (FixedAspectPanel) itemView.findViewById(R.id.posterPanel);
            }

            @Override
            public void bind() {
                // TODO
                if (post != null)
                    posterPanel.setAspect(post.getImage().getAspect());
                if (imagePath != null)
                    image.setImage(imagePath);

                imagePanel.setImages(images);
                similar.setImages(toImages());
            }

            private List<Image> toImages() {
                List<Image> result = new ArrayList<>();
                for (SimilarPost s : similarPosts)
                    result.add(s.getImage());
                return result;
            }
        }

        class CommentViewHolder extends ComplexViewHolder {

            TextView rating;
            TextView text;
            TextView replies;
            WebImageView avatar;
            WebImageView attachment;

            public CommentViewHolder(ViewGroup parent) {
                super(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment, parent, false));
                rating = (TextView) itemView.findViewById(R.id.rating);
                text = (TextView) itemView.findViewById(R.id.text);
                avatar = (WebImageView) itemView.findViewById(R.id.avatar);
                replies = (TextView) itemView.findViewById(R.id.replies);
                attachment = (WebImageView) itemView.findViewById(R.id.attachment);

                itemView.findViewById(R.id.action).setOnClickListener(v ->
                        presenter.selectComment(comments.getId(getRealPosition())));

                View commentButton = itemView.findViewById(R.id.commentMenu);
                commentButton.setOnClickListener(v -> {
                    PopupMenu menu = new PopupMenu(parent.getContext(), commentButton);
                    menu.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getItemId() == R.id.reply)
                            presenter.replyToComment(comments.get(getRealPosition()));
                        return true;
                    });
                    menu.inflate(R.menu.comment);
                    menu.show();
                });
            }

            @Override
            public void bind() {
                ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).leftMargin =
                        comments.isChild(getRealPosition()) ? toPx(64) : toPx(8);

                Comment c = comments.get(getRealPosition());
                text.setText(c.text);
                avatar.setImage(c.getUserImage().toImage());
                rating.setText("" + c.rating);
                replies.setText("" + c.replies);

                attachment.setVisibility(c.getAttachment() == null ? View.GONE : View.VISIBLE);
                attachment.setImage(c.getAttachment());
            }

            private int getRealPosition() {
                return getAdapterPosition() - 1;
            }

            private int toPx(int dip) {
                return (int) (dip * itemView.getResources().getDisplayMetrics().density);
            }
        }
    }
}