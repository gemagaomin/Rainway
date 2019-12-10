package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.DialogPeopleAdapter;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.utils.MyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DialogPeopleActivity extends BaseActivity implements DialogPeopleAdapter.Listener {
    private ListView listView;
    private List<PersonModel> showList;
    private DialogPeopleAdapter dialogPeopleAdapter;
    private Button cancel;
    private final int WHAT=8;
    private Button confirm;
    private ImageView imageView;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case WHAT:
                    dialogPeopleAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog_people);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("添加人员信息");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        String peoples=intent.getStringExtra("data");
        List peopleList=new ArrayList();
        if(!TextUtils.isEmpty(peoples)){
            peopleList.addAll(JSONArray.parseArray(peoples,PersonModel.class));
        }
        if(peopleList==null||peopleList.size()==0){
            showList=new ArrayList<>();
        }else{
            showList=peopleList;
        }
        listView=(ListView) findViewById(R.id.dialog_people_auto_list_view);
        dialogPeopleAdapter=new DialogPeopleAdapter(this,showList);
        dialogPeopleAdapter.setListener(this);
        listView.setAdapter(dialogPeopleAdapter);
        cancel=(Button) findViewById(R.id.dialog_people_cancel_btn);
        cancel.setOnClickListener(this);
        confirm=(Button) findViewById(R.id.dialog_people_confirm_btn);
        confirm.setOnClickListener(this);
        imageView=(ImageView) findViewById(R.id.dialog_add_driver_btn);
        imageView.setOnClickListener(this);
    }


    @Override
    public void removeOnClick(int index) {
        showList.remove(index);
        handler.sendEmptyMessage(WHAT);
    }
    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.dialog_people_cancel_btn:
                finish();
                break;
            case R.id.dialog_people_confirm_btn:
                Intent intent=new Intent();
                intent.putExtra("data",(Serializable) showList);
                setResult(10,intent);
                finish();
                break;
            case R.id.dialog_add_driver_btn:
                getDriver();
                break;
        }
    }
    public void getDriver(){
        Intent intent=new Intent(DialogPeopleActivity.this,SearchDriverActivity.class);
        startActivityForResult(intent,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){
            if(resultCode==800){
                PersonModel driverModel=(PersonModel)data.getSerializableExtra("driver");
                showList.add(driverModel);
                handler.sendEmptyMessage(WHAT);
            }
        }
    }
}
