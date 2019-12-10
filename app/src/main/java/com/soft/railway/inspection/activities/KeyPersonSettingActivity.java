package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.KeyPersonModel;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class KeyPersonSettingActivity extends BaseActivity {
    private TextView    lastMonth;
    private TextView    lastMonthPersonName;
    private TextView    lastQuarter;
    private TextView    lastQuarterPersonName;
    private TextView    month;
    private TextView    monthPersonName;
    private TextView    quarter;
    private TextView    quarterPersonName;
    private ImageView   monthBtn;
    private ImageView   quarterBtn;
    private final int TIPS=0;
    private final int WHAT1=1;
    private final int INIT=2;
    private final int CANCHANGE=3;
    private KeyPersonModel lastMonthModel;
    private KeyPersonModel lastQuarterModel;
    private KeyPersonModel monthModel;
    private KeyPersonModel quarterModel;
    private List<KeyPersonModel> list;
    private String url;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            if(what==TIPS){
                Toast.makeText(KeyPersonSettingActivity.this, "当前时间不允许修改", Toast.LENGTH_SHORT).show();
            }else if(what==WHAT1){
                if(KeyPersonModel.KeyTypeEnum.MONTH.getId().equals(keyType)){
                    monthPersonName.setText(monthModel.getDriverName());
                }else if(KeyPersonModel.KeyTypeEnum.QUARTER.getId().equals(keyType)){
                    quarterPersonName.setText(quarterModel.getDriverName());
                }
            }else if (what==INIT){
                lastMonthPersonName.setText(lastMonthModel!=null?lastMonthModel.getDriverName():"");
                monthPersonName.setText(monthModel!=null?monthModel.getDriverName():"");
                quarterPersonName.setText(quarterModel!=null?quarterModel.getDriverName():"");
                lastQuarterPersonName.setText(lastQuarterModel!=null?lastQuarterModel.getDriverName():"");
                if(lastMonthModel==null&&monthModel==null){
                    monthBtn.setImageResource(R.drawable.ic_edit);
                    monthBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(KeyPersonSettingActivity.this,SelectKeyPersonActivity.class);
                            intent.putExtra("person",monthModel);
                            intent.putExtra("lastPerson",lastMonthModel);
                            keyType=KeyPersonModel.KeyTypeEnum.MONTH.getId();
                            intent.putExtra("keyType",keyType);
                            startActivityForResult(intent,451);
                        }
                    });

                }
                if(quarterModel==null&&lastQuarterModel==null){
                    quarterBtn.setImageResource(R.drawable.ic_edit);
                    quarterBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(KeyPersonSettingActivity.this,SelectKeyPersonActivity.class);
                            intent.putExtra("person",quarterModel);
                            intent.putExtra("lastPerson",lastQuarterModel);
                            keyType=KeyPersonModel.KeyTypeEnum.QUARTER.getId();
                            intent.putExtra("keyType",keyType);
                            startActivityForResult(intent,450);
                        }
                    });
                }
            }
            super.handleMessage(msg);
        }
    };
    private boolean isMonth=false;
    private boolean isQuarter=false;
    private String keyType= KeyPersonModel.KeyTypeEnum.MONTH.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_key_person_setting);
        Toolbar toolbar = findViewById(R.id.toolbar_key);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        getKeyPersonInit();
        initView();

    }


    @Override
    public void onNoDoubleClick(View v) {
        Intent intent=new Intent(this,SelectKeyPersonActivity.class);
        int id=v.getId();
        switch (id){
            case R.id.key_person_quarter_btn:
                if(isQuarter){
                    intent.putExtra("person",quarterModel);
                    intent.putExtra("lastPerson",lastQuarterModel);
                    keyType=KeyPersonModel.KeyTypeEnum.QUARTER.getId();
                    intent.putExtra("keyType",keyType);
                    startActivityForResult(intent,450);
                }else{
                    handler.sendEmptyMessage(TIPS);
                }
                break;
            case R.id.key_person_btn:
                if(isMonth){
                    intent.putExtra("person",monthModel);
                    intent.putExtra("lastPerson",lastMonthModel);
                    keyType=KeyPersonModel.KeyTypeEnum.MONTH.getId();
                    intent.putExtra("keyType",keyType);
                    startActivityForResult(intent,451);
                }else{
                    handler.sendEmptyMessage(TIPS);
                }
                break;
        }
    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        httpUtil=HttpUtil.getInstance();
        list=new ArrayList<>();
        if(DateTimeUtil.inTimeFrame(DateTimeUtil.getMonthDay(26),DateTimeUtil.getMonthLastDay(),new Date())){
            isMonth=true;
        }else{
            isMonth=false;
        }

        if(DateTimeUtil.inTimeFrame(DateTimeUtil.getQuarterDay(26),DateTimeUtil.getQuarterCanChangeDay(),new Date())){
            isQuarter=true;
        }else{
            isQuarter=false;
        }
        url="/app/getkeypersons";

    }

    public void initView(){
        lastMonth        =findViewById(R.id.key_person_last_month);
        lastMonthPersonName=findViewById(R.id.key_person_last_month_name);
        lastQuarter=findViewById(R.id.key_person_last_quarter);
        lastQuarterPersonName=findViewById(R.id.key_person_last_quarter_name);
        month            =findViewById(R.id.key_person_month);
        monthPersonName  =findViewById(R.id.key_person_name);
        quarter          =findViewById(R.id.key_person_quarter);
        quarterPersonName=findViewById(R.id.key_person_quarter_name);
        monthBtn         =findViewById(R.id.key_person_btn);
        quarterBtn       =findViewById(R.id.key_person_quarter_btn);
        //todo 初始化关键人信息
        monthBtn.setImageResource(R.drawable.ic_edit);
        quarterBtn.setImageResource(R.drawable.ic_edit);
        monthBtn.setOnClickListener(this);
        quarterBtn.setOnClickListener(this);
        if(isMonth){
            monthBtn.setImageResource(R.drawable.ic_edit);
            monthBtn.setOnClickListener(this);
        }else{
            monthBtn.setImageResource(R.drawable.ic_no_editing);
            monthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        lastMonth.setText(DateTimeUtil.getMonth()+"月");
        lastQuarter.setText(DateTimeUtil.getQuarterLastStartMonth()+"月-"+DateTimeUtil.getQuarterLastEndMonth()+"月");
        month.setText(DateTimeUtil.getMonth(1)+"月");
        if(isQuarter){
            quarterBtn.setImageResource(R.drawable.ic_edit);
            quarterBtn.setOnClickListener(this);
        }else{
            quarterBtn.setImageResource(R.drawable.ic_no_editing);
            quarterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        quarter.setText(DateTimeUtil.getQuarterStartMonth()+"月-"+DateTimeUtil.getQuarterEndMonth()+"月");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==308){
            if(KeyPersonModel.KeyTypeEnum.QUARTER.getId().equals(keyType)){
                quarterModel=(KeyPersonModel)data.getSerializableExtra("keyPerson");
            }else{
                monthModel=(KeyPersonModel)data.getSerializableExtra("keyPerson");
            }
            handler.sendEmptyMessage(WHAT1);
        }
    }

    public void getKeyPersonInit(){
        final LoadingDialog  loadingDialog=LoadingDialog.getInstance(KeyPersonSettingActivity.this,"数据加载中...");
        loadingDialog.show();
        Map params=new HashMap();
        String userId=dataUtil.getUser().getUserId();
        params.put("userId",userId);
        Map map=new HashMap();
        map.put("data",JSONObject.toJSONString(params));
        httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                list.clear();
                loadingDialog.dismiss();
                MyException myException=new MyException();
                myException.buildException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try{
                        String result= AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject=JSONObject.parseObject(result);
                        String errorCode=jsonObject.getString("errorCode");
                        if("0".equals(errorCode)){
                            List<KeyPersonModel> personList=JSONArray.parseArray(jsonObject.getString("datas"),KeyPersonModel.class);
                            if(personList!=null&&personList.size()>0){
                               list.addAll(personList);
                                for (KeyPersonModel o:list
                                ) {
                                    if(KeyPersonModel.KeyTypeEnum.MONTH.getId().equals(o.getKeyType())){
                                        monthModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.LAST_MONTH.getId().equals(o.getKeyType())){
                                        lastMonthModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.QUARTER.getId().equals(o.getKeyType())){
                                        quarterModel=o;
                                        continue;
                                    }
                                    if(KeyPersonModel.KeyTypeEnum.LAST_QUARTER.getId().equals(o.getKeyType())){
                                        lastQuarterModel=o;
                                        continue;
                                    }
                                }
                            }else{
                                list.clear();
                            }
                            handler.sendEmptyMessage(INIT);
                        }
                    }catch (Exception e){
                        list.clear();
                        MyException myException=new MyException();
                        myException.buildException(e);
                    }finally {
                        loadingDialog.dismiss();
                    }
                }else{
                    list.clear();
                    loadingDialog.dismiss();
                    MyException myException=new MyException();
                    myException.buildException("服务器异常",KeyPersonSettingActivity.this);
                }
            }
        });
    }

}
