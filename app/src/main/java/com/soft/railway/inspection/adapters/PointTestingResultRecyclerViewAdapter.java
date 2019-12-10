package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.holders.PointContentHolder;
import com.soft.railway.inspection.models.PointTestingResultModel;
import java.util.List;

public class PointTestingResultRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<PointTestingResultModel> list;
    private Context context;
    private View view;
    private OnItemClickListener onItemClickListener;

    public PointTestingResultRecyclerViewAdapter(List<PointTestingResultModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        view=LayoutInflater.from(context).inflate(R.layout.item_point_testing_result,parent,false);
        holder=new PointContentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position<=list.size()){
            PointTestingResultModel pointTestingResultModel=list.get(position);
            PointContentHolder pointContentHolder=(PointContentHolder)holder;
            pointContentHolder.getPointName().setText(pointTestingResultModel.getPointName());
            String pointNum=pointTestingResultModel.getCardNum();
            if(TextUtils.isEmpty(pointNum)||"0".equals(pointNum)){
                pointNum="未发牌";
            }
            String countNum=pointTestingResultModel.getCountNum();
            pointContentHolder.getCardNum().setText(pointNum);
            pointContentHolder.getPointDoneNum().setText(countNum);
            if(!"0".equals(countNum)){
                pointContentHolder.getPointDoneNum().setTextColor(view.getResources().getColor(R.color.greet));
                pointContentHolder.getCardNum().setTextColor(view.getResources().getColor(R.color.greet));
               // pointContentHolder.getPointName().setTextColor(view.getResources().getColor(R.color.greet));
            }else{
                pointContentHolder.getPointDoneNum().setTextColor(view.getResources().getColor(R.color.lightColor));
                pointContentHolder.getCardNum().setTextColor(view.getResources().getColor(R.color.lightColor));
               // pointContentHolder.getPointName().setTextColor(view.getResources().getColor(R.color.lightColor));
            }
            pointContentHolder.itemView.setOnClickListener(new BaseActivity() {
                @Override
                public void onNoDoubleClick(View v) {
                    onItemClickListener.onItemClick(v,position);
                }
            });
            pointContentHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(v,position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
