package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frisky.icebreaker.R;

import java.util.Objects;

public class ConfirmSessionStartDialog extends DialogFragment {

    private OnConfirmSessionStart onConfirmSessionStart;

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            Button start = getDialog().findViewById(R.id.button_start);
            start.setOnClickListener(v -> {
                onConfirmSessionStart.sessionStart(true);
                dismiss();
            });

            Button cancel = getDialog().findViewById(R.id.button_cancel);
            cancel.setOnClickListener(v -> {
                onConfirmSessionStart.sessionStart(false);
                dismiss();
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog pickImageDialog = new Dialog(Objects.requireNonNull(getContext()));
        pickImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickImageDialog.setContentView(R.layout.dialog_confirm_session_start);

        return pickImageDialog;
    }

    public interface OnConfirmSessionStart {
        void sessionStart(boolean choice);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        this.onConfirmSessionStart.sessionStart(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.onConfirmSessionStart = (ConfirmSessionStartDialog.OnConfirmSessionStart) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnConfirmSessionStart");
        }
    }
}
