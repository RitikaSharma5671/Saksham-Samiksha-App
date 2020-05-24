package com.samagra.parent.ui.submissions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.odk.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;

public class FilterDialogAdapter extends RecyclerView.Adapter<FilterDialogAdapter.ViewHolder> {


    private final RecyclerViewClickListener listener;
    private final int selectedSortingOrder;
    private final RecyclerView recyclerView;
    private final ThemeUtils themeUtils;
    private final ArrayList<String> formNames;

    public FilterDialogAdapter(Context context, RecyclerView recyclerView, ArrayList<String> formNames, int selectedSortingOrder, RecyclerViewClickListener recyclerViewClickListener) {
        themeUtils = new ThemeUtils(context);
        this.recyclerView = recyclerView;
        this.selectedSortingOrder = selectedSortingOrder;
        listener = recyclerViewClickListener;
        this.formNames = formNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(org.odk.collect.android.R.layout.sort_item_layout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.txtViewTitle.setText(formNames.get(position));
        int color = position == selectedSortingOrder ? themeUtils.getAccentColor() : themeUtils.getPrimaryTextColor();
        viewHolder.txtViewTitle.setTextColor(color);
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return formNames.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        ImageView imgViewIcon;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(org.odk.collect.android.R.id.title);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(ViewHolder.this, getLayoutPosition());
                }
            });
        }

        public void updateItemColor(int selectedSortingOrder) {
            ViewHolder previousHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedSortingOrder);
            previousHolder.txtViewTitle.setTextColor(themeUtils.getPrimaryTextColor());
            txtViewTitle.setTextColor(themeUtils.getAccentColor());
        }
    }
}
