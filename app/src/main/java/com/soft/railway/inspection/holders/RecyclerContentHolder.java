package com.soft.railway.inspection.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.railway.inspection.R;

public class RecyclerContentHolder extends RecyclerView.ViewHolder {
    private TextView typeTextView;
    private TextView timeTextView;
    private TextView placeTextView;
    private ImageView statusImageView;

    public RecyclerContentHolder(@NonNull View itemView) {
        super(itemView);
        this.typeTextView=(TextView)itemView.findViewById(R.id.item_work_content_type);
        this.timeTextView=(TextView)itemView.findViewById(R.id.item_work_content_time);
        this.placeTextView=(TextView)itemView.findViewById(R.id.item_work_content_place);
        this.statusImageView=(ImageView) itemView.findViewById(R.id.item_work_content_status);
    }

    public TextView getTypeTextView() {
        return typeTextView;
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public TextView getPlaceTextView() {
        return placeTextView;
    }

    public ImageView getStatusImageView() {
        return statusImageView;
    }
}
