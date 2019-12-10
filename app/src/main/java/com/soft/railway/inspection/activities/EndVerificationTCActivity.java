package com.soft.railway.inspection.activities;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.LineStationAdapter;
import com.soft.railway.inspection.fragments.IssueDialogFragment;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EndVerificationTCActivity extends BaseActivity  {
    private WorkPojo workPojo;
    private WorkModel workModel;
    private TrainInfoModel trainInfoModel;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private DBUtil dbUtil;
    private boolean isMission=false;
    private String url="/app/save";
    private Uri uri;
    private ImageView recorderIV;
    private Map<String, TrainTypeModel> trainTypeModelMap;
    private static final int WHAT=1;
    private static final int TIPS=2;
    private IssueDialogFragment fragment;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case WHAT:
                    showPhoto.setImageURI(uri);
                    break;
                case TIPS:
                    new AlertDialog.Builder(EndVerificationTCActivity.this).setTitle("操作提示").setMessage("结束添乘检查！")
                            .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1=new Intent("com.soft.railway.inspection.activities.broadcast");
                            sendBroadcast(intent1);
                            finish();
                            RunningWorkActivity.instance.finish();
                        }
                    }).show();
                    break;

            }
        }
    };
    private TextView type;
    private EditText teachET;
    private TextView trainType;
    private TextView trainCc;
    private TextView driverId;
    private TextView fdriverId;
    private ImageView missionIV;
    private TextView jcTV;
    private TextView zzTV;
    private TextView lsTV;
    private TextView editBtn;
    private ListView lineStationLV;
    private List<AreaAndLineModel> areaAndLineModelList;
    private LineStationAdapter lineStationAdapter;
    private Button cancel;
    private Button submit;
    private ImageView showPhoto;
    private ImageView photoBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_end_verification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("确认信息");
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

    private void initData(){
        if(DataUtil.DEBUG){
            url="/app/save";
        }else{
            url="/examine/save";
        }
        httpUtil=HttpUtil.getInstance();
        dataUtil=DataUtil.getInstance();
        dbUtil=DBUtil.getInstance();
        Intent intent=getIntent();
        trainTypeModelMap=dataUtil.getTrainTypeMap();
        workPojo=(WorkPojo)intent.getSerializableExtra("work");
        trainInfoModel =workPojo.getTrainInfo();
        areaAndLineModelList = workPojo.getRouteStationList();
        lineStationAdapter=new LineStationAdapter(areaAndLineModelList,EndVerificationTCActivity.this,null,R.layout.item_line_station_xc);
    }

    private void initView(){
        missionIV=findViewById(R.id.work_end_tc_mission_train_iv);
        if("1".equals(workPojo.getMissionTrain())){
            missionIV.setImageResource(R.drawable.ic_radio_select);
            isMission=true;
        }
        teachET=(EditText)findViewById(R.id.end_verification_tc_teach);
        teachET.setText(workPojo.getTeach());
        type=(TextView) findViewById(R.id.end_verification_type);
        type.setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
        lineStationLV=(ListView) findViewById(R.id.end_verification_tc_list_view);
        lineStationLV.setAdapter(lineStationAdapter);
        trainType         =(TextView) findViewById(R.id.end_verification_train_type);
        trainCc           =(TextView) findViewById(R.id.end_verification_cc);
        cancel            =(Button) findViewById(R.id.end_verification_cancel_btn);
        submit            =(Button)findViewById(R.id.end_verification_submit_btn);
        driverId          =(TextView) findViewById(R.id.end_verification_driver);
        fdriverId         =(TextView)findViewById(R.id.end_verification_fdriver);
        photoBtn          =(ImageView) findViewById(R.id.end_verification_photograph_btn);
        showPhoto         =(ImageView) findViewById(R.id.end_verification_show_photo);
        showPhoto.setOnClickListener(this);
        editBtn=(TextView) findViewById(R.id.end_verification_edit_btn);
        editBtn.setOnClickListener(this);
        jcTV=(TextView)findViewById(R.id.end_verification_jc);
        zzTV=(TextView)findViewById(R.id.end_verification_zz);
        lsTV=(TextView)findViewById(R.id.end_verification_ls);
       if(trainInfoModel !=null){
           trainType   .setText(trainInfoModel.getTrainTypeIdName(dataUtil.getTrainTypeMap()));
           trainCc     .setText(trainInfoModel.getTrainOrder());
           driverId    .setText(trainInfoModel.getDriverName());
           fdriverId   .setText(trainInfoModel.getAssistantDriverName());
           jcTV        .setText(trainInfoModel.getJc());
           zzTV        .setText(trainInfoModel.getZz());
           lsTV        .setText(trainInfoModel.getLs());
       }
        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);
        photoBtn.setOnClickListener(this);

        recorderIV=findViewById(R.id.end_verification_recorder_iv);
        recorderIV.setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.end_verification_cancel_btn:
                Cancel();
                break;
            case R.id.end_verification_photograph_btn:
                PhotoGraph();
                break;
            case R.id.end_verification_submit_btn:
                ShowIssueDialog();
                break;
            case R.id.end_verification_edit_btn:
                goEdit();
                break;
            case R.id.end_verification_recorder_iv:
                goRecorder();
                break;
        }
    }

    public void Cancel(){
        finish();
    }

    public void PhotoGraph(){
        Intent intent=new Intent(EndVerificationTCActivity.this,Camera2Activity.class);
        intent.putExtra(DataUtil.CAMERA_ORIENTATION,0);
        startActivity(intent);
    }

    public void ShowIssueDialog(){
            fragment=IssueDialogFragment.newInstance();
            fragment.show(getSupportFragmentManager(),"issueDialog");
            fragment.setmListener(new IssueDialogFragment.IssueDialogInteractionListener() {
                @Override
                public void SubmitClick(String value) {
                    workPojo.setWorkSuggest(value);
                    Submit();
                }
            });
    }

    public void Submit(){
        fragment.HideIssueDialog();
        final LoadingDialog loadingDialog=LoadingDialog.getInstance(EndVerificationTCActivity.this);
        loadingDialog.show(EndVerificationTCActivity.this);
        final Map map=new HashMap();
        workPojo.setEndTime(DateTimeUtil.getNewDateShow());
        workPojo.setTeach(teachET.getText().toString());
        workPojo.setMissionTrain(missionTrain());
        workPojo.setWorkStatus(DataUtil.WORK_STATUS_UNFINISH+"");
        workModel =new WorkModel(workPojo);
        map.put("data", JSONObject.toJSONString(workModel));
        httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingDialog.dismiss();
                MyException myException=new MyException();
                myException.buildException(e,EndVerificationTCActivity.this);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try{
                        String res= AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject=JSONObject.parseObject(res);
                        String errorCode=jsonObject.getString("errorCode");
                        if("0".equals(errorCode)){
                            MyApplication.instance.end(workPojo);
                            dataUtil.editWorkPojoList(workPojo);
                            dbUtil.delete(DataUtil.TableNameEnum.ITEM.toString()," workid=?",new String[]{workPojo.getWorkId()});
                            dbUtil.delete(DataUtil.TableNameEnum.WORK.toString()," workid=? ",new String[]{workPojo.getWorkId()});
                            if(FileUtil.getInstance().updateFileStatus(workPojo.getWorkId())){
                                List<FileModel> fileModelList=new ArrayList<>();
                                Map<String,FileModel> fileModelMap=new HashMap<>();
                                Cursor fileCursor=dbUtil.select("select * from "+DataUtil.TableNameEnum.SUBMITFILE.toString()+" where filestatus=? group by workid,filetime,filerank ORDER BY filetime,filerank  ",new String[]{FileUtil.FILE_STATUS_WAIT_UPLOADED});
                                if(fileCursor!=null){
                                    while (fileCursor.moveToNext()){
                                        FileModel fileModel=new FileModel(fileCursor);
                                        fileModelList.add(fileModel);
                                        fileModelMap.put(fileModel.getFileId(),fileModel);
                                    }
                                    fileCursor.close();
                                }
                                dataUtil.setFileModelList(fileModelList);
                                dataUtil.setFileModelMap(fileModelMap);
                            }
                            handler.sendEmptyMessage(TIPS);
                        }
                    }catch (Exception e){
                        MyException myException=new MyException();
                        myException.buildException(e,EndVerificationTCActivity.this);
                    }finally {
                        loadingDialog.cancel();
                    }
                }
                loadingDialog.dismiss();
            }
        });
    }

    public void goEdit(){
        Intent intent=new Intent();
        intent.setClass(EndVerificationTCActivity.this,WorkExamineEditTCActivity.class);
        intent.putExtra("work",workPojo);
        startActivityForResult(intent,300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==300&&resultCode==400){
            workPojo=(WorkPojo)data.getSerializableExtra("work");
            workModel=new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(),workModel.getContentValues(workModel)," workid=? ",new String[]{workPojo.getWorkId()});
            dataUtil.editWorkPojoList(workPojo);
            trainInfoModel=workPojo.getTrainInfo();
            refreshViewMyself();
        }else if(requestCode==1901&&requestCode==1900){
            workPojo=(WorkPojo)data.getSerializableExtra("data");
        }
    }

    public void refreshViewMyself(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(trainInfoModel !=null){
                    if(areaAndLineModelList !=null){
                        areaAndLineModelList.clear();
                        areaAndLineModelList.addAll(workPojo.getRouteStationList());
                    }
                    lineStationAdapter.notifyDataSetChanged();
                    trainType   .setText(trainInfoModel.getTrainTypeIdName(trainTypeModelMap));
                    trainCc     .setText(trainInfoModel.getTrainOrder());
                    driverId    .setText(trainInfoModel.getDriverName());
                    fdriverId   .setText(trainInfoModel.getAssistantDriverName());
                    jcTV        .setText(trainInfoModel.getJc());
                    zzTV        .setText(trainInfoModel.getZz());
                    lsTV        .setText(trainInfoModel.getLs());
                    if("1".equals(workPojo.getMissionTrain())){
                        missionIV.setImageResource(R.drawable.ic_radio_select);
                    }else{
                        missionIV.setImageResource(R.drawable.ic_radio);
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String missionTrain(){
        String missionTrain=DataUtil.IS_NOT_MISSION_TRAIN;
        if(isMission){
            missionTrain=DataUtil.IS_MISSION_TRAIN;
        }
        return missionTrain;
    }

    public void goRecorder(){
        Intent intent=new Intent(EndVerificationTCActivity.this,RecordActivity.class);
        intent.putExtra("data",workPojo);
        startActivityForResult(intent,1901);
    }


}