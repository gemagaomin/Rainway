package com.soft.railway.inspection.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.utils.DataUtil;
import java.util.ArrayList;
import java.util.List;

public class PointListAdapter extends BaseAdapter {
    private List<WorkItemPojo> list;
    private int myResource;
    private LayoutInflater inflater;
    public PointListAdapter(Context context, int resource, List<WorkItemPojo> list) {
        this.list = list;
        this.inflater=LayoutInflater.from(context);
        this.myResource=resource;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public WorkItemPojo getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent) {
        Holder holder;
        final WorkItemPojo pointPojo =list.get(position);
        if(DataUtil.punishmentLevelList==null||DataUtil.punishmentLevelList.size()==0){
            DataUtil.getPunishmentLevelList();
        }
        String card= DataUtil.punishmentLevelModelMap!=null?DataUtil.punishmentLevelModelMap.get(pointPojo.getCard()).getCardName():"";
        if(convertView==null){
            convertView= inflater.inflate(myResource,null);
            holder=new Holder();
            holder.type=(TextView) convertView.findViewById(R.id.item_work_detail_final_point_type);
            holder.time=(TextView)convertView.findViewById(R.id.item_work_detail_final_point_time);
            holder.remark=(TextView)convertView.findViewById(R.id.item_work_detail_final_point_supplement);
            holder.card=(TextView)convertView.findViewById(R.id.item_work_detail_final_point_card);
            holder.imageView=(ImageView) convertView.findViewById(R.id.item_work_detail_final_point_btn);
            holder.photo=(ImageView)convertView.findViewById(R.id.item_work_detail_has_photo);
            holder.video=(ImageView)convertView.findViewById(R.id.item_work_detail_has_video);
            holder.type.setText(pointPojo.getPointContent());
            holder.time.setText(pointPojo.getInsertTime());
            holder.remark.setText(pointPojo.getRemarks());
            holder.card.setText(card);
            if(pointPojo.getPhotos()!=null&&pointPojo.getPhotos().size()>0){
                holder.photo.setVisibility(View.VISIBLE);
            }
            if(pointPojo.getVideos()!=null&&pointPojo.getVideos().size()>0){
                holder.video.setVisibility(View.VISIBLE);
            }
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
            holder.type.setText(pointPojo.getPointContent());
            holder.time.setText(pointPojo.getInsertTime());
            holder.remark.setText(pointPojo.getRemarks());
            holder.card.setText(card);
            if(pointPojo.getPhotos()!=null&&pointPojo.getPhotos().size()>0){
                holder.photo.setVisibility(View.VISIBLE);
            }
            if(pointPojo.getVideos()!=null&&pointPojo.getVideos().size()>0){
                holder.video.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    static class Holder {
        TextView type;
        TextView time;
        TextView remark;
        TextView card;
        ImageView imageView;
        ImageView photo;
        ImageView video;
    }
}
