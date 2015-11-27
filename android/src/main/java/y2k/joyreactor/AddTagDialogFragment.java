package y2k.joyreactor;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import y2k.joyreactor.presenters.AddTagPresenter;

/**
 * Created by y2k on 11/27/15.
 */
public class AddTagDialogFragment extends AppCompatDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_tag, container, false);

        View cancelButton = view.findViewById(R.id.cancel);
        View okButton = view.findViewById(R.id.ok);
        TextView tagView = (TextView) view.findViewById(R.id.tag);

        AddTagPresenter presenter = new AddTagPresenter(new AddTagPresenter.View() {

            @Override
            public void setIsBusy(boolean isBusy) {
                okButton.setEnabled(!isBusy);
                cancelButton.setEnabled(!isBusy);
                tagView.setEnabled(!isBusy);
                setCancelable(!isBusy);
            }

            @Override
            public void showErrorMessage() {
                Toast.makeText(App.getInstance(), R.string.unknown_error_occurred, Toast.LENGTH_LONG).show();
            }
        });

        cancelButton.setOnClickListener(v -> dismiss());
        okButton.setOnClickListener(v -> presenter.add("" + tagView.getText()));

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.add_tag);
        return dialog;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        AddTagPresenter presenter = new AddTagPresenter(new AddTagPresenter.View() {
//
//            @Override
//            public void setIsBusy(boolean isBusy) {
//                // TODO
//            }
//
//            @Override
//            public void showErrorMessage() {
//                Toast.makeText(App.getInstance(), R.string.unknown_error_occurred, Toast.LENGTH_LONG).show();
//            }
//        });
//
////        return new AlertDialog.Builder(getContext())
////                .setTitle(R.string.add_tag)
////                .setView(R.layout.layout_add_tag)
////                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
////                    TextView tagView = (TextView) getDialog().findViewById(R.id.tag);
////                    presenter.add("" + tagView.getText());
////                })
////                .setNegativeButton(android.R.string.cancel, null)
////                .create();
//
//
//    }
}