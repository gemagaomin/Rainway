package com.soft.railway.inspection.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MediaRecorderUtil {
    private String TAG = "SoundRecording";
    private static MediaRecorderUtil myMediaRecorderUtil;
    //录音
    private static MediaRecorder mRecorder = null;
    /**
     * 采样率
     */
    private static int SAMPLE_RATE_IN_HZ = 8000;
    //播放录音
    private MediaPlayer mPlayer = null;
    private boolean playState = false; // 录音的播放状态
    private String mFileName;
    private String mFilePath;

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        public void run() {
            updateMicStatus();
        }
    };

    //当文件播放结束后调用此方法
    public MediaPlayer.OnCompletionListener completion = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.i("spoort_list", "RecorderControl 播放结束");
            playingFinish();
        }
    };

    private MediaRecorderUtil() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
    }

    public static MediaRecorderUtil getInstance() {
        if (myMediaRecorderUtil == null) {
            synchronized (MediaRecorderUtil.class) {
                if (myMediaRecorderUtil == null) {
                    myMediaRecorderUtil = new MediaRecorderUtil();
                }
            }
        }
        return myMediaRecorderUtil;
    }


    public MediaPlayer getmPlayer() {
        return mPlayer;
    }

    public void setmPlayer(MediaPlayer mPlayer) {
        this.mPlayer = mPlayer;
    }

    /*
     * 开始录音
     */
    public void startRecording(String name, String path) {
        setFile(name,path);
        // 实例化MediaRecorder
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        // 设置音频源为MIC
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置输出文件的格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        //      mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // 设置输出文件的名称
        mRecorder.setOutputFile(mFileName);
        //设置音频的编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //      mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        //设置采样率
        mRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);

        try {
            //得到设置的音频来源，编码器，文件格式等等内容，在start()之前调用
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //开始录音
            mRecorder.start();
            updateMicStatus();
        } catch (Exception e) {
            mRecorder = null;
            mRecorder = new MediaRecorder();
        }
    }


    /**
     * @return String
     */
    /*
     * 停止录音
     */
    public String stopRecording() {
        try {
            mRecorder.stop();
        } catch (Exception e) {
            //释放资源
            mRecorder = null;
            mRecorder = new MediaRecorder();
        }
        //释放资源
        mRecorder.release();
        mRecorder = null;

        return mFileName;
    }


    /**
     * @param completion completion
     */
    //
    public void startPlaying(String name, MediaPlayer.OnCompletionListener completion) {
        if (!playState) {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
            }
            try {
                mPlayer.setDataSource(name);
                mPlayer.prepare();
                playState = true;
                mPlayer.start();
                mPlayer.setOnCompletionListener(completion);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                playState = false;
                //              startPlaying(Filename);
            } else {
                playState = false;
            }
        }
    }

    /**
     * 播放完后释放资源
     */
    public void playingFinish() {
        Log.i("spoort_list", "RecorderControl 播放结束释放资源");
        if (playState) {
            playState = false;
        }
        mPlayer.release();
        mPlayer = null;
    }

    /**
     * 停止播放
     *
     * @return boolean
     */
    public boolean stopPlaying() {

        if (mPlayer != null) {
            //          if(mPlayer!=null&&mPlayer.isPlaying()){
            Log.i("spoort_list", "RecorderControl mPlayer.stop()");
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            playState = false;
            return true;
        } else {
            Log.i("spoort_list", "RecorderControl mPlayer.stop() is null");
            return false;
        }
    }


    /**
     * 获取音量的大小
     *
     * @return double
     */
    public double getAmplitude() {
        if (mRecorder != null) {
            return (mRecorder.getMaxAmplitude());
        } else
            return 0;
    }

    /**
     * 更新话筒状态
     */
    private int BASE = 1;
    private int SPACE = 100;// 间隔取样时间

    public double updateMicStatus() {
        double result=0;
        if (mRecorder != null) {
            double ratio = (double) mRecorder.getMaxAmplitude() / BASE;
            if (ratio > 1){
                result = 20 * Math.log10(ratio);
            }
            Log.d(TAG, "分贝值：" + result);
            //mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
        return result;
    }

    public void setFile(String name, String path){
        mFilePath = DataUtil.RECORDER_PATH;
        if (!TextUtils.isEmpty(path)) {
            mFilePath = path;
        }

        File file = new File(mFilePath);
        if (!file.exists()&&!file.isDirectory()) {
            file.mkdirs();
        }
        //      mFileName += "/" + name + ".3gp";
        // mFileName += "/" + name + ".arm";
        mFileName = mFilePath + "/" + name + ".mp3";
    }


}


