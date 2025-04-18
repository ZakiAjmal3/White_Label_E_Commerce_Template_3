package com.example.whitelabeltemplate3.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whitelabeltemplate3.Models.TrackingOrderTimelineModel;
import com.example.whitelabeltemplate3.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

public class TrackingOrderTimelineAdapter extends RecyclerView.Adapter<TrackingOrderTimelineAdapter.ViewHolder> {

    private List<TrackingOrderTimelineModel> orderStages;
    Context context;
    public TrackingOrderTimelineAdapter(List<TrackingOrderTimelineModel> orderStages, Context context) {
        this.orderStages = orderStages;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tracking_order_item_layout, parent, false);
        return new ViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TrackingOrderTimelineModel orderStage = orderStages.get(position);
        holder.stageNameTextView.setText(orderStage.getStageName());
        holder.statusTextView.setText(orderStage.getStatus());
        Drawable markerDrawable = ContextCompat.getDrawable(context, orderStage.getMarkerDrawable());
        holder.timelineView.setMarker(markerDrawable);
        if (position == 0) {
            holder.timelineView.setStartLineColor(ContextCompat.getColor(context, R.color.white), 0);
        }
        if (position == orderStages.size() - 1) {
            holder.timelineView.setEndLineColor(ContextCompat.getColor(context, R.color.white), 0);

        }
    }

    @Override
    public int getItemCount() {
        return orderStages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView stageNameTextView;
        TextView statusTextView;
        TimelineView timelineView;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            stageNameTextView = itemView.findViewById(R.id.stageName);
            statusTextView = itemView.findViewById(R.id.status);
            timelineView = itemView.findViewById(R.id.time_marker);
            timelineView.initLine(viewType);
        }
    }
}