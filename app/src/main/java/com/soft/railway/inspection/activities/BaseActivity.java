package com.soft.railway.inspection.activities;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.soft.railway.inspection.services.StepService;
import com.soft.railway.inspection.services.StepTempService;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.MyApplication;

import java.io.File;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private boolean ifCanOnclick=true;
    private boolean isDestroy=false;
    private long lastOnclickTime=0;
    private static final long INTERVAL_TIME=500;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isDestroy=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy=true;
        MyApplication.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ifCanOnclick=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void hideInput(){
        InputMethodManager imm=(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view=getWindow().peekDecorView();
        if(null!=view){
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public boolean isIfCanOnclick() {
        return ifCanOnclick;
    }

    public void lockIfCanOnclick() {
        this.ifCanOnclick = false;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if(isIfCanOnclick()){
            lockIfCanOnclick();
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        long currentTime=System.currentTimeMillis();
        if((currentTime-lastOnclickTime>=INTERVAL_TIME)&&isIfCanOnclick()){
            lastOnclickTime=currentTime;
            onNoDoubleClick(v);
        }
    }
    public abstract void onNoDoubleClick(View v);

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
