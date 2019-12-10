package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.AreaAdapter;
import com.soft.railway.inspection.models.AreaModel;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.MyApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchAreaActivity extends BaseActivity {
    private DataUtil dataUtil;
    private List<AreaModel> list;
    private List<AreaModel> allList;
    private EditText area;
    private ListView areaLV;
    private boolean ifEdit=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            areaAdapter.notifyDataSetChanged();
        }
    };
    private AreaAndLineModel areaAndLineModel;
    private AreaAdapter areaAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_area);
        Toolbar toolbar =(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("选择区域");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        initView();
    }

    @Override
    public void onNoDoubleClick(View v) {

    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        Intent intent=getIntent();
        AreaAndLineModel areaAndLineModelItem =(AreaAndLineModel)intent.getSerializableExtra("areaItem");
        if(areaAndLineModel !=null){
            areaAndLineModel = areaAndLineModelItem;
            ifEdit=true;
        }else{
            areaAndLineModel =new AreaAndLineModel();
        }
        allList=dataUtil.getAreaList();
        list=new ArrayList<>();
        areaAdapter=new AreaAdapter(list,SearchAreaActivity.this);
        refreshView("");
    }

    public void initView(){
        area=(EditText) findViewById(R.id.search_area_auto_text_view);
        area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshView(s.toString());
            }
        });
        areaLV=(ListView) findViewById(R.id.search_area_list_view);
        areaLV.setAdapter(areaAdapter);
        areaLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AreaModel areaModel =list.get(position);
                areaAndLineModel.setAreaId(areaModel.getAreaId());
                areaAndLineModel.setAreaName(areaModel.getAreaName());
                List<Activity> list= MyApplication.list;
                if(list!=null&&list.size()>1){
                    Activity activity=list.get(list.size()-1);
                    // Intent intent=new Intent(SearchAreaActivity.this,WorkExamineEditXCActivity.class);
                    Intent intent=new Intent();
                    intent.setClass(SearchAreaActivity.this,activity.getClass());
                    intent.putExtra("areaItem", areaAndLineModel);
                    if(ifEdit){
                        setResult(301,intent);
                    }else{
                        setResult(1001,intent);
                    }
                    finish();
                }


            }
        });
    }

    public void refreshView(String str){
        list.clear();
        if(TextUtils.isEmpty(str)){
            list.addAll(allList);
        }else{
            int num=allList.size();
            if(num>0){
                for(int i=0;i<num;i++){
                    AreaModel areaModel =allList.get(i);
                    String areaStr= areaModel.getAreaName();
                    if(str.length()<=areaStr.length()&&areaStr.indexOf(str)!=-1){
                        list.add(areaModel);
                    }
                }
            }
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
