package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.PersonModel;
import java.util.List;

public class DriverAdapter extends BaseAdapter {
    List<PersonModel> driverList;
    Context context;

    public DriverAdapter(List<PersonModel> driverList, Context context) {
        this.driverList = driverList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return driverList!=null?driverList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return driverList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       Holder holder;
        PersonModel driverModel=driverList.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_area_line_station,parent,false);
            holder=new Holder();
            holder.area=(TextView) convertView.findViewById(R.id.item_area);
            holder.area.setText(driverModel.getPersonName()+"("+driverModel.getPersonId()+")"+"  "+driverModel.getUnitName());
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            holder.area.setText(driverModel.getPersonName()+"("+driverModel.getPersonId()+")"+"  "+driverModel.getUnitName());
        }
        return convertView;
    }
    class Holder{
        private TextView area;
    }

}
