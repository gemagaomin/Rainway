package com.soft.railway.inspection.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.AreaAndLineAdapter;
import com.soft.railway.inspection.adapters.LineStationAdapter;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.LoadingDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkExamineEditZXActivity extends BaseActivity {
    private static final int AREA_WHAT=0;
    private static final int LINE_WHAT=1;
    private int selectIndex=0;
    private WorkPojo workPojo;
    private Button cancel;
    private Button submit;
    private DBUtil dbUtil;
    private ImageView addArea;
    private ImageView addLineStation;
    private ListView lineStationLV;
    private List<AreaAndLineModel> lineAndStationList;
    private LineStationAdapter lineAndStationAdapter;
    private ListView areaLV;
    private List<AreaAndLineModel> areaList;
    private AreaAndLineAdapter areaAdapter;
    private DataUtil dataUtil;
    private EditText editText;
    private Button editCancel;
    private Button editSubmit;
    private LinearLayout addBtnLinearLayout;
    private LinearLayout editBtnLinearLayout;
    private Map<String,String> paramsMap=new HashMap<>();
    private AreaAndLineAdapter.ClickListener areaListener=new AreaAndLineAdapter.ClickListener() {
        @Override
        public void OnClick(int index) {
            areaList.remove(index);
            handler.sendEmptyMessage(AREA_WHAT);
        }
    };
    private boolean isEdit=false;
    private LineStationAdapter.ClickListener clickListener=new LineStationAdapter.ClickListener() {
        @Override
        public void OnClick(int index) {
            lineAndStationList.remove(index);
            handler.sendEmptyMessage(LINE_WHAT);
        }
    };
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            switch (what){
                case AREA_WHAT:
                    areaAdapter.notifyDataSetChanged();
                    break;
                case LINE_WHAT:
                    lineAndStationAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_work_examine_edit_zx);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        dataUtil=DataUtil.getInstance();
        workPojo=(WorkPojo) intent.getSerializableExtra("work");
        if(workPojo!=null){
            toolbar.setTitle("编辑专项检查");
            isEdit=true;
        }else{
            toolbar.setTitle("添加专项检查");
            workPojo=new WorkPojo(dataUtil.getUser());
        }
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
        initEdit();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.work_zx_submit_btn:
                submit();
                break;
            case R.id.work_examine_edit_zx_add_area:
                Intent intent=new Intent(WorkExamineEditZXActivity.this,SearchAreaActivity.class);
                startActivityForResult(intent,300);
                break;
            case R.id.work__zx_cancel_btn:
                finish();
                break;
            case R.id.work_examine_edit_zx_add_line_station:
                Intent intent1=new Intent();
                intent1.setClass(WorkExamineEditZXActivity.this,SearchLineStationActivity.class);
                intent1.putExtra("xc","xc");
                intent1.putExtra("params",(Serializable)paramsMap);
                startActivityForResult(intent1,200);
                break;
            case R.id.work_zx_edit_cancel_btn:
                EditCancel();
                break;
            case R.id.work_zx_edit_submit_btn:
                EditSubmit();
                break;
        }
    }
    private void initData(){
        lineAndStationList=new ArrayList<>();
        if(workPojo.getRouteStationList()!=null&&workPojo.getRouteStationList().size()>0){
            lineAndStationList.addAll(workPojo.getRouteStationList());
        }
        areaList=new ArrayList<>();
        if(workPojo.getAreaList()!=null&&workPojo.getAreaList().size()>0){
            areaList.addAll(workPojo.getAreaList());
        }
    }
    private void initEdit(){
        if(isEdit){
            editText.setText(workPojo.getWorkExplain());
            editBtnLinearLayout=(LinearLayout) findViewById(R.id.work_zx_edit_btn_layout);
            addBtnLinearLayout=(LinearLayout) findViewById(R.id.work_zx_btn_layout);
            editBtnLinearLayout.setVisibility(View.VISIBLE);
            addBtnLinearLayout.setVisibility(View.GONE);
            editCancel=(Button) findViewById(R.id.work_zx_edit_cancel_btn);
            editCancel.setOnClickListener(this);
            editSubmit=(Button) findViewById(R.id.work_zx_edit_submit_btn);
            editSubmit.setOnClickListener(this);
        }
    }

    private void initView(){
        editText=(EditText)findViewById(R.id.work_examine_edit_zx_explain);
        lineStationLV=(ListView) findViewById(R.id.work_examine_edit_zx_line_list);
        lineAndStationAdapter=new LineStationAdapter(lineAndStationList,WorkExamineEditZXActivity.this,clickListener,R.layout.item_line_station_xc);
        lineStationLV.setAdapter(lineAndStationAdapter);
        areaLV=(ListView) findViewById(R.id.work_examine_edit_zx_area_list);
        areaAdapter=new AreaAndLineAdapter(areaList, WorkExamineEditZXActivity.this,areaListener ,R.layout.item_area_xc);
        areaLV.setAdapter(areaAdapter);
        cancel=(Button) findViewById(R.id.work__zx_cancel_btn);
        cancel.setOnClickListener(this);
        addArea=(ImageView) findViewById(R.id.work_examine_edit_zx_add_area);
        addArea.setOnClickListener(this);
        addLineStation=(ImageView) findViewById(R.id.work_examine_edit_zx_add_line_station);
        addLineStation.setOnClickListener(this);
        submit=(Button) findViewById(R.id.work_zx_submit_btn);
        submit.setOnClickListener(this);
    }
    private boolean Validate(){
        boolean res=true;
        if(areaList.size()==0){
            res=false;
        }
        return res;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1000){
            paramsMap=(Map) data.getSerializableExtra("params");
            String lines=(String)paramsMap.get("lineName");
            String startStation=(String)paramsMap.get("startStationName");
            String lineId=(String)paramsMap.get("lineId");
            String startStationId=(String)paramsMap.get("startStationId");
            AreaAndLineModel areaAndLineModel =new AreaAndLineModel();
            areaAndLineModel.setLineId(lineId);
            areaAndLineModel.setLineName(lines);
            areaAndLineModel.setStationId(startStationId);
            areaAndLineModel.setStationName(startStation);
            if(requestCode==200){
                lineAndStationList.add(areaAndLineModel);
            }else if(requestCode==201){
                lineAndStationList.set(selectIndex, areaAndLineModel);
            }
            handler.sendEmptyMessage(LINE_WHAT);
        }else if(resultCode==1001){
            AreaAndLineModel areaAndLineModel =(AreaAndLineModel)data.getSerializableExtra("areaItem");
            if(requestCode==300){
                areaList.add(areaAndLineModel);
            }else if(requestCode==301){
                areaList.set(selectIndex, areaAndLineModel);
            }
            handler.sendEmptyMessage(AREA_WHAT);
        }
    }

    private void EditCancel(){
        Intent intent=new Intent();
        intent.putExtra("data",workPojo);
        setResult(601,intent);
        finish();
    }

    private void EditSubmit(){
        String place="";
        String tx="; ";
        if(areaList!=null&&areaList.size()>0){
            for(int i=0,num=areaList.size();i<num;i++){
                place+=areaList.get(i).getAreaName()+tx;
            }
        }
        if(lineAndStationList!=null&&lineAndStationList.size()>0){
            for(int i=0,num=lineAndStationList.size();i<num;i++){
                AreaAndLineModel areaAndLineModel =lineAndStationList.get(i);
                place+= areaAndLineModel.getLineName()+":"+ areaAndLineModel.getStationName()+tx;
            }
        }
        workPojo.setAreaList(areaList);
        workPojo.setRouteStationList(lineAndStationList);
        workPojo.setPlace(place);
        Intent intent=new Intent();
        intent.putExtra("data",workPojo);
        setResult(600,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void submit(){
        if(Validate()){
            final LoadingDialog loadingDialog=LoadingDialog.getInstance(WorkExamineEditZXActivity.this);
            loadingDialog.show();
            UserModel userModel=dataUtil.getUser();
            workPojo.setUserId(userModel.getUserId());
            String time= DateTimeUtil.getNewDateShow();
            workPojo.setWorkType(DataUtil.WORK_TYPE_ZXJC);
            workPojo.setWorkStatus(DataUtil.WORK_STATUS_UNSTART+"");
            workPojo.setStartTime(time);
            workPojo.setWorkExplain(editText.getText().toString());
            workPojo.setWorkId(DataUtil.getOnlyId());
            String place="";
            String tx="; ";
            if(areaList!=null&&areaList.size()>0){
                for(int i=0,num=areaList.size();i<num;i++){
                    place+=areaList.get(i).getAreaName()+tx;
                }
            }
            if(lineAndStationList!=null&&lineAndStationList.size()>0){
                for(int i=0,num=lineAndStationList.size();i<num;i++){
                    AreaAndLineModel areaAndLineModel =lineAndStationList.get(i);
                    place+= areaAndLineModel.getLineName()+":"+ areaAndLineModel.getStationName()+tx;
                }
            }
            workPojo.setAreaList(areaList);
            workPojo.setRouteStationList(lineAndStationList);
            workPojo.setPlace(place);
            workPojo.setUserId(userModel.getUserId());
            workPojo.setUnitId(userModel.getUnitId());
            workPojo.setShowTime(DateTimeUtil.getNewDate(time));
            dbUtil=DBUtil.getInstance();
            WorkModel workModel=new WorkModel(workPojo);
            dbUtil.insert(DataUtil.TableNameEnum.WORK.toString(),workModel.getContentValues(workModel));
            dataUtil.addWorkPojoList(workPojo);
            loadingDialog.cancel();
            new AlertDialog.Builder(WorkExamineEditZXActivity.this).setTitle("操作提示").setMessage("工作创建成功！")
                    .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        }else{
            Toast.makeText(WorkExamineEditZXActivity.this,"区域或线路至少选择一个",Toast.LENGTH_LONG).show();
        }
    }


}
