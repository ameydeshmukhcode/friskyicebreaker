package com.frisky.icebreaker.profile;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.ui.components.dialogs.PickImageDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditImagesFragment extends Fragment implements PickImageDialog.OnImageUpdatedListener {

    RecyclerView mRecyclerImageGridView;
    RecyclerView.LayoutManager mEditImagesGridLayoutManager;
    RecyclerView.Adapter mImageGridAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_recycler_view, null);

        mRecyclerImageGridView = view.findViewById(R.id.recycler_view);

        mRecyclerImageGridView.setHasFixedSize(true);

        // use a grid layout manager
        mEditImagesGridLayoutManager = new GridLayoutManager(getContext(), 3);
        mRecyclerImageGridView.setLayoutManager(mEditImagesGridLayoutManager);

        // specify an adapter (see also next example)
        mImageGridAdapter = new EditImagesAdapter(getContext(), getActivity());
        mRecyclerImageGridView.setAdapter(mImageGridAdapter);

        return view;
    }

    @Override
    public void imageUpdated(Bitmap bitmap) {
        
    }
}
