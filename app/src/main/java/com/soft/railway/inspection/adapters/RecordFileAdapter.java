package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.FileModel;

import java.util.List;

public class RecordFileAdapter extends BaseAdapter {
    private List<FileModel> list;
    private Context context;
    private RecorderListener mlistener;

    public RecordFileAdapter(List<FileModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public List<FileModel> getList() {
        return list;
    }

    public void setList(List<FileModel> list) {
        this.list = list;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public RecorderListener getMlistener() {
        return mlistener;
    }

    public void setMlistener(RecorderListener mlistener) {
        this.mlistener = mlistener;
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
        FileModel fileModel=list.get(position);
        Holder holder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_recorder,parent,false);
            holder=new Holder();
            holder.fileNameView=convertView.findViewById(R.id.item_recorder_file_name);
            holder.fileTimeView=convertView.findViewById(R.id.item_recorder_file_time);
            holder.iv=convertView.findViewById(R.id.item_recorder_iv);
            convertView.setTag(holder);
        }else{
            holder=(Holder)convertView.getTag();
        }
        holder.fileNameView.setText(fileModel.getFileName());
        holder.fileTimeView.setText(fileModel.getFileTime());
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.onClick(v,position);
            }
        });
        return convertView;
    }

    class Holder{
        TextView fileNameView;
        TextView fileTimeView;
        ImageView iv;
    }

    public interface RecorderListener{
        void onClick(View v,int position);
    }
}
