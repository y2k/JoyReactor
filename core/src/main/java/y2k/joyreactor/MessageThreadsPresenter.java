package y2k.joyreactor;

/**
 * Created by y2k on 01/10/15.
 */
public class MessageThreadsPresenter {

    public MessageThreadsPresenter(View view) {
        view.setIsBusy(true);
        MessageThread.Collection
                .request()
                .subscribe(threads -> {
                    view.setIsBusy(false);
                    view.reloadData(threads);
                });
    }

    public interface View {

        void setIsBusy(boolean isBusy);

        void reloadData(MessageThread.Collection threads);
    }
}