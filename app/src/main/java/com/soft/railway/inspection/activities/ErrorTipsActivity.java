package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.observers.Observer;
import com.soft.railway.inspection.observers.Observerable;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.MyApplication;

public class ErrorTipsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_error_tips);
        TextView textView=(TextView) findViewById(R.id.error_yy);
        Intent intent=getIntent();
        String error=intent.getStringExtra(DataUtil.ERROR_TIPS_INTNET_DATA);
        if(!TextUtils.isEmpty(error)){
            textView.setText(error);
        }
        findViewById(R.id.error_refresh_btn).setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        if(id==R.id.error_refresh_btn){
            MyApplication.finishAll();
        }
    }
}
