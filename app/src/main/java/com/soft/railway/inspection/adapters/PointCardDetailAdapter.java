package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import java.util.List;

public class PointCardDetailAdapter extends BaseAdapter{
    private List<String> stringList;
    private Context context;

    public PointCardDetailAdapter(List<String> stringList, Context context) {
        this.stringList = stringList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return stringList!=null?stringList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        String show=stringList.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_work_search_common,parent,false);
            holder=new Holder();
            holder.tv=convertView.findViewById(R.id.search_text_view);
            holder.tv.setText(show);
            convertView.setTag(holder);
        }else {
            holder=(Holder) convertView.getTag();
            holder.tv.setText(show);
        }
        return convertView;
    }

    class Holder{
        private TextView tv;
    }
}
