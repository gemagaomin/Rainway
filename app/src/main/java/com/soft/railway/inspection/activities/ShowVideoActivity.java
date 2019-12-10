package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowVideoActivity extends BaseActivity {
    private Uri uri;
    private VideoView vv;
    private String fileName;
    private String path;
    private String photoPath;
    private MediaController controller;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            vv.setVideoURI(uri);
        }
    };
    private boolean isOk=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        Intent intent=getIntent();
        fileName=intent.getStringExtra("fileName");
        vv=(VideoView) findViewById(R.id.show_video_view);
        path=DataUtil.VIDEO_PATH+File.separator+fileName;
        findViewById(R.id.show_video_btn).setOnClickListener(this);
        findViewById(R.id.show_video_submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.newUri=uri;
                SharedPreferences sharedPreferences=getSharedPreferences("video", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
                photoPath= DataUtil.PHOTO_PATH+File.separator+fileName;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND);
                        String photo=photoPath.substring(0,(photoPath.length()-4))+".jpg";
                        saveBitmapFile(bitmap,photo);
                    }
                });
                editor.putString("fileName",fileName);
                editor.commit();
                Intent intent=new Intent(ShowVideoActivity.this,PointEditActivity.class);
                setResult(13,intent);
                VideoActivity.instance.finish();
                finish();
            }
        });
        if(!TextUtils.isEmpty(path)){
            //TODO 是否控制视频最小长度
            File file=new File(path);
            uri= FileProvider.getUriForFile(this,"com.soft.railway.inspection.fileProvider",file);
            vv.setVideoPath(path);
            vv.setMediaController(controller);
            vv.seekTo(0);
            vv.requestFocus();
            vv.start();
        }
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.show_video_btn:
                goBack();
                break;
        }
    }

    /**
     * 把batmap 转file
     * @param bitmap
     * @param filepath
     */
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public void goBack(){
        FileUtil.deleteFile(path);
        SharedPreferences sharedPreferences=getSharedPreferences("video", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("fileName","");
        editor.commit();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }
}
