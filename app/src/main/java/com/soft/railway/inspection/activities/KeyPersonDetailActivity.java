package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.KeyPersonModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;

public class KeyPersonDetailActivity extends AppCompatActivity {
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView evaluate;
    private KeyPersonModel    keyPersonModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_key_person_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("关键人记录详情");
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

    public void initData(){
        Intent intent=getIntent();
        keyPersonModel=(KeyPersonModel) intent.getSerializableExtra("data");
        if(keyPersonModel==null){
            keyPersonModel=new KeyPersonModel();
        }
    }

    public void initView(){
         one  =findViewById(R.id.key_person_detail_one);
         two  =findViewById(R.id.key_person_detail_two);
         three=findViewById(R.id.key_person_detail_three);
         four =findViewById(R.id.key_person_detail_four);
         five =findViewById(R.id.key_person_detail_five);
         evaluate=findViewById(R.id.key_person_detail_evaluate);
         one  .setText(keyPersonModel.getStepOne());
         two  .setText(keyPersonModel.getStepTwo());
         three.setText(keyPersonModel.getStepThree());
         four .setText(keyPersonModel.getStepFour());
         five .setText(keyPersonModel.getStepFive());
         evaluate.setText(keyPersonModel.getEvaluate());
    }


}
