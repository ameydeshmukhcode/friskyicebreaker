package com.frisky.icebreaker.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
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
    private RecyclerView mRecyclerPubView;
    private OrderSummaryListAdapter mOrderSummaryAdapter;

    private View rootView;
    private ViewGroup rootContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.progress_icon, container, false);
        rootContainer = container;

        getOrderHistory();

        return rootView;
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
                    if (task.getResult().size() > 0) {

                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        rootView = inflater.inflate(R.layout.fragment_order_summary_list, rootContainer, false);
                        ViewGroup view = (ViewGroup) getView();

                        mRecyclerPubView = rootView.findViewById(R.id.recycler_view);

                        mRecyclerPubView.setVisibility(View.GONE);

                        mRecyclerPubView.setPadding(0, 0, 0, 0);
                        mRecyclerPubView.setPadding(0, 0, 0, 225);
                        mRecyclerPubView.setClipToPadding(false);

                        RecyclerView.LayoutManager mPubViewLayoutManager;
                        // use a linear layout manager
                        mPubViewLayoutManager = new LinearLayoutManager(getContext());
                        mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

                        mOrderSummaryAdapter = new OrderSummaryListAdapter(mSessionHistoryList, getContext());
                        mRecyclerPubView.setAdapter(mOrderSummaryAdapter);

                        view.removeAllViews();
                        view.addView(rootView);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document == null)
                                return;
                            if (document.contains("amount_payable")) {
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                                String endTime = formatter.format((Objects
                                        .requireNonNull(document.getTimestamp("end_time")).toDate()));
                                double total = Double.parseDouble(document.getString("amount_payable"));
                                final DocumentReference restaurantReference = document.getReference().getParent().getParent();
                                restaurantReference.get().addOnCompleteListener(restTask -> {
                                    OrderSummary summary = new OrderSummary(restaurantReference.getId(),
                                            Uri.parse(restTask.getResult().getString("image")),
                                            String.valueOf(restTask.getResult().get("name")),
                                            document.getId(), endTime, total);
                                    mSessionHistoryList.add(summary);
                                    mRecyclerPubView.setVisibility(View.VISIBLE);
                                    mOrderSummaryAdapter.notifyDataSetChanged();
                                });
                            }
                        }
                    }
                    else {
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        rootView = inflater.inflate(R.layout.fragment_order_summary_empty, rootContainer, false);
                        ViewGroup view = (ViewGroup) getView();
                        view.removeAllViews();
                        view.addView(rootView);
                    }
                }

            }
            else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }
}
