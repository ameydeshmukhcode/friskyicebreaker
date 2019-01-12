package com.frisky.icebreaker.ui.components.bottomsheet.filters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.frisky.icebreaker.R;

import java.util.List;

public class BottomSheetFiltersViewAdapter extends RecyclerView.Adapter<BottomSheetFiltersViewAdapter.FiltersViewHolder> {

    private List<String> mFiltersList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class FiltersViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox checkBox;
        public FiltersViewHolder(View v) {
            super(v);
            checkBox = v.findViewById(R.id.checkbox_filters);
        }
    }

    public BottomSheetFiltersViewAdapter(List<String> pubList) {
        this.mFiltersList = pubList;
    }

    @NonNull
    @Override
    public BottomSheetFiltersViewAdapter.FiltersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.checkbox_filters, viewGroup, false);

        return new BottomSheetFiltersViewAdapter.FiltersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSheetFiltersViewAdapter.FiltersViewHolder viewHolder, int i) {
        String filter = mFiltersList.get(i);
        viewHolder.checkBox.setText(filter);
    }

    @Override
    public int getItemCount() {
        return mFiltersList.size();
    }

}
