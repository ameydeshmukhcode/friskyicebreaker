package com.frisky.icebreaker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.adapters.OrderSummaryListAdapter;
import com.frisky.icebreaker.core.structures.OrderSummary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistoryFragment extends Fragment {

    private List<OrderSummary> mSessionHistoryList = new ArrayList<>();
    private OrderSummaryListAdapter mOrderSummaryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        getOrderHistory();

        RecyclerView mRecyclerPubView;
        mRecyclerPubView = view.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mPubViewLayoutManager;
        // use a linear layout manager
        mPubViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

        mOrderSummaryAdapter = new OrderSummaryListAdapter(mSessionHistoryList, getContext());
        mRecyclerPubView.setAdapter(mOrderSummaryAdapter);

        return view;
    }

    private void getOrderHistory() {
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collectionGroup("sessions")
                .whereEqualTo("created_by", Objects.requireNonNull(FirebaseAuth.getInstance()
                        .getCurrentUser()).getUid())
                .orderBy("end_time", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult() != null) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document == null)
                            return;
                        @SuppressLint("SimpleDateFormat")
                        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                        String endTime = formatter.format((Objects
                                .requireNonNull(document.getTimestamp("end_time")).toDate()));
                        int total = Integer.parseInt(document.get("bill_amount").toString());
                        final DocumentReference restaurantReference = document.getReference().getParent().getParent();
                        restaurantReference.get().addOnCompleteListener(restTask -> {
                            OrderSummary summary = new OrderSummary(restaurantReference.getId(),
                                    String.valueOf(restTask.getResult().get("name")),
                            document.getId(), endTime, total);
                            mSessionHistoryList.add(summary);
                            mOrderSummaryAdapter.notifyDataSetChanged();
                        });
                    }
                }
            }
            else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }
}
