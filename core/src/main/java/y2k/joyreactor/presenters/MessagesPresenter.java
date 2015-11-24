package y2k.joyreactor.presenters;

import y2k.joyreactor.Message;
import y2k.joyreactor.repository.MessageForUserQuery;
import y2k.joyreactor.repository.Repository;
import y2k.joyreactor.services.requests.SendMessageRequest;

import java.util.List;

/**
 * Created by y2k on 01/10/15.
 */
public class MessagesPresenter extends Presenter {

    private View view;
    private Repository<Message> repository;

    public MessagesPresenter(View view) {
        this(view, new Repository<>(Message.class));
    }

    public MessagesPresenter(View view, Repository<Message> repository) {
        this.view = view;
        this.repository = repository;

        // FIXME:
        reloadMessages(getUsername());

        getMessages().add(
                m -> reloadMessages(m.thread.userName),
                MessageThreadsPresenter.ThreadSelectedMessage.class);
    }

    public void reply(String message) {
        view.setIsBusy(true);
        new SendMessageRequest(getUsername())
                .request(message)
                .subscribe(s -> reloadMessages(getUsername()), Throwable::printStackTrace);
    }

    private void reloadMessages(String username) {
        view.setIsBusy(true);
        repository
                .queryAsync(new MessageForUserQuery(username))
                .subscribe((messages) -> {
                    view.updateMessages(messages);
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    private String getUsername() {
        return "user500"; // TODO:
    }

    public interface View {

        void updateMessages(List<Message> messages);

        void setIsBusy(boolean isBusy);
    }
}