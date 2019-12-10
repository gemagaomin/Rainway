package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.AreaAndLineModel;

import java.util.List;

public class AreaEditAdapter extends BaseAdapter {
    List<AreaAndLineModel> areaModelList;
    Context context;
    private ClickListener clickListener;

    public AreaEditAdapter(List<AreaAndLineModel> areaModelList, Context context) {
        this.areaModelList = areaModelList;
        this.context = context;
    }

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getCount() {
        return areaModelList!=null?areaModelList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return areaModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        AreaAndLineModel areaAndLineModel=areaModelList.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_area_edit,parent,false);
            holder=new Holder();
            holder.area=(TextView) convertView.findViewById(R.id.item_area_delete_tv);

            holder.imageView=(ImageView)convertView.findViewById(R.id.item_area_delete_btn) ;
            holder.area.setText(areaAndLineModel.getAreaName());
            if(clickListener!=null){
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.OnClick(position);
                    }
                });
            }
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            holder.area.setText(areaAndLineModel.getAreaName());
            if(clickListener!=null){
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.OnClick(position);
                    }
                });
            }
        }
        return convertView;
    }

    class Holder{
        private TextView area;
        private ImageView imageView;
    }
    public interface ClickListener{
        void OnClick(int index);
    }
}
