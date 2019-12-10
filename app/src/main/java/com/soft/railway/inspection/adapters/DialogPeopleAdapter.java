package com.soft.railway.inspection.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.models.PersonModel;
import java.util.List;

public class DialogPeopleAdapter extends BaseAdapter {
    private Context myContext;
    private List<PersonModel> list;
    private Listener listener;
    public DialogPeopleAdapter(Context context, List<PersonModel> showList) {
        this.myContext=context;
        this.list=showList;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final PersonModel personModel=list.get(position);
        if(convertView==null){
            convertView= LayoutInflater.from(myContext).inflate(R.layout.item_dialog_people,parent,false);
            holder=new Holder();
            holder.name=(TextView) convertView.findViewById(R.id.item_dialog_people_name);
            holder.addBtn=(ImageView) convertView.findViewById(R.id.item_dialog_people_btn);
            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.removeOnClick(position);
                }
            });
            String nameT=personModel.getPersonName();
            if(!TextUtils.isEmpty(nameT)){
                holder.name.setText(nameT);
            }else{
                holder.name.setText("测试");
            }
            holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    AutoCompleteTextView view=(AutoCompleteTextView) v;
                    if(hasFocus){
                        view.showDropDown();
                    }else{
                        holder.addBtn.setImageResource(R.drawable.ic_delete);
                        holder.addBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.removeOnClick(position);
                            }
                        });
                    }
                }
            });
            convertView.setTag(holder);
        }else{
            holder=(Holder) convertView.getTag();
            holder.name.setText(personModel.getPersonName());
            holder.addBtn=(ImageView) convertView.findViewById(R.id.item_dialog_people_btn);
            holder.addBtn.setOnClickListener(new BaseActivity() {
                @Override
                public void onNoDoubleClick(View v) {
                    listener.removeOnClick(position);
                }
            });
            String nameT=personModel.getPersonName();
            if(!TextUtils.isEmpty(nameT)){
                holder.name.setText(nameT);
            }
            holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    AutoCompleteTextView view=(AutoCompleteTextView) v;
                    if(hasFocus){
                        view.showDropDown();
                    }else{
                        holder.addBtn.setOnClickListener(new BaseActivity() {
                            @Override
                            public void onNoDoubleClick(View v) {
                                listener.removeOnClick(position);
                            }
                        });
                    }
                }
            });
            convertView.setTag(holder);
        }
        return convertView;
    }

    class Holder{
        TextView name;
        ImageView addBtn;
    }

    public interface Listener{
        void removeOnClick(int index );
    }
}
