package y2k.joyreactor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.MessageThreadsPresenter;

import java.util.List;

/**
 * Created by y2k on 11/13/15.
 */
public class ThreadsFragment extends Fragment {

    private MessageThreadsPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_threads, container, false);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        ThreadAdapter adapter = new ThreadAdapter();
        list.setAdapter(adapter);

        View progress = view.findViewById(R.id.progress);

        presenter = ServiceLocator.getInstance().provideMessageThreadsPresenter(
                new MessageThreadsPresenter.View() {

                    @Override
                    public void setIsBusy(boolean isBusy) {
                        progress.setVisibility(isBusy ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void reloadData(List<? extends Message> threads) {
                        adapter.updateData(threads);
                    }
                });

        return view;
    }

    class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ViewHolder> {

        private List<? extends Message> threads;

        public void updateData(List<? extends Message> threads) {
            this.threads = threads;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_message_thread, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Message t = threads.get(position);
            holder.userImage.setImage(t.getUserImage().toImage());
            holder.userName.setText(t.userName);
            holder.lastMessage.setText(t.text);
            holder.time.setText("" + t.date);
        }

        @Override
        public int getItemCount() {
            return threads == null ? 0 : threads.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            WebImageView userImage;
            TextView userName;
            TextView lastMessage;
            TextView time;

            public ViewHolder(View view) {
                super(view);

                userImage = (WebImageView) view.findViewById(R.id.userImage);
                userName = (TextView) view.findViewById(R.id.userName);
                lastMessage = (TextView) view.findViewById(R.id.lastMessage);
                time = (TextView) view.findViewById(R.id.time);

                view.findViewById(R.id.button).setOnClickListener(
                        v -> presenter.selectThread(getAdapterPosition()));
            }
        }
    }
}