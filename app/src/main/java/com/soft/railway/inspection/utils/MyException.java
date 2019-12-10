package com.soft.railway.inspection.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;
import com.soft.railway.inspection.activities.ErrorTipsActivity;

public class MyException extends Exception {
    private static boolean sDebug= false;
    public void buildException(Exception e){
        //TODO 通过不同的异常，做不同的提示
        Log.d(e.toString());
    }

    public void buildException(Exception e,Context context){
        //TODO 通过不同的异常，做不同的提示
        Log.d(e.toString());
        Looper.prepare();
        String str=e.getMessage();
        if(!sDebug){
            str="网络链接错误！";
        }
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public void buildException(String message,Context context){
        //TODO 通过不同的异常，做不同的提示
        Log.d(message);
        Looper.prepare();
        Intent intent=new Intent(context,ErrorTipsActivity.class);
        intent.putExtra(DataUtil.ERROR_TIPS_INTNET_DATA,message);
        context.startActivity(intent);
        Looper.loop();
    }
}
