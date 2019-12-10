package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.models.AreaAndLineModel;

import java.util.List;

public class AreaAndLineAdapter extends BaseAdapter {
    private List<AreaAndLineModel> list;
    private Context context;
    private ClickListener clickListener;
    private int id;
    public AreaAndLineAdapter(List<AreaAndLineModel> list, Context context, ClickListener clickListener, int id) {
        this.list = list;
        this.context = context;
        this.clickListener = clickListener;
        this.id=id;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        AreaAndLineModel areaModel=list.get(position);
        if(convertView==null){
            holder=new Holder();
            convertView= LayoutInflater.from(context).inflate(id,parent,false);

            if(R.layout.item_area_xc==id){
                holder.textView=(TextView) convertView.findViewById(R.id.item_area_xc_area);
                holder.imageView=(ImageView) convertView.findViewById(R.id.item_area_xc_delete);
                holder.textView.setText(areaModel.getAreaName());
                if(clickListener==null){
                    holder.imageView.setVisibility(View.GONE);
                }else{
                    holder.imageView.setVisibility(View.VISIBLE);
                    holder.imageView.setOnClickListener(new BaseActivity() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            clickListener.OnClick(position);
                        }
                    });
                }
            }else{
                holder.textView=(TextView)convertView.findViewById(R.id.item_area_xc_area_end_show);
                holder.textView.setText(areaModel.getAreaName());
            }
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            holder.textView.setText(areaModel.getAreaName());
            if(R.layout.item_area_xc==id){
                holder.imageView.setOnClickListener(new BaseActivity() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        clickListener.OnClick(position);
                    }
                });
            }
        }
        return convertView;
    }

    class Holder{
        TextView textView;
        ImageView imageView;
    }
    public interface ClickListener{
        void OnClick(int index);
    }
}
