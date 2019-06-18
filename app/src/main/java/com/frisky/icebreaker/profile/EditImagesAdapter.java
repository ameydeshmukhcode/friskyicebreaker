package com.frisky.icebreaker.profile;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditImagesAdapter extends RecyclerView.Adapter<EditImagesAdapter.ImageViewHolder> {

    private FragmentActivity mActivity;
    private PickImageDialog pickImageDialog;

    private List<Uri> mImageList = new ArrayList<>();

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        ImageViewHolder(View v) {
            super(v);
            mImage = v.findViewById(R.id.image_edit);
        }
    }

    EditImagesAdapter(Context context, FragmentActivity activity) {
        mActivity = activity;
        getProfileImage();
    }

    @NonNull
    @Override
    public EditImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_edit);
        Picasso.get().load(mImageList.get(i)).into(imageView);

        return new ImageViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, int i) {
        viewHolder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageDialog = new PickImageDialog();
                pickImageDialog.show(mActivity.getSupportFragmentManager(), "pick image dialog");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    public void addToImageList(Uri image) {
        mImageList.add(image);
    }

    private void getProfileImage() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        StorageReference profileImageRef = storageReference.child("profile_images").child(auth.getCurrentUser().getUid());

        profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                addToImageList(uri);
                notifyDataSetChanged();
                Log.i("Image Uri Downloaded", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Uri Download Failed", e.getMessage());
            }
        });
    }
}
