package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.TrainTypeAutoTextViewAdapter;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SetTrainInfoActivity extends BaseActivity {

    private static final int SEARCH_NUM_SET = 25;
    private static final int TRAIN_INFO = 3;
    private static final int NO_TRAIN_INFO = 4;
    private static final int DRIVER_WHAT = 1;
    private static final int F_DRIVER_WHAT = 2;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private TrainInfoModel trainInfoModel;
    private ImageView setTrainInfoSubmitIV;
    private List<TrainTypeModel> trainTypeModels;
    private TrainTypeAutoTextViewAdapter trainTypeAutoTextViewAdapter;
    private AutoCompleteTextView trainTypeIdACTV;
    private EditText trainIdET;
    private EditText trainOrderET;
    private TextView searchBtn;
    private ImageView explainIV;
    private TextView driverIdTV;
    private ImageView driverBtnIV;
    private TextView assistantDriverIdTV;
    private ImageView assistantDriverIdBtnIV;
    private EditText zzET;
    private EditText jcET;
    private EditText lsET;
    private boolean isDriver = false;
    private Map<String, TrainTypeModel> trainTypeModelMap;
    private AdapterView.OnItemClickListener trainTypeOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TrainTypeModel o = trainTypeModels.get(position);
            trainInfoModel.setTrainTypeId(o.getTrainTypeId());
        }
    };
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NO_TRAIN_INFO:
                    Toast.makeText(getApplicationContext(), "未查到机车相关信息，请稍后再查或手动输入！", Toast.LENGTH_LONG).show();
                    break;
                case TRAIN_INFO:
                    driverIdTV.setText(trainInfoModel.getDriverName());
                    assistantDriverIdTV.setText(trainInfoModel.getAssistantDriverName());
                    zzET.setText(trainInfoModel.getZz());
                    lsET.setText(trainInfoModel.getLs());
                    jcET.setText(trainInfoModel.getJc());
                    break;
                case DRIVER_WHAT:
                    driverIdTV.setText(trainInfoModel.getDriverName());
                    assistantDriverIdTV.setText(trainInfoModel.getAssistantDriverName());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_train_info);
        Toolbar toolbar = findViewById(R.id.set_train_info_toolbar);
        toolbar.setTitle("编辑机车信息");
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
        initView();
    }

    public void initData() {
        Intent intent = getIntent();
        trainInfoModel = (TrainInfoModel) intent.getSerializableExtra("data");
        if (trainInfoModel == null) {
            trainInfoModel = new TrainInfoModel();
        }
        dataUtil = DataUtil.getInstance();
        httpUtil = HttpUtil.getInstance();
        trainTypeModels = dataUtil.getTrainTypeList();
        trainTypeAutoTextViewAdapter = new TrainTypeAutoTextViewAdapter(trainTypeModels, this);
        trainTypeModelMap = dataUtil.getTrainTypeMap();
    }

    public void initView() {
        trainTypeIdACTV = findViewById(R.id.set_train_info_type);
        trainTypeIdACTV.setAdapter(trainTypeAutoTextViewAdapter);
        trainTypeIdACTV.setDropDownHeight(400);
        trainTypeIdACTV.setOnItemClickListener(trainTypeOnItemClickListener);
        trainTypeIdACTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        trainTypeIdACTV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                } else {
                    validateTrainType();
                }
            }
        });
        trainIdET = findViewById(R.id.set_train_info_id);
        trainOrderET = findViewById(R.id.set_train_info_order);
        searchBtn = findViewById(R.id.set_train_info_search);
        explainIV = findViewById(R.id.set_train_info_explain);
        driverIdTV = findViewById(R.id.set_train_info_driver);
        driverBtnIV = findViewById(R.id.set_train_info_driver_btn);
        assistantDriverIdTV = findViewById(R.id.set_train_info_f_driver);
        assistantDriverIdBtnIV = findViewById(R.id.set_train_info_f_driver_btn);
        zzET = findViewById(R.id.set_train_info_zz);
        jcET = findViewById(R.id.set_train_info_jc);
        lsET = findViewById(R.id.set_train_info_ls);
        String typeName = trainInfoModel.getTrainTypeIdName(trainTypeModelMap);
        trainTypeIdACTV.setText(typeName);
        trainTypeIdACTV.setSelection(typeName.length());
        trainIdET.setText(trainInfoModel.getTrainId());
        trainOrderET.setText(trainInfoModel.getTrainOrder());
        searchBtn.setOnClickListener(this);
        explainIV.setOnClickListener(this);
        driverIdTV.setText(trainInfoModel.getDriverName());
        driverBtnIV.setOnClickListener(this);
        assistantDriverIdTV.setText(trainInfoModel.getAssistantDriverName());
        assistantDriverIdBtnIV.setOnClickListener(this);
        zzET.setText(trainInfoModel.getZz());
        jcET.setText(trainInfoModel.getJc());
        lsET.setText(trainInfoModel.getLs());

        setTrainInfoSubmitIV = findViewById(R.id.set_train_info_submit);
        setTrainInfoSubmitIV.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_NUM_SET) {
            if (resultCode == 800) {
                PersonModel personModel = (PersonModel) data.getSerializableExtra("driver");
                String id = personModel.getPersonId();
                String name = personModel.getPersonName();
                if (isDriver) {
                    trainInfoModel.setDriverId(id);
                    trainInfoModel.setDriverName(name);
                } else {
                    trainInfoModel.setAssistantDriverId(id);
                    trainInfoModel.setAssistantDriverName(name);
                }
                handler.sendEmptyMessage(DRIVER_WHAT);
            }
        }
    }

    @Override
    public void onNoDoubleClick(View v) {
        switch (v.getId()) {
            case R.id.set_train_info_driver_btn:
                isDriver = true;
                getDriver();
                break;
            case R.id.set_train_info_f_driver_btn:
                isDriver = false;
                getDriver();
                break;
            case R.id.set_train_info_search:
                getTrainInfo();
                break;
            case R.id.set_train_info_explain:
                showExplain();
                break;
            case R.id.set_train_info_submit:
                submit();
                break;
        }
    }

    public void getDriver() {
        Intent intent = new Intent(SetTrainInfoActivity.this, SearchDriverActivity.class);
        startActivityForResult(intent, SEARCH_NUM_SET);
    }

    public void getTrainInfo() {
        hideInput();
        final Map<String, String> map = new HashMap<>();
        final String trainTypeId = trainInfoModel.getTrainTypeId();
        final String trainOrder = trainOrderET.getText().toString();
        final String trainId = trainIdET.getText().toString();
        if (TextUtils.isEmpty(trainTypeId) || TextUtils.isEmpty(trainOrder) || TextUtils.isEmpty(trainId)) {
            Toast.makeText(this, "车型，车号，车次不能为空！", Toast.LENGTH_LONG).show();
        } else {
            map.put("trainTypeId", trainTypeId);
            map.put("trainId", trainId);
            map.put("trainOrder", trainOrder);
            if (httpUtil == null) {
                httpUtil = HttpUtil.getInstance();
            }
            final LoadingDialog loadingDialog = LoadingDialog.getInstance(this, "加载机车相关信息...");
            loadingDialog.show(this);
            Map<String, String> params = new HashMap<>();
            params.put("data", JSONObject.toJSONString(map));
            httpUtil.asynch(DataUtil.GET_TRAIN_INFO_URL, httpUtil.TYPE_POST, params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    loadingDialog.dismiss();
                    MyException myException = new MyException();
                    myException.buildException(e, SetTrainInfoActivity.this);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response != null && response.code() != 500 && response.isSuccessful()) {
                        try {
                            String result = AESUtil.AESDecode(response.body().string());
                            JSONObject jsonObject = JSONObject.parseObject(result);
                            String trainInfoSome = jsonObject.getString("trainInfo");
                            JSONObject trainInfoObject=null;
                            JSONObject trainInfoTrueObject=null;
                            trainInfoObject = JSONObject.parseObject(trainInfoSome);
                            String errorCode ="";
                            if(DataUtil.DEBUG){
                                errorCode = trainInfoObject.getString("errorCode");
                                String trainInfoString=trainInfoObject.getString("trainInfo");
                                trainInfoTrueObject=JSONObject.parseObject(trainInfoString);
                            }else{
                                trainInfoTrueObject=trainInfoObject;
                                errorCode = jsonObject.getString("errorCode");
                            }
                            if ("0".equals(errorCode)) {
                                if (TextUtils.isEmpty(trainInfoSome)) {
                                    handler.sendEmptyMessage(NO_TRAIN_INFO);
                                } else {
                                    trainInfoModel.setTrainTypeId(trainTypeId);
                                    trainInfoModel.setTrainOrder(trainOrder);
                                    trainInfoModel.setTrainId(trainId);
                                    trainInfoModel.setLs(trainInfoTrueObject.getString("vehicleNumber"));
                                    trainInfoModel.setZz(trainInfoTrueObject.getString("totalWeight"));
                                    trainInfoModel.setJc(trainInfoTrueObject.getString("meterLength"));
                                    trainInfoModel.setDriverName(trainInfoTrueObject.getString("driverName"));
                                    trainInfoModel.setDriverId(trainInfoTrueObject.getString("driverId"));
                                    trainInfoModel.setAssistantDriverId(trainInfoTrueObject.getString("assDriverId"));
                                    trainInfoModel.setAssistantDriverName(trainInfoTrueObject.getString("assDriverName"));
                                    handler.sendEmptyMessage(TRAIN_INFO);
                                }
                            } else {
                                trainInfoModel.setTrainTypeId(trainTypeId);
                                trainInfoModel.setTrainOrder(trainOrder);
                                trainInfoModel.setTrainId(trainId);
                                trainInfoModel.setLs("");
                                trainInfoModel.setZz("");
                                trainInfoModel.setJc("");
                                trainInfoModel.setDriverName("");
                                trainInfoModel.setDriverId("");
                                trainInfoModel.setAssistantDriverId("");
                                trainInfoModel.setAssistantDriverName("");
                                handler.sendEmptyMessage(NO_TRAIN_INFO);
                            }
                        } catch (Exception e) {
                            loadingDialog.dismiss();
                            MyException myException = new MyException();
                            myException.buildException(e, SetTrainInfoActivity.this);
                        } finally {
                            loadingDialog.dismiss();
                        }
                    }
                    loadingDialog.dismiss();
                }
            });
        }
    }

    public boolean validateTrainType() {
        String trainTypeName = trainTypeIdACTV.getText().toString();
        if (TextUtils.isEmpty(trainTypeName)) {
            trainInfoModel.setTrainTypeId("");
        } else {
            boolean ret = false;
            String id = "";
            for (TrainTypeModel o : trainTypeModels) {
                if (trainTypeName.equals(o.getTrainTypeName())) {
                    id = o.getTrainTypeId();
                    ret = true;
                    break;
                }
            }
            if (ret) {
                trainInfoModel.setTrainTypeId(id);
            } else {
                trainInfoModel.setTrainTypeId("");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        trainTypeIdACTV.setText(null);
                        trainTypeIdACTV.setHint("选择正确的车型");
                        trainTypeIdACTV.setHintTextColor(getResources().getColor(R.color.errorAccent));
                    }
                });
                return false;
            }
        }
        return true;
    }

    public void showExplain() {
        new AlertDialog.Builder(this).setTitle("信息说明").setMessage("输入车型，车号，车次可以查询机车相关信息").show();
    }

    public void submit() {
        Intent intent = new Intent();
        intent.setClass(SetTrainInfoActivity.this, PointEditActivity.class);
        trainInfoModel.setTrainOrder(trainOrderET.getText().toString());
        trainInfoModel.setTrainId(trainIdET.getText().toString());
        trainInfoModel.setLs(lsET.getText().toString());
        trainInfoModel.setZz(zzET.getText().toString());
        trainInfoModel.setJc(jcET.getText().toString());
        intent.putExtra("data", trainInfoModel);
        setResult(123, intent);
        finish();
    }

}
