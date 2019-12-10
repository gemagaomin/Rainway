package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.pojos.PersonPojo;

import java.util.List;

public class KeyPersonAdapter extends BaseAdapter {
    private List<PersonPojo> list;
    private Context context;

    public KeyPersonAdapter(List<PersonPojo> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void select(int position){
        for(int i=0,num=list.size();i<num;i++){
            if(position==i){
                list.get(i).setSelect(true);
            }else{
                list.get(i).setSelect(false);
            }
        }
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
        PersonPojo personPojo=(PersonPojo)getItem(position);
        Holder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_key_person,parent,false);
            holder=new Holder();
            holder.imageButton=convertView.findViewById(R.id.item_key_person_r);
            holder.textView=convertView.findViewById(R.id.item_key_person_tv);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        if(personPojo.isSelect()){
            holder.imageButton.setImageResource(R.drawable.ic_radio_select);
        }else{
            holder.imageButton.setImageResource(R.drawable.ic_radio);
        }
        holder.textView.setText(personPojo.getPersonName()+"   "+personPojo.getUnitName());
        return convertView;
    }

    class Holder{
        ImageView imageButton;
        TextView textView;
    }

}
