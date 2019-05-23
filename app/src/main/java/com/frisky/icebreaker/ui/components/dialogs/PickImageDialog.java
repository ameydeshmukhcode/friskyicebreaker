package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.frisky.icebreaker.R;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PickImageDialog extends DialogFragment {

    Button galleryButton;
    Button cameraButton;

    private OnImageUpdatedListener onImageUpdatedListener;

    final int PICK_IMAGE_GALLERY = 0;
    final int PICK_IMAGE_CAMERA = 1;

    @Override
    public void onStart() {
        super.onStart();
        galleryButton = getDialog().findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImageFromDevice = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(getImageFromDevice, PICK_IMAGE_GALLERY);
            }
        });

        cameraButton = getDialog().findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, PICK_IMAGE_CAMERA);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog pickImageDialog = new Dialog(getContext());
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
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    this.onImageUpdatedListener.imageUpdated(bitmap);
                    dismiss();
                } catch (IOException exp) {
                    Log.e("IOException", "Image not found", exp);
                }
            } else if (requestCode == PICK_IMAGE_CAMERA) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                this.onImageUpdatedListener.imageUpdated(photo);
                dismiss();            }
        }
    }

    public static interface OnImageUpdatedListener {
        public abstract void imageUpdated(Bitmap bitmap);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.onImageUpdatedListener = (OnImageUpdatedListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnImageUpdatedListener");
        }
    }
}