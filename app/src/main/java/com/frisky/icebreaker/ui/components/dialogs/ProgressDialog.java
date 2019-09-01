package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frisky.icebreaker.R;

import java.util.Objects;

public class ProgressDialog extends DialogFragment {

    private String title;

    public ProgressDialog(String title) {
        this.title = title;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            TextView titleText = dialog.findViewById(R.id.text_progress_dialog);
            titleText.setText(title);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog progressDialog = new Dialog(Objects.requireNonNull(getContext()));
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.dialog_progress);

        return progressDialog;
    }
}
