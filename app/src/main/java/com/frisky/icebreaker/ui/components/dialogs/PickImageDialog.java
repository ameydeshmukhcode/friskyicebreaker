package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.Window;
import android.widget.Button;

import com.frisky.icebreaker.R;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class PickImageDialog extends DialogFragment {

    //Button cameraButton;
    private OnImageUpdatedListener onImageUpdatedListener;

    private final int PICK_IMAGE_GALLERY = 0;

    //private final int PICK_IMAGE_CAMERA = 1;

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        if (dialog != null) {
            Button galleryButton = getDialog().findViewById(R.id.button_gallery);
            galleryButton.setOnClickListener(v -> {
                Intent getImageFromDevice = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(getImageFromDevice, PICK_IMAGE_GALLERY);
            });
        }

//        cameraButton = getDialog().findViewById(R.id.button_camera);
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePicture, PICK_IMAGE_CAMERA);
//            }
//        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog pickImageDialog = new Dialog(Objects.requireNonNull(getContext()));
        pickImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pickImageDialog.setContentView(R.layout.dialog_pick_image);

        return pickImageDialog;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            if (requestCode == PICK_IMAGE_GALLERY) {
                this.onImageUpdatedListener.imageUpdated(selectedImage);
                dismiss();
            }
//            else if (requestCode == PICK_IMAGE_CAMERA) {
//
//            }
            dismiss();
        }
    }

    public interface OnImageUpdatedListener {
        void imageUpdated(Uri bitmap);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            this.onImageUpdatedListener = (OnImageUpdatedListener) context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnImageUpdatedListener");
        }
    }
}