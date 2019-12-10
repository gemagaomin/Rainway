package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PointListAdapter;
import com.soft.railway.inspection.fragments.ShowTcFragment;
import com.soft.railway.inspection.fragments.ShowXCFragment;
import com.soft.railway.inspection.fragments.ShowZXFragment;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.Log;
import java.util.ArrayList;
import java.util.List;

public class WorkDetailFinishActivity extends BaseActivity  {
    private TextView type;
    private TextView time;
    private TextView place;
    private ListView pointListView;
    private PointListAdapter myAdapter;
    private List<WorkItemPojo> list=new ArrayList<WorkItemPojo>();
    private WorkPojo workPojo;
    private TextView teachTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_work_detail_finish);
        Toolbar  mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("工作详情");
        setSupportActionBar(mToolbar);  //将ToolBar设置成ActionBar
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        initView();
        initFragment();
    }

    @Override
    public void onNoDoubleClick(View v) {

    }

    public void initData(){
        Intent intent=getIntent();
        workPojo=(WorkPojo) intent.getSerializableExtra("data");
        list=workPojo.getItems();
    }

    public void initView(){
        teachTV=findViewById(R.id.work_detail_finish_teach);
        teachTV.setText(workPojo.getTeach());
        type=(TextView)findViewById(R.id.work_detail_finish_type);
        type.setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
        time=(TextView)findViewById(R.id.work_detail_finish_show_time);
        time.setText(workPojo.getShowTime());
        place=(TextView)findViewById(R.id.work_detail_finish_place);
        place.setText(workPojo.getPlace());
        pointListView=(ListView) findViewById(R.id.work_detail_finish_list_view);
        myAdapter=new PointListAdapter(this,R.layout.item_work_detail_finsh_point,list);
        pointListView.setAdapter(myAdapter);
        pointListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     WorkItemPojo workItemPojo =list.get(position);
                     Intent intent=new Intent();
                     intent.setClass(WorkDetailFinishActivity.this, PointActivity.class);
                     intent.putExtra("pointData", workItemPojo);
                     intent.putExtra("workType",workPojo.getWorkType());
                     startActivity(intent);
                 }
             }

        );
    }

    public void initFragment(){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        if(DataUtil.WORK_TYPE_XCJC.equals(workPojo.getWorkType())){
            //TODO 没有特殊的信息
        }else if(DataUtil.WORK_TYPE_TC.equals(workPojo.getWorkType())){
            ShowTcFragment fragment=new ShowTcFragment();
            TrainInfoModel trainInfoModel =workPojo.getTrainInfo();
            fragment.setTrainInfoModel(trainInfoModel);
            fragmentTransaction.add(R.id.framlayout,fragment,DataUtil.WORK_TYPE_TC).commit();
        }else if(DataUtil.WORK_TYPE_ZXJC.equals(workPojo.getWorkType())){
            ShowZXFragment fragment=ShowZXFragment.newInstance(workPojo.getWorkExplain(),"");
            fragmentTransaction.add(R.id.framlayout,fragment,DataUtil.WORK_TYPE_ZXJC).commit();
        }else if(DataUtil.WORK_TYPE_SMALL_TC.equals(workPojo.getWorkType())){
            ShowTcFragment fragment=new ShowTcFragment();
            TrainInfoModel trainInfoModel =workPojo.getTrainInfo();
            fragment.setTrainInfoModel(trainInfoModel);
            fragmentTransaction.add(R.id.framlayout,fragment,DataUtil.WORK_TYPE_SMALL_TC).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
