package com.frisky.icebreaker.fragments;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.VisitsListAdapter;
import com.frisky.icebreaker.core.structures.Visit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VisitsFragment extends Fragment {

    private VisitsListAdapter mOrderSummaryAdapter;
    private ConstraintLayout mEmptyState;
    private ShimmerFrameLayout mShimmerFrame;

    private List<Visit> mSessionHistoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visits, container, false);

        RecyclerView mRecyclerPubView = view.findViewById(R.id.recycler_view_visits);
        mEmptyState = view.findViewById(R.id.fragment_empty_state);
        mShimmerFrame = view.findViewById(R.id.shimmer_list);

        mEmptyState.setVisibility(View.GONE);

        mRecyclerPubView.setPadding(0, 0, 0, 0);
        mRecyclerPubView.setPadding(0, 0, 0, 225);
        mRecyclerPubView.setClipToPadding(false);

        RecyclerView.LayoutManager mPubViewLayoutManager;
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        mOrderSummaryAdapter = new VisitsListAdapter(mSessionHistoryList, getContext());
        mRecyclerPubView.setAdapter(mOrderSummaryAdapter);

        getOrderHistory();

        return view;
    }

    private void getOrderHistory() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        mFirestore.collectionGroup("sessions")
            .whereEqualTo("created_by", userID)
            .orderBy("end_time", Query.Direction.DESCENDING)
            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots.getDocuments().size() > 0) {
                    mEmptyState.setVisibility(View.GONE);
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        if (document == null) return;
                        if (document.contains("amount_payable")) {
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                            String endTime = formatter.format((Objects
                                    .requireNonNull(document.getTimestamp("end_time")).toDate()));
                            double total = Double.parseDouble(document.getString("amount_payable"));
                            final DocumentReference restaurantReference = document.getReference().getParent().getParent();
                            restaurantReference.get().addOnCompleteListener(restTask -> {
                                Visit summary = new Visit(restaurantReference.getId(),
                                        Uri.parse(restTask.getResult().getString("image")),
                                        String.valueOf(restTask.getResult().get("name")),
                                        document.getId(), endTime, total);
                                mSessionHistoryList.add(summary);
                                mOrderSummaryAdapter.notifyDataSetChanged();
                                mShimmerFrame.stopShimmer();
                                mShimmerFrame.setVisibility(View.GONE);
                            });
                        }
                    }
                } else {
                    mShimmerFrame.stopShimmer();
                    mShimmerFrame.setVisibility(View.GONE);
                    mEmptyState.setVisibility(View.VISIBLE);
                }
        });
    }
}
