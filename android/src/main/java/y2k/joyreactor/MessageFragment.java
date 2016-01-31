package y2k.joyreactor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import org.ocpsoft.prettytime.PrettyTime;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.MessagesPresenter;

import java.util.List;

/**
 * Created by y2k on 11/20/15.
 */
public class MessageFragment extends Fragment implements MessagesPresenter.View {

    private MessageAdapter adapter;
    private MessagesPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        list.setAdapter(adapter = new MessageAdapter());

        EditText newMessage = (EditText) view.findViewById(R.id.newMessage);
        view.findViewById(R.id.createMessage)
            .setOnClickListener(v -> presenter.reply("" + newMessage.getText()));

        presenter = ServiceLocator.getInstance().provideMessagesPresenter(this);
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

    @Override
    public void updateMessages(List<Message> messages) {
        adapter.update(messages);
    }

    @Override
    public void setIsBusy(boolean isBusy) {
        // TODO:
    }

    static class MessageAdapter extends RecyclerView.Adapter<ViewHolderImpl> {

        private List<? extends Message> items;

        public void update(List<? extends Message> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolderImpl onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderImpl(LayoutInflater.from(
                parent.getContext()).inflate(viewType, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolderImpl holder, int position) {
            Message i = items.get(position);
            holder.message.setText(i.getText());
            holder.created.setText(new PrettyTime().format(i.getDate()));
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position).isMine())
                return isFirst(position)
                    ? R.layout.item_message_outbox_first
                    : R.layout.item_message_outbox;
            return isFirst(position)
                ? R.layout.item_message_inbox_first
                : R.layout.item_message_inbox;
        }

        private boolean isFirst(int position) {
            return position == items.size() - 1
                || items.get(position).isMine() != items.get(position + 1).isMine();
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }
    }

    static class ViewHolderImpl extends RecyclerView.ViewHolder {

        TextView message;
        TextView created;

        public ViewHolderImpl(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.message);
            created = (TextView) view.findViewById(R.id.created);
        }
    }
}