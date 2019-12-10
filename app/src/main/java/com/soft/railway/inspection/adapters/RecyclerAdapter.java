package com.soft.railway.inspection.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.holders.RecyclerContentHolder;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DataUtil;
import java.util.ArrayList;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<WorkPojo> list;
    private OnItemClickListener onItemClickListener;
    public RecyclerAdapter(List list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder holder;
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_work_content_layout,viewGroup,false);
        holder= new RecyclerContentHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,final int i) {
            if(i<=list.size()){
                final WorkPojo workPojo=list.get(i);
                RecyclerContentHolder holder=(RecyclerContentHolder)viewHolder;
                holder.getTypeTextView().setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
                holder.getTimeTextView().setText(workPojo.getShowTime());
                holder.getPlaceTextView().setText(workPojo.getPlace());
                int statusInt=Integer.parseInt(workPojo.getWorkStatus());
                switch (statusInt){
                    case DataUtil.WORK_STATUS_UNFINISH://完成检查没有发牌
                        holder.getStatusImageView().setImageResource(R.drawable.ic_work_status_unfinish);
                        break;
                    case DataUtil.WORK_STATUS_FINISH://已完成
                        holder.getStatusImageView().setImageResource(R.drawable.ic_finished);
                        break;
                    case DataUtil.WORK_STATUS_RUNNING://正在进行中
                        holder.getStatusImageView().setImageResource(R.drawable.ic_work_status_running);
                        break;
                    case DataUtil.WORK_STATUS_UNSTART://还未开始
                        holder.getStatusImageView().setImageResource(R.drawable.ic_work_status_unstart);
                        break;
                }
                holder.itemView.setOnClickListener(new BaseActivity() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        onItemClickListener.onItemClick(v,i);
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onItemLongClick(v,i);
                        return true;
                    }
                });
            }

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public void setOnItemClickListener(RecyclerAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 自定义监听回调，RecyclerView 的 单击和长按事件
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
}
