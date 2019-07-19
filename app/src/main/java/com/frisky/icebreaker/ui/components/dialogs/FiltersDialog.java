package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.frisky.icebreaker.R;

public class FiltersDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog filtersDialog = new Dialog(getContext());
        filtersDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        filtersDialog.setContentView(R.layout.dialog_filters);

        return filtersDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Button mCancelButton;
        Button mApplyButton;

        mApplyButton = getDialog().findViewById(R.id.button_apply);
        mCancelButton = getDialog().findViewById(R.id.button_cancel);

        mCancelButton.setOnClickListener(v -> dismiss());
        mApplyButton.setOnClickListener(v -> dismiss());
    }
}
