package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.text.TextUtils;
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

public class LineStationAdapter extends BaseAdapter {
    private List<AreaAndLineModel> lineModelList;
    private Context context;
    private ClickListener clickListener;
    private int id;

    public LineStationAdapter(List<AreaAndLineModel> lineModelList, Context context, ClickListener clickListener, int id) {
        this.lineModelList = lineModelList;
        this.context = context;
        this.clickListener = clickListener;
        this.id=id;
    }

    @Override
    public int getCount() {
        return lineModelList!=null?lineModelList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return lineModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        AreaAndLineModel areaAndLineModel =lineModelList.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(id,parent,false);
            holder=new Holder();
            if(id==R.layout.item_line_station_xc){
                holder.line      =(TextView) convertView.findViewById(R.id.item_line_station_line);
                holder.station   =(TextView) convertView.findViewById(R.id.item_line_station_station);
                holder.delete=(ImageView) convertView.findViewById(R.id.item_line_station_delete);
                if(clickListener==null){
                    holder.delete.setVisibility(View.GONE);
                }else{
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.delete.setOnClickListener(new BaseActivity() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            clickListener.OnClick(position);
                        }
                    });
                }
            }else{
                holder.line      =(TextView) convertView.findViewById(R.id.item_line_station_line_end_show);
                holder.station   =(TextView) convertView.findViewById(R.id.item_line_station_end_show);
            }
            holder.line.setText(areaAndLineModel.getLineName());
            String showStr= areaAndLineModel.getStationName();
            if(!TextUtils.isEmpty(areaAndLineModel.getEndStationId())){
                showStr+=" -> "+ areaAndLineModel.getEndStationName();
            }
            holder.station.setText(showStr);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
            if(id==R.layout.item_line_station_xc){
                if(clickListener==null){
                    holder.delete.setVisibility(View.GONE);
                }else{
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.delete.setOnClickListener(new BaseActivity() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            clickListener.OnClick(position);
                        }
                    });
                }
            }
            holder.line.setText(areaAndLineModel.getLineName());
            String showStr= areaAndLineModel.getStationName();
            if(!TextUtils.isEmpty(areaAndLineModel.getEndStationId())){
                showStr+=" -> "+ areaAndLineModel.getEndStationName();
            }
            holder.station.setText(showStr);
        }
        return convertView;
    }

    class Holder{
        private ImageView delete;
        private TextView line;
        private TextView station;
    }

    public interface ClickListener{
        void OnClick(int index);
    }

}
