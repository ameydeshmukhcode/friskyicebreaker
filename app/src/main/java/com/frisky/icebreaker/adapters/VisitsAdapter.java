package com.frisky.icebreaker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.frisky.icebreaker.R;
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

public class VisitsAdapter extends PagerAdapter {

    private Context context;
    private List<OrderSummary> mSessionHistoryList = new ArrayList<>();
    private OrderSummaryListAdapter mOrderSummaryAdapter;

    public VisitsAdapter(Context applicationContext) {
        context = applicationContext;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0: title = "Active";
                break;
            case 1: title = "History";
                break;
        }
        return title;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.empty_state_fragment_active_visits, container, false);
        switch (position) {
            case 0:
                layout = (ViewGroup) inflater.inflate(R.layout.empty_state_fragment_active_visits, container, false);
                break;
            case 1:
                layout = (ViewGroup) inflater.inflate(R.layout.recycler_view, container, false);

                getOrderHistory();

                RecyclerView mRecyclerPubView;
                mRecyclerPubView = layout.findViewById(R.id.recycler_view);

                mRecyclerPubView.setPadding(0, 0, 0, 0);
                mRecyclerPubView.setPadding(0, 0, 0, 225);
                mRecyclerPubView.setClipToPadding(false);

                RecyclerView.LayoutManager mPubViewLayoutManager;
                // use a linear layout manager
                mPubViewLayoutManager = new LinearLayoutManager(context);
                mRecyclerPubView.setLayoutManager(mPubViewLayoutManager);

                mOrderSummaryAdapter = new OrderSummaryListAdapter(mSessionHistoryList, context);
                mRecyclerPubView.setAdapter(mOrderSummaryAdapter);
                break;
        }
        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
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
                        if (document.contains("bill_amount")) {
                            @SuppressLint("SimpleDateFormat")
                            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM YYYY hh:mm a");
                            String endTime = formatter.format((Objects
                                    .requireNonNull(document.getTimestamp("end_time")).toDate()));
                            int total = Integer.parseInt(document.get("bill_amount").toString());
                            final DocumentReference restaurantReference = document.getReference().getParent().getParent();
                            restaurantReference.get().addOnCompleteListener(restTask -> {
                                OrderSummary summary = new OrderSummary(restaurantReference.getId(),
                                        Uri.parse(restTask.getResult().getString("image")),
                                        String.valueOf(restTask.getResult().get("name")),
                                        document.getId(), endTime, total);
                                mSessionHistoryList.add(summary);
                                mOrderSummaryAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                }
            }
            else {
                Log.e("error", "Error getting documents: ", task.getException());
            }
        });
    }
}
