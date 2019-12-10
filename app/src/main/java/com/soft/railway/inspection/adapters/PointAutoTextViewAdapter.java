package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.PointItemModel;
import java.util.ArrayList;
import java.util.List;


public class PointAutoTextViewAdapter extends BaseAdapter implements Filterable {
    List<PointItemModel> list;
    Context context;
    List<PointItemModel> tempList=new ArrayList<>();

    public PointAutoTextViewAdapter(List<PointItemModel> list, Context context) {
        this.list = list;
        this.context = context;
        this.tempList.addAll(list);
    }

    @Override
    public int getCount() {
        return list.isEmpty()?0:list.size();
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
        PointItemModel point=list.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_work_search_common,parent,false);
            holder=new Holder();
            holder.textView=(TextView) convertView.findViewById(R.id.search_text_view);

            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        if(TextUtils.isEmpty(point.getItemNumber())){
            holder.textView.setText(point.getItemTypeName());
        }else{
            holder.textView.setText(point.getItemNumber()+"."+point.getItemTypeName());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                List<PointItemModel> dataList=new ArrayList<>();
                if(TextUtils.isEmpty(constraint)){
                    dataList.addAll(tempList);
                }else{
                    for (PointItemModel o:tempList
                         ) {
                        String name=o.getItemTypeName();
                        int num=constraint.length();
                        if(name.length()>=num&&constraint.equals(o.getItemTypeName().substring(0,num))){
                            dataList.add(o);
                        }
                    }
                }
                results.count=dataList.size();
                results.values=dataList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                list.addAll((ArrayList)results.values);
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    class Holder{
        TextView textView;
    }
}
