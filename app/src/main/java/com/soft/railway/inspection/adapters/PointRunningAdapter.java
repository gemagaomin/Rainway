package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.models.PunishmentLevelModel;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.utils.DataUtil;

import java.util.List;
import java.util.Map;

public class PointRunningAdapter extends BaseAdapter {
    private List<WorkItemPojo> list;
    private Context context;
    private OnClickListener myClickListener;
    private int resourceId=-1;
    private Map<String,PunishmentLevelModel> modelMap;
    public PointRunningAdapter(List<WorkItemPojo> list, Context context, int resourceId, Map<String, PunishmentLevelModel> modelMap) {
        this.list = list;
        this.context = context;
        this.resourceId=resourceId;
        this.modelMap=modelMap;
    }

    public void setMyClickListener(OnClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder;
        final WorkItemPojo workItemPojo =list.get(position);
        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.item_point,parent,false);
            holder=new Holder();
            holder.point=       (TextView) convertView.findViewById(R.id.point_name);
            holder.addTime=     (TextView) convertView.findViewById(R.id.point_add_time);
            holder.supplement=  (TextView) convertView.findViewById(R.id.point_supplement);
            holder.imageView=   (ImageView) convertView.findViewById(R.id.point_delete_btn);
            holder.card=(TextView) convertView.findViewById(R.id.point_card_text_view);
            if(resourceId!=-1){
                holder.imageView.setVisibility(View.GONE);
                holder.card.setVisibility(View.VISIBLE);
            }
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
        }
        holder.point.setText(workItemPojo.getPointContent());
        holder.addTime.setText(workItemPojo.getInsertTime());
        if(resourceId!=-1){
            String cardId= workItemPojo.getCard();
            String cardName="批评教育";
            if(!TextUtils.isEmpty(cardId)){
                cardName=modelMap.get(cardId)!=null?modelMap.get(cardId).getCardName():"批评教育";
            }
            holder.card.setText(cardName);
            holder.card.setOnClickListener(new BaseActivity() {
                @Override
                public void onNoDoubleClick(View v) {
                    myClickListener.delete(position);
                }
            });
        }
        holder.supplement.setText(workItemPojo.getRemarks());
        holder.imageView.setOnClickListener(
                new BaseActivity() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        myClickListener.delete(position);
                    }
                }
        );
        return convertView;
    }

    class Holder {
        TextView point;
        TextView addTime;
        TextView supplement;
        TextView card;
        ImageView imageView;
    }

    public interface OnClickListener{
        void delete(int position);
    }
}
