package com.soft.railway.inspection.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PasswordActivity extends BaseActivity  {
    private TextView  userName;
    private EditText  oldPassword;
    private EditText  newPassword;
    private EditText  tempPassword;
    private Button    btn;
    private DataUtil dataUtil;
    private UserModel userModel;
    private HttpUtil httpUtil;
    String old="";
    String newS="";
    String temp="";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ShowToast(msg.getData().getString("str"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("修改密码");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dataUtil=DataUtil.getInstance();
        httpUtil=HttpUtil.getInstance();
        userModel=dataUtil.getUser();
        userName=(TextView) findViewById(R.id.password_userName);
        userName.setText(userModel.getUserName());
        oldPassword=(EditText) findViewById(R.id.password_old);
        newPassword=(EditText) findViewById(R.id.password_new);
        tempPassword=(EditText) findViewById(R.id.password_new_temp);
        btn=(Button) findViewById(R.id.password_submit_btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        submit();
    }

    public void submit(){
        if(verification()){
            final LoadingDialog loadingDialog=LoadingDialog.getInstance(PasswordActivity.this,"修改密码中...");
            loadingDialog.show(PasswordActivity.this);
            final Map<String,String> map=new HashMap();
            map.put("userId",userModel.getUserId());
            map.put("password",newS);
            final Map<String,String> parasMap=new HashMap();
            parasMap.put("data", JSONObject.toJSONString(map));
            httpUtil.asynch("/app/editpassword",httpUtil.TYPE_POST,parasMap,new Callback(){
                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String ret=response.body().string();
                        try{
                            ret= AESUtil.AESDecode(ret);
                            if(!TextUtils.isEmpty(ret)){
                                JSONObject jsonObject=JSONObject.parseObject(ret);
                                String errorCode=jsonObject.getString("errorCode");
                                if("0".equals(errorCode)){
                                    UserModel userModel=dataUtil.getUser();
                                    userModel.setPassword(newS);
                                    dataUtil.setUser(userModel);
                                    Message message=new Message();
                                    Bundle bundle=new Bundle();
                                    bundle.putString("str","修改成功！");
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                    finish();
                                }
                            }
                        }catch (Exception e){
                            MyException myException=new MyException();
                            myException.buildException(e,PasswordActivity.this);
                        }finally {
                            loadingDialog.dismiss();
                        }
                    }
                    loadingDialog.dismiss();
                }
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    MyException myException=new MyException();
                    myException.buildException(e);
                }
            });
        }
    }

    public boolean verification(){
        old =oldPassword.getText().toString();
        newS=newPassword.getText().toString();
        temp=tempPassword.getText().toString();
        if(TextUtils.isEmpty(old)||TextUtils.isEmpty(newS)||TextUtils.isEmpty(temp)){
            ShowToast("信息不能为空");
           return false;
        }
        if(!userModel.getPassword().equals(old)){
            ShowToast("原密码不符");
            return false;
        }
        if(!newS.equals(temp)){
            ShowToast("两次填写密码不一致");
        }
        return true;
    }

    public void ShowToast(String str){
        Toast.makeText(PasswordActivity.this,str,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
