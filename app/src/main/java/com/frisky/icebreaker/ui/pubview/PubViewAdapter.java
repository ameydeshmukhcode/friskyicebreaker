package com.frisky.icebreaker.ui.pubview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frisky.icebreaker.R;
import com.frisky.icebreaker.core.structures.Pub;

import java.util.List;

public class PubViewAdapter extends RecyclerView.Adapter<PubViewAdapter.PubViewHolder> {

    private List<Pub> mPubList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PubViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitle;
        public TextView mDescription;
        public PubViewHolder(View v) {
            super(v);
            mTitle = v.findViewById(R.id.title);
            mDescription = v.findViewById(R.id.description);
        }
    }

    public PubViewAdapter(List<Pub> pubList) {
        this.mPubList = pubList;
    }

    @NonNull
    @Override
    public PubViewAdapter.PubViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_pub, viewGroup, false);

        return new PubViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PubViewHolder viewHolder, int i) {
        Pub pub = mPubList.get(i);
        viewHolder.mTitle.setText(pub.getmName());
        viewHolder.mDescription.setText(pub.getmDesc());
    }

    @Override
    public int getItemCount() {
        return mPubList.size();
    }
}
