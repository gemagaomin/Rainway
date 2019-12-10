package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.PunishmentLevelModel;
import java.util.List;

public class PunishmentLevelAdapter extends BaseAdapter {
    List<PunishmentLevelModel> list;
    Context context;

    public PunishmentLevelAdapter(Context context, List<PunishmentLevelModel> list) {
        this.list = list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
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
        PunishmentLevelModel punishmentLevelModel=list.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_punishment_level,parent,false);
            holder=new Holder();
            holder.textView=(TextView) convertView.findViewById(R.id.item_punoshment_level_text_view);
            holder.textView.setText(punishmentLevelModel.getCardName());
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            holder.textView.setText(punishmentLevelModel.getCardName());
        }
        return convertView;
    }

    class Holder{
        TextView textView;
    }

}
