package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PhotoRecycleAdapter;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.utils.DataUtil;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PointActivity extends BaseActivity {
    private TextView pointTypeTextView;
    private TextView timeTextView;
    private TextView pointSupplement;
    private TextView peoplesTextView;
    private TextView unitTextView;
    private RecyclerView phoneRV;
    private PhotoRecycleAdapter phoneAdapter;
    private DataUtil dataUtil;
    private Map<String, TrainTypeModel> trainTypeModelMap;
    private String workType="";
    private PhotoRecycleAdapter.OnRecycleViewItemClickListener phoneRVICL=new PhotoRecycleAdapter.OnRecycleViewItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            Intent intent=new Intent(PointActivity.this,ShowPhotoVideoActivity.class);
            intent.putExtra("isfinish",true);
            intent.putExtra("data",(Serializable) photoModelList.get(position));
            startActivity(intent);
        }

        @Override
        public void DeleteItemClick(View view, int position) {

        }
    };
    private RecyclerView videoRV;
    private PhotoRecycleAdapter videoAdapter;
    private PhotoRecycleAdapter.OnRecycleViewItemClickListener videoRVICL=new PhotoRecycleAdapter.OnRecycleViewItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            Intent intent=new Intent(PointActivity.this,ShowPhotoVideoActivity.class);
            intent.putExtra("isfinish",true);
            intent.putExtra("data",(Serializable) videoModelList.get(position));
            startActivity(intent);
        }

        @Override
        public void DeleteItemClick(View view, int position) {

        }
    };
    private WorkItemPojo workItemPojo;
    private List<FileModel> videoModelList;
    private List<FileModel> photoModelList;
    private TrainInfoModel trainInfoModel;


    //
    private LinearLayout trainTypeLL;
    private TextView trainTypeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_point);
        Toolbar toolbar=(Toolbar) findViewById(R.id.point_tool);
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
        initPhotoGridView();
        initVideoGridView();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.point_tool:
                finish();
                break;
        }
    }

    public void initData(){
        Intent intent=getIntent();
        workItemPojo =(WorkItemPojo) intent.getSerializableExtra("pointData");
        workType=intent.getStringExtra("workType");
        videoModelList= workItemPojo.getVideos();
        photoModelList= workItemPojo.getPhotos();
        dataUtil=DataUtil.getInstance();
        trainTypeModelMap= dataUtil.getTrainTypeMap();
        trainInfoModel=workItemPojo.getTrainInfoModel();
    }

    public void initView(){
        pointTypeTextView=(TextView) findViewById(R.id.point_detail_type);
        timeTextView     =(TextView) findViewById(R.id.point_detail_add_time);
        pointSupplement  =(TextView) findViewById(R.id.point_detail_supplement);
        peoplesTextView  =(TextView) findViewById(R.id.point_detail_peoples);
        unitTextView     =(TextView) findViewById(R.id.point_detail_unit);
        pointTypeTextView.setText(workItemPojo.getPointContent());
        timeTextView     .setText(workItemPojo.getInsertTime());
        pointSupplement  .setText(workItemPojo.getRemarks());
        peoplesTextView  .setText(workItemPojo.getShowPeoples());
        unitTextView     .setText(workItemPojo.getUnitName());
        trainTypeLL=findViewById(R.id.point_detail_train_info_ll);
        trainTypeTV=findViewById(R.id.point_detail_train_type_tv);
        if(!DataUtil.WORK_TYPE_TC.equals(workType)&&!DataUtil.WORK_TYPE_SMALL_TC.equals(workType)){
            trainTypeLL.setVisibility(View.VISIBLE);
            trainTypeTV.setText(trainInfoModel.getTrainTypeIdName(trainTypeModelMap));
        }
    }
    public void initPhotoGridView(){
        if(photoModelList!=null&&photoModelList.size()>0){
            phoneRV=(RecyclerView) findViewById(R.id.point_detail_phone_rv);
            phoneRV.setHasFixedSize(true);
            phoneRV.setItemAnimator(new DefaultItemAnimator());
            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            phoneRV.setLayoutManager(linearLayoutManager);
            phoneAdapter=new PhotoRecycleAdapter(photoModelList,this,R.layout.item_photo_recycle_layout);
            phoneRV.setAdapter(phoneAdapter);
            phoneAdapter.setOnItemClickListener(phoneRVICL);
        }
    }

    public void initVideoGridView(){
        if(videoModelList!=null&&videoModelList.size()>0){
            videoRV=(RecyclerView) findViewById(R.id.point_detail_video_rv);
            videoRV.setItemAnimator(new DefaultItemAnimator());
            videoRV.setHasFixedSize(true);
            LinearLayoutManager manager=new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            videoRV.setLayoutManager(manager);
            videoAdapter=new PhotoRecycleAdapter(videoModelList,this,R.layout.item_photo_recycle_layout);
            videoRV.setAdapter(videoAdapter);
            videoAdapter.setOnItemClickListener(videoRVICL);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
