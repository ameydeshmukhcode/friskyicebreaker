package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.Button;

import com.frisky.icebreaker.R;

public class PickImageDialog extends DialogFragment {

    Button galleryButton;
    Button cameraButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog pickImageDialog = new Dialog(getContext());
        pickImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickImageDialog.setContentView(R.layout.dialog_pick_image);

        return pickImageDialog;
    }
}