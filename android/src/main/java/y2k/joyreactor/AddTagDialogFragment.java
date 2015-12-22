package y2k.joyreactor;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import y2k.joyreactor.common.ServiceLocator;
import y2k.joyreactor.presenters.AddTagPresenter;

/**
 * Created by y2k on 11/27/15.
 */
public class AddTagDialogFragment extends AppCompatDialogFragment {

    private static final String TAG_ID = "add_tag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_add_tag, container, false);

        View cancelButton = view.findViewById(R.id.cancel);
        View okButton = view.findViewById(R.id.ok);
        TextView tagView = (TextView) view.findViewById(R.id.tag);

        AddTagPresenter presenter = ServiceLocator.getInstance().provideAddTagPresenter(
                new AddTagPresenter.View() {

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

    public static void show(FragmentManager fragmentManager) {
        new AddTagDialogFragment().show(fragmentManager, TAG_ID);
    }

    public static void dismiss(AppCompatActivity activity) {
        AddTagDialogFragment dialog = (AddTagDialogFragment) activity
                .getSupportFragmentManager().findFragmentByTag(TAG_ID);
        dialog.dismiss();
    }
}