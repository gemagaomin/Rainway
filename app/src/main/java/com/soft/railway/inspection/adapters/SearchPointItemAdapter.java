package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.pojos.PointItemPojo;

import java.util.List;

public class SearchPointItemAdapter extends BaseAdapter {
    private List<PointItemPojo> list;
    private Context context;

    public SearchPointItemAdapter(List<PointItemPojo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        PointItemPojo pointItemPojo=list.get(position);
        if(convertView==null){
            holder=new Holder();
            convertView= LayoutInflater.from(context).inflate(R.layout.item_search_pointitem,parent,false);
            holder.imageView=convertView.findViewById(R.id.item_search_pointitem_iv);
            holder.pointNameTV=convertView.findViewById(R.id.item_search_pointitem_tv);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        holder.pointNameTV.setText(pointItemPojo.getItemTypeName());
        boolean select=pointItemPojo.isSelect();
        int id=R.drawable.ic_more_select_frame;
        if(select){
            id=R.drawable.ic_more_selected;
        }
        holder.imageView.setImageResource(id);
        return convertView;
    }

    class Holder{
        private TextView pointNameTV;
        private ImageView imageView;
    }
}
