package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frisky.icebreaker.R;

import java.util.Objects;

public class ConfirmOrderDialog extends DialogFragment {

    private OnConfirmOrderListener onConfirmOrderListener;

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            Button ok = getDialog().findViewById(R.id.button_ok);
            ok.setOnClickListener(v -> {
                onConfirmOrderListener.confirmOrder(true);
                dismiss();
            });

            Button cancel = getDialog().findViewById(R.id.button_cancel);
            cancel.setOnClickListener(v -> {
                onConfirmOrderListener.confirmOrder(false);
                dismiss();
            });
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog pickImageDialog = new Dialog(Objects.requireNonNull(getContext()));
        pickImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickImageDialog.setContentView(R.layout.dialog_confirm_order);

        return pickImageDialog;
    }

    public interface OnConfirmOrderListener {
        void confirmOrder(boolean choice);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.onConfirmOrderListener = (ConfirmOrderDialog.OnConfirmOrderListener) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnConfirmOrderListener");
        }
    }
}
