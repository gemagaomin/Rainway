package com.soft.railway.inspection.utils;


import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.IOException;

public class MP3Player {

    private MediaPlayer mp;
    private String path;
    private int length;


    public MP3Player(Context ctx) {
        mp = new MediaPlayer();
        // 获取内部存储器绝对路径
        path =DataUtil.RECORDER_PATH;
        //Toast.makeText(ctx, path, Toast.LENGTH_LONG).show();
    }

    public int getPosition() {
        // 获取当前位置
        return mp.getCurrentPosition();
    }

    public int getLength() {
        return length;
    }

    public void init(String fileName) {
        path = path + "/" + fileName;
        try {
            // 存储在SD卡或其他文件路径下的媒体文件
            mp.setDataSource(path);
            // 音乐文件准备
            mp.prepare();
            // 音乐文件长度
            length = mp.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        mp.start();
    }

    public void pause() {
        mp.pause();
    }

    public void stop() {
        mp.stop();
    }

    public void destroy() {
        mp.release();
    }
}
