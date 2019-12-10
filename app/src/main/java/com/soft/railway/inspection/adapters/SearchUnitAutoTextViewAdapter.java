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
import com.soft.railway.inspection.models.UnitModel;
import java.util.ArrayList;
import java.util.List;

public class SearchUnitAutoTextViewAdapter extends BaseAdapter implements Filterable {
    List<UnitModel> list;
    Context context;
    List<UnitModel> tempList=new ArrayList<>();

    public SearchUnitAutoTextViewAdapter(List<UnitModel> list, Context context) {
        this.list = list;
        this.context = context;
        tempList.addAll(list);
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
        SearchUnitAutoTextViewAdapter.Holder holder;
        UnitModel unitModel=list.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_work_search_common,parent,false);
            holder=new SearchUnitAutoTextViewAdapter.Holder();
            holder.textView=(TextView) convertView.findViewById(R.id.search_text_view);
            holder.textView.setText(unitModel.getgName());
            convertView.setTag(holder);
        }else{
            holder=(SearchUnitAutoTextViewAdapter.Holder) convertView.getTag();
            holder.textView.setText(unitModel.getgName());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results=new FilterResults();
                List<UnitModel> dataList=new ArrayList<>();
                if(TextUtils.isEmpty(constraint)){
                    dataList.addAll(tempList);
                }else{
                    for (UnitModel o:tempList
                    ) {
                        String name=o.getgName();
                        int num=constraint.length();
                        if(name.length()>=num&&constraint.equals(o.getgName().substring(0,num))){
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
                list=(ArrayList)results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    class Holder{
        TextView textView;
    }
}
