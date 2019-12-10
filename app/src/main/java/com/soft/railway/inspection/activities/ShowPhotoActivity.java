package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.PhotoObserverableUtil;
import java.io.File;

public class ShowPhotoActivity extends BaseActivity {
    private Uri uri;
    private ImageView iv;
    private String data="";
    private boolean orientationCamera;
    private PhotoObserverableUtil photoObserverableUtil;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                iv.setImageURI(uri);
            }

        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_photo);
        Intent intent=getIntent();
        data=intent.getStringExtra("fileName");
        orientationCamera=intent.getBooleanExtra("from",false);
        iv=(ImageView) findViewById(R.id.show_photo_image_view_only);
        if(!TextUtils.isEmpty(data)){
            File file=new File(DataUtil.PHOTO_PATH+File.separator+data);
            uri= FileProvider.getUriForFile(this,"com.soft.railway.inspection.fileProvider",file);
            handler.sendEmptyMessage(0);
        }
        findViewById(R.id.show_photo_btn).setOnClickListener(this);
        findViewById(R.id.show_submit_btn).setOnClickListener(this);
        photoObserverableUtil=PhotoObserverableUtil.getInstance();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        String name="photo";
        if(orientationCamera){
            name="selfphoto";
        }
        SharedPreferences sharedPreferences=getSharedPreferences(name, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        switch (id){
            case R.id.show_photo_btn:
                FileUtil.deleteFile(uri.getEncodedPath());
                editor.putString("fileName","");
                editor.commit();
                finish();
                break;
            case R.id.show_submit_btn:
                DataUtil.newUri=uri;
                Intent intent=new Intent();
                editor.putString("fileName",data);
                editor.commit();
                photoObserverableUtil.notifyObserver();
                if(orientationCamera){
                    intent=new Intent(ShowPhotoActivity.this,RunningWorkActivity.class);
                }else{
                    intent=new Intent(ShowPhotoActivity.this,PointEditActivity.class);
                }
                setResult(14,intent);
                Camera2Activity.instance.finish();
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FileUtil.deleteFile(uri.getEncodedPath());
        String name="photo";
        if(orientationCamera){
            name="selfphoto";
        }
        SharedPreferences sharedPreferences=getSharedPreferences(name, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("fileName","");
        editor.commit();
        finish();
    }


}
