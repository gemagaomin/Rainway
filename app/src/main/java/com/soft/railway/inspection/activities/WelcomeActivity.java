package com.soft.railway.inspection.activities;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.RequestPowerUtil;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class WelcomeActivity extends BaseActivity  {
    private DataUtil dataUtil;
    private DBUtil dbUtil;
    private final static int SEND_SMS_REQUEST_CODE = 107;
    private TextView versionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        versionCode=findViewById(R.id.welcome_version_cord);
        versionCode.setText(MyApplication.getAppVersionName(this));
        RequestPowerUtil requestPowerUtil=RequestPowerUtil.getInstance();
        if(requestPowerUtil.requestAllPower(this,this)){
            getData();
        }
    }

    @Override
    public void onNoDoubleClick(View v) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        doNext(requestCode,grantResults);
    }

    public void doNext(int requestCode, int[] grantResults) {
        if (requestCode == SEND_SMS_REQUEST_CODE) {
            for(int i=0,num=grantResults.length;i<num;i++){
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this,"未获得授权，本软件无法正常运行！",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
            }
            getData();
        }
    }
    public void getData(){
        dbUtil=DBUtil.getInstance();
        if(DataUtil.IS_UPDATA_DATABASE){
            dbUtil.updateDatabase(DataUtil.TableNameEnum.WORK.toString());
        }
        dataUtil = DataUtil.getInstance();
        SharedPreferences sharedPreferences=getSharedPreferences(MyApplication.USER_NAME,Activity.MODE_PRIVATE);
        SharedPreferences versionPreferences=getSharedPreferences(MyApplication.DATA_VERSION,Activity.MODE_PRIVATE);
        SharedPreferences ipPreferences=getSharedPreferences(MyApplication.DATA_IP, Activity.MODE_PRIVATE);
        DataUtil.setVersionMap(versionPreferences);
        String ip=ipPreferences.getString("ip","");
        if(!TextUtils.isEmpty(ip)){
            HttpUtil.BASE_PATH_OTHER=ip;
        }
        String userId=sharedPreferences.getString("userId","");
        if(!TextUtils.isEmpty(userId)){
            String userName=sharedPreferences.getString("userName","");
            String unitId=sharedPreferences.getString("unitId","");
            String unitName=sharedPreferences.getString("unitName","");
            String password=sharedPreferences.getString("password","");
            String pointIds=sharedPreferences.getString("pointIds","");
            UserModel userModel=new UserModel();
            userModel.setUserId(userId);
            userModel.setUserName(userName);
            userModel.setUnitId(unitId);
            userModel.setPassword(password);
            userModel.setUnitName(unitName);
            userModel.setPointIds(pointIds);
            dataUtil.setUser(userModel);
        }
        Intent intent1=new Intent(WelcomeActivity.this,LoginActivity.class);
        startActivity(intent1);
        finish();
    }
}
