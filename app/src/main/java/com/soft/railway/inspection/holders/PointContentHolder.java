package com.soft.railway.inspection.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.soft.railway.inspection.R;

public class PointContentHolder extends RecyclerView.ViewHolder {
    private TextView pointName;
    private TextView pointDoneNum;
    private TextView cardNum;
    public PointContentHolder(View itemView) {
        super(itemView);
         this.pointName        =(TextView)itemView.findViewById(R.id.item_point_testing_result_point)  ;
         this.pointDoneNum     =(TextView)itemView.findViewById(R.id.item_point_testing_result_point_done_num)  ;
         this.cardNum          =(TextView)itemView.findViewById(R.id.item_point_testing_result_point_card_num)  ;
    }

    public TextView getPointName() {
        return pointName;
    }

    public void setPointName(TextView pointName) {
        this.pointName = pointName;
    }

    public TextView getPointDoneNum() {
        return pointDoneNum;
    }

    public void setPointDoneNum(TextView pointDoneNum) {
        this.pointDoneNum = pointDoneNum;
    }

    public TextView getCardNum() {
        return cardNum;
    }

    public void setCardNum(TextView cardNum) {
        this.cardNum = cardNum;
    }
}
