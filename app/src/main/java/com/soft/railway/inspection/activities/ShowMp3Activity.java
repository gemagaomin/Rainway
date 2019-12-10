package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PhotoRecycleAdapter;
import com.soft.railway.inspection.utils.MP3Player;

import java.util.Timer;
import java.util.TimerTask;

public class ShowMp3Activity extends BaseActivity {
    private MP3Player mp3Player;
    private String fileName;

    private ProgressBar seekBar;
    private TextView fileNameTV;
    private ImageView iv;
    private Timer timer;
    private TimerTask timerTask;
    private int fileLength;
    private int status=0;
    private static final int PLAY=0;
    private static final int SUSPEND=1;//暂停
    private static final int STOP=2;//停止
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (status){
                case PLAY:
                    iv.setImageResource(R.drawable.ic_recorder_play);
                    break;
                case SUSPEND:
                    iv.setImageResource(R.drawable.ic_recorder_no_play);
                    break;
                case STOP:
                    iv.setImageResource(R.drawable.ic_recorder_no_play);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_mp3);
        initData();
        initView();
    }

    public void initData() {
        Intent intent = getIntent();
        fileName = intent.getStringExtra("data");
        mp3Player = new MP3Player(ShowMp3Activity.this);
        mp3Player.init(fileName);
        fileLength=mp3Player.getLength();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                // 设置当前播放条位置
                int newLength=mp3Player.getPosition();
                if(newLength>=fileLength-100){
                    seekBar.setProgress(fileLength);
                    status=STOP;
                    changeStatus();
                }else if(newLength<fileLength){
                    seekBar.setProgress(newLength);
                }
            }
        };
        timer.schedule(timerTask, 0, 10);
    }

    public void initView() {
        seekBar = findViewById(R.id.item_show_mp3_seekBar);
        seekBar.setMax(mp3Player.getLength());
        fileNameTV=findViewById(R.id.item_show_mp3_file_name);
        fileNameTV.setText(fileName);
        iv=findViewById(R.id.item_show_mp3_iv);
        iv.setOnClickListener(this);
        mp3Player.play();
    }

    public void changeStatus(){
        int newLength=mp3Player.getPosition();
        switch (status){
            case PLAY:
                if(newLength<fileLength){
                    mp3Player.pause();
                    status=SUSPEND;
                }else{
                    status=STOP;
                }
                break;
            case SUSPEND:
                if(newLength<fileLength){
                    mp3Player.play();
                    status=PLAY;
                }else{
                    status=STOP;
                }
                break;
            case STOP:
                stop();
                break;
        }
        handler.sendEmptyMessage(0);
    }


    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.item_show_mp3_iv:
                changeStatus();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        timerTask.cancel();
        timer.cancel();
        mp3Player.stop();
        mp3Player.destroy();
        super.onDestroy();
    }

    public void stop(){
        timerTask.cancel();
        timer.cancel();
        mp3Player.stop();
        finish();
    }

}
