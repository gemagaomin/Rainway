package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends BaseActivity {
    private TextView        versionNumber;
    private TextView    updateVersion;
    private LinearLayout    editPassword;
    private Button          logOut;
    private Button          appOut;
    private LinearLayout update;
    private HttpUtil httpUtil;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(SettingActivity.this,"当前为最新版本",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        versionNumber =(TextView)findViewById(R.id.setting_version_code);
        String versionName="";
        try{
            versionName =getPackageManager().getPackageInfo(getPackageName(),0).versionName;
        }catch (PackageManager.NameNotFoundException e){
            MyException myException=new MyException();
            myException.buildException(e);
        }
        versionNumber.setText(versionName);
        editPassword  =(LinearLayout) findViewById(R.id.setting_edit_password);
        editPassword.setOnClickListener(this);
        logOut=(Button) findViewById(R.id.setting_log_out_btn);
        logOut.setOnClickListener(this);
        appOut=(Button) findViewById(R.id.setting_app_out_btn);
        appOut.setOnClickListener(this);
        update=(LinearLayout) findViewById(R.id.setting_edit_version);
        httpUtil=HttpUtil.getInstance();
        update.setOnClickListener(this);
        updateVersion=findViewById(R.id.setting_updata_tv);
        updateVersion.setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.setting_edit_password:
                Intent intent=new Intent(SettingActivity.this,PasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_log_out_btn:
                logOut();
                break;
            case R.id.setting_app_out_btn:
                appOut();
                break;
            case R.id.setting_updata_tv:
                updateVersion();
                break;
        }
    }

    public void logOut(){
        new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage("确定要退出登录吗？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedPreferences=getSharedPreferences(MyApplication.USER_NAME, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("userId","");
                DataUtil.selectSearchTime="";
                editor.commit();
                Intent intent=new Intent(SettingActivity.this,LoginActivity.class);
                startActivity(intent);

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void appOut(){
        new AlertDialog.Builder(SettingActivity.this).setTitle("提示").setMessage("确定要退出程序吗？").setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataUtil.selectSearchTime="";
                MyApplication.finishAll();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public  void updateVersion(){
        httpUtil.asynch("/app/version", httpUtil.TYPE_POST, null, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                MyException myException=new MyException();
                myException.buildException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String version=response.body().string();
                    if(TextUtils.isEmpty(version)){
                        if((getPackageManager().getPackageArchiveInfo(getPackageName(),0).versionCode+"").equals(version)){
                            handler.sendEmptyMessage(1);
                        }else{
                            Intent intent=new Intent(SettingActivity.this,VersionUpdateActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }

}
