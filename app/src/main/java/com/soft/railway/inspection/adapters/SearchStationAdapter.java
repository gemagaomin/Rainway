package com.soft.railway.inspection.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.StationModel;
import java.util.List;

public class SearchStationAdapter extends BaseAdapter {
    List<StationModel> list;
    Context context;

    public SearchStationAdapter(List<StationModel> list, Context context) {
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
        StationModel stationModel=list.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_work_search_common,parent,false);
            holder=new Holder();
            holder.textView=(TextView) convertView.findViewById(R.id.search_text_view);
            holder.textView.setText(stationModel.getStationName());
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
            holder.textView.setText(stationModel.getStationName());
        }
        return convertView;
    }

    class Holder{
        TextView textView;
    }
}
