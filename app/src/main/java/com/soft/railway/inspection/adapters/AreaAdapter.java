package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.AreaModel;
import java.util.List;


public class AreaAdapter extends BaseAdapter {
    List<AreaModel> areaModelList;
    Context context;
    public AreaAdapter(List<AreaModel> lineModelList, Context context) {
        this.areaModelList = lineModelList;
        this.context = context;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        AreaModel areaAndLineModel=areaModelList.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_area_line_station,parent,false);
            holder=new Holder();
            holder.area=(TextView) convertView.findViewById(R.id.item_area);
            holder.area.setText(areaAndLineModel.getAreaName());
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            holder.area.setText(areaAndLineModel.getAreaName());
        }
        return convertView;
    }

    class Holder{
        private TextView area;
    }
}
