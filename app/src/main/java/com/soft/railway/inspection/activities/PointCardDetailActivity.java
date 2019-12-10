package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PointCardDetailAdapter;
import com.soft.railway.inspection.models.PointTestingResultModel;
import java.util.ArrayList;
import java.util.List;

public class PointCardDetailActivity extends BaseActivity {
    private TextView seed;
    private TextView yellow;
    private TextView white;
    private TextView red;
    private ListView lv;
    private List<String> list;
    private PointCardDetailAdapter pointCardDetailAdapter;
    private PointTestingResultModel pointTestingResultModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_card_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.pointcarddetailtoolbar);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        Intent intent=getIntent();
        pointTestingResultModel=(PointTestingResultModel) intent.getSerializableExtra("data");
        list=new ArrayList<>();
        if(pointTestingResultModel!=null&&pointTestingResultModel.getItemPoints()!=null){
            if(pointTestingResultModel.getItemPoints().size()>0){
                list=pointTestingResultModel.getItemPoints();
            }
        }
        pointCardDetailAdapter=new PointCardDetailAdapter(list,this);
    }

    public void initView(){
       seed   =findViewById(R.id.point_card_detail_seed);
       yellow =findViewById(R.id.point_card_detail_yellow);
       red    =findViewById(R.id.point_card_detail_red);
       white  =findViewById(R.id.point_card_detail_white);
       seed   .setText(pointTestingResultModel.getSeed());
       yellow .setText(pointTestingResultModel.getYellowCard());
       red    .setText(pointTestingResultModel.getRedCard());
       white  .setText(pointTestingResultModel.getWhiteCard());
       lv =findViewById(R.id.point_card_detail_lv);
       lv.setAdapter(pointCardDetailAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
