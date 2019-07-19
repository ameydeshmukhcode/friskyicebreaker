package com.frisky.icebreaker.profile;

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
import com.frisky.icebreaker.ui.assistant.RoundRectTransformation;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
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
        ImageViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.image_edit);
        }
    }

    EditImagesAdapter(FragmentActivity activity) {
        mActivity = activity;
        getProfileImage();
    }

    @NonNull
    @Override
    public EditImagesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int viewType) {
        final View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_edit_image, viewGroup, false);

        ImageView imageView = itemView.findViewById(R.id.image_edit);
        Picasso.get().load(mImageList.get(viewType)).transform(new RoundRectTransformation()).into(imageView);

        return new ImageViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, int position) {
        viewHolder.mImage.setOnClickListener(v -> {
            pickImageDialog = new PickImageDialog();
            pickImageDialog.show(mActivity.getSupportFragmentManager(), "pick image dialog");
        });
    }

    @Override
    public int getItemCount() {
        return mImageList.size();
    }

    private void addToImageList(Uri image) {
        mImageList.add(image);
    }

    private void getProfileImage() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        StorageReference profileImageRef = null;

        if (firebaseAuth.getCurrentUser() != null)
            profileImageRef = storageReference.child("profile_images").child(firebaseAuth.getCurrentUser().getUid());

        if (profileImageRef != null) {
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                addToImageList(uri);
                notifyDataSetChanged();
                Log.i("Image Uri Downloaded", uri.toString());
            }).addOnFailureListener(e -> Log.e("Uri Download Failed", e.getMessage()));
        }
    }
}
