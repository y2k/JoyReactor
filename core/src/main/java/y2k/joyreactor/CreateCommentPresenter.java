package y2k.joyreactor;

import rx.Observable;

/**
 * Created by y2k on 10/4/15.
 */
public class CreateCommentPresenter {

    private View view;

    public CreateCommentPresenter(View view) {
        this.view = view;
    }

    public void create() {
        view.setIsBusy(true);
        new CreateCommentRequest()
                .request(view.getCommentText())
                .subscribe(s -> {
                    // TODO:
                    view.setIsBusy(false);
                }, Throwable::printStackTrace);
    }

    public interface View {

        String getCommentText();

        void setIsBusy(boolean isBusy);
    }

    private static class CreateCommentRequest {

        public Observable<Void> request(String text) {
            return ObservableUtils.create(() -> {
                // TODO:

                return null;

            });
        }
    }
}
