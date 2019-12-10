package com.soft.railway.inspection.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.AreaAdapter;
import com.soft.railway.inspection.adapters.AreaEditAdapter;
import com.soft.railway.inspection.adapters.LineStationAdapter;
import com.soft.railway.inspection.adapters.TrainTypeAutoTextViewAdapter;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WorkExamineEditSmallActivity extends BaseActivity {
    private static final int SEARCH_NUM = 8;
    private static final int AREA_WHAT = 0;
    private static final int DRIVER_WHAT = 1;
    private static final int F_DRIVER_WHAT = 2;
    private static final int TRAIN_INFO = 3;
    private static final int NO_TRAIN_INFO = 4;
    private HttpUtil httpUtil;
    private DBUtil dbUtil;
    private DataUtil dataUtil;
    private Map paramsMap = new HashMap();
    private AlertDialog dialog;
    private WorkPojo workPojo;
    private TrainInfoModel trainInfoModel;
    private EditText trainOrderET;
    private AutoCompleteTextView trainTypeAutoCompleteTextView;
    private TrainTypeAutoTextViewAdapter trainTypeAutoTextViewAdapter;
    private List<TrainTypeModel> trainTypeModelList;
    private TextView driverTV;
    private ImageView driverIV;
    private TextView fDriverTV;
    private ImageView fDriverIV;
    private EditText trainIdEditText;
    private Button submitBtn;
    private Button cancelBtn;
    private Button editSubmitBtn;
    private Button editCancelBtn;
    private TextView searchTextView;
    private ImageView explain;
    private ImageView addAreaIV;
    private ListView areaLV;
    private List<AreaAndLineModel> areaAndLineModelList;
    private AreaEditAdapter areaEditAdapter;
    private EditText zzEditText;
    private EditText lsEditText;
    private EditText jcEditText;
    private boolean isDriver = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case AREA_WHAT:
                    areaEditAdapter.notifyDataSetChanged();
                    break;
                case DRIVER_WHAT:
                    driverTV.setText((String) paramsMap.get("driverName"));
                    break;
                case F_DRIVER_WHAT:
                    fDriverTV.setText((String) paramsMap.get("fdriverName"));
                    break;
                case TRAIN_INFO:
                    driverTV.setText(trainInfoModel.getDriverName());
                    fDriverTV.setText(trainInfoModel.getAssistantDriverName());
                    zzEditText.setText(trainInfoModel.getZz());
                    lsEditText.setText(trainInfoModel.getLs());
                    jcEditText.setText(trainInfoModel.getJc());
                    break;
                case NO_TRAIN_INFO:
                    Toast.makeText(getApplicationContext(), "未查到机车相关信息，请稍后再查或手动输入！", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    private AreaEditAdapter.ClickListener clickListener = new AreaEditAdapter.ClickListener() {
        @Override
        public void OnClick(int index) {
            areaAndLineModelList.remove(index);
            handler.sendEmptyMessage(AREA_WHAT);
        }
    };
    private LinearLayout addLinearLayout;
    private LinearLayout editLinearLayout;
    private boolean isEdit = false;
    private String url = "/app/gettraininfos";
    private AdapterView.OnItemClickListener trainTypeOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            paramsMap.put("trainTypeId", trainTypeModelList.get(position).getTrainTypeId());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_work_examine_edit_small);
        Toolbar toolbar = findViewById(R.id.toolbarSmall);
        Intent intent = getIntent();
        workPojo = (WorkPojo) intent.getSerializableExtra("work");
        addLinearLayout = (LinearLayout) findViewById(R.id.work_small_tc_add_linear_layout);
        editLinearLayout = (LinearLayout) findViewById(R.id.work_small_tc_edit_linear_layout);
        dataUtil = DataUtil.getInstance();
        if (workPojo != null) {
            toolbar.setTitle("编辑小添乘");
            isEdit = true;
            addLinearLayout.setVisibility(View.GONE);
            editLinearLayout.setVisibility(View.VISIBLE);
            trainInfoModel = workPojo.getTrainInfo();
            areaAndLineModelList = workPojo.getAreaList();
        } else {
            workPojo = new WorkPojo(dataUtil.getUser());
            trainInfoModel = new TrainInfoModel();
            toolbar.setTitle("创建小添乘");
            areaAndLineModelList = new ArrayList<>();
        }
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
        initEditData();
    }

    public void initData() {
        trainTypeModelList = dataUtil.getTrainTypeList();
        areaEditAdapter = new AreaEditAdapter(areaAndLineModelList, WorkExamineEditSmallActivity.this);
        areaEditAdapter.setClickListener(clickListener);
    }

    public void initView() {
        addAreaIV = (ImageView) findViewById(R.id.work_examine_edit_small_tc_add_area);
        addAreaIV.setOnClickListener(this);
        areaLV = (ListView) findViewById(R.id.work_examine_edit_small_tc_area_list);
        areaLV.setAdapter(areaEditAdapter);
        trainOrderET = (EditText) findViewById(R.id.work_small_tc_order);
        trainTypeAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.work_small_tc_traintype);
        trainTypeAutoTextViewAdapter = new TrainTypeAutoTextViewAdapter(trainTypeModelList, getApplicationContext());
        trainTypeAutoCompleteTextView.setAdapter(trainTypeAutoTextViewAdapter);
        trainTypeAutoCompleteTextView.setDropDownHeight(400);
        trainTypeAutoCompleteTextView.setOnItemClickListener(trainTypeOnItemClickListener);
        trainTypeAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        trainTypeAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    if (validateArea())
                        view.showDropDown();
                } else {
                    validateTrainType();
                }
            }
        });
        driverTV = (TextView) findViewById(R.id.work_small_tc_driver);
        driverIV = (ImageView) findViewById(R.id.work_small_tc_driver_btn);
        driverIV.setOnClickListener(this);
        fDriverTV = (TextView) findViewById(R.id.work_small_tc_f_driver);
        fDriverIV = (ImageView) findViewById(R.id.work_small_tc_f_driver_btn);
        fDriverIV.setOnClickListener(this);
        trainIdEditText = (EditText) findViewById(R.id.work_small_tc_train_id);
        trainIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!validateArea()) {
                        MyApplication.hideSoftKeyboard(WorkExamineEditSmallActivity.this);
                    }
                } else {
                    paramsMap.put("trainId", trainIdEditText.getText().toString());
                }
            }
        });
        submitBtn = (Button) findViewById(R.id.work_small_tc_submit);
        submitBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.work_small_tc_cancle);
        cancelBtn.setOnClickListener(this);
        editSubmitBtn = (Button) findViewById(R.id.work_small_tc_edit_sumit);
        editSubmitBtn.setOnClickListener(this);
        editCancelBtn = (Button) findViewById(R.id.work_small_tc_edit_cancle);
        editCancelBtn.setOnClickListener(this);
        searchTextView = (TextView) findViewById(R.id.work_small_tc_search_cc);
        searchTextView.setOnClickListener(this);
        zzEditText = (EditText) findViewById(R.id.work_small_tc_zz);
        lsEditText = (EditText) findViewById(R.id.work_small_tc_ls);
        jcEditText = (EditText) findViewById(R.id.work_small_tc_jc);
        explain = (ImageView) findViewById(R.id.small_tc_explain);
        explain.setOnClickListener(this);
    }

    public void initEditData() {
        if (isEdit) {
            paramsMap.put("trainTypeId", trainInfoModel.getTrainTypeId());
            paramsMap.put("trainId", trainInfoModel.getTrainId());
            paramsMap.put("trainOrder", trainInfoModel.getTrainOrder());
            paramsMap.put("driverId", trainInfoModel.getDriverId());
            paramsMap.put("fdriverId", trainInfoModel.getAssistantDriverId());
            paramsMap.put("driverName", trainInfoModel.getDriverName());
            paramsMap.put("fdriverName", trainInfoModel.getAssistantDriverName());
            paramsMap.put("zz", trainInfoModel.getZz());
            paramsMap.put("ls", trainInfoModel.getLs());
            paramsMap.put("jc", trainInfoModel.getJc());
            String name = dataUtil.getTrainTypeMap().get(trainInfoModel.getTrainTypeId()) != null ? dataUtil.getTrainTypeMap().get(trainInfoModel.getTrainTypeId()).getTrainTypeName() : trainInfoModel.getTrainTypeId();
            trainTypeAutoCompleteTextView.setText(name);
            trainTypeAutoCompleteTextView.setSelection(name.length());
            trainIdEditText.setText(trainInfoModel.getTrainId());
            trainOrderET.setText(trainInfoModel.getTrainOrder());
            driverTV.setText(trainInfoModel.getDriverName());
            fDriverTV.setText(trainInfoModel.getAssistantDriverName());
            zzEditText.setText(trainInfoModel.getZz());
            lsEditText.setText(trainInfoModel.getLs());
            jcEditText.setText(trainInfoModel.getJc());
            addLinearLayout.setVisibility(View.GONE);
            editLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_small_tc_driver_btn:
                isDriver = true;
                getDriver();
                break;
            case R.id.work_small_tc_f_driver_btn:
                isDriver = false;
                getDriver();
                break;
            case R.id.work_examine_edit_small_tc_add_area:
                goArea();
                break;
            case R.id.work_small_tc_submit:
                submit();
                break;
            case R.id.work_small_tc_cancle:
                goBack();
                break;
            case R.id.work_small_tc_search_cc:
                getTrainInfo();
                break;
            case R.id.work_small_tc_edit_cancle:
                cancelEditData();
                break;
            case R.id.work_small_tc_edit_sumit:
                editData();
                break;
            case R.id.small_tc_explain:
                showExplain();
                break;
        }
    }

    public void getTrainInfo() {
        hideInput();
        final Map<String, String> map = new HashMap<>();
        final String trainTypeId = DataUtil.StringData((String) paramsMap.get("trainTypeId"));
        final String trainOrder = trainOrderET.getText().toString();
        final String trainId = trainIdEditText.getText().toString();
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
            httpUtil.asynch(url, httpUtil.TYPE_POST, params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    loadingDialog.dismiss();
                    MyException myException = new MyException();
                    myException.buildException(e, WorkExamineEditSmallActivity.this);
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
                                    paramsMap.put("trainTypeId", trainInfoModel.getTrainTypeId());
                                    paramsMap.put("trainId", trainInfoModel.getTrainId());
                                    paramsMap.put("trainOrder", trainInfoModel.getTrainOrder());
                                    paramsMap.put("driverId", trainInfoModel.getDriverId());
                                    paramsMap.put("fdriverId", trainInfoModel.getAssistantDriverId());
                                    paramsMap.put("driverName", trainInfoModel.getDriverName());
                                    paramsMap.put("fdriverName", trainInfoModel.getAssistantDriverName());
                                    paramsMap.put("zz", trainInfoModel.getZz());
                                    paramsMap.put("ls", trainInfoModel.getLs());
                                    paramsMap.put("jc", trainInfoModel.getJc());
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
                                paramsMap.put("trainTypeId", trainInfoModel.getTrainTypeId());
                                paramsMap.put("trainId", trainInfoModel.getTrainId());
                                paramsMap.put("trainOrder", trainInfoModel.getTrainOrder());
                                paramsMap.put("driverId", trainInfoModel.getDriverId());
                                paramsMap.put("fdriverId", trainInfoModel.getAssistantDriverId());
                                paramsMap.put("driverName", trainInfoModel.getDriverName());
                                paramsMap.put("fdriverName", trainInfoModel.getAssistantDriverName());
                                paramsMap.put("zz", trainInfoModel.getZz());
                                paramsMap.put("ls", trainInfoModel.getLs());
                                paramsMap.put("jc", trainInfoModel.getJc());
                                handler.sendEmptyMessage(NO_TRAIN_INFO);
                            }
                        } catch (Exception e) {
                            MyException myException = new MyException();
                            myException.buildException(e, WorkExamineEditSmallActivity.this);
                        } finally {
                            loadingDialog.dismiss();
                        }
                    }
                    loadingDialog.dismiss();
                }
            });
        }
    }

    /**
     * 判断是否有区域信息
     *
     * @return false 没有区域信息 true 有区域信息
     */
    public boolean validateArea() {
        if (areaAndLineModelList.size() > 0) {//大于0说明有区域信息
            return true;
        }
        return false;
    }

    public boolean validateTrainType() {
        String trainTypeName = trainTypeAutoCompleteTextView.getText().toString();
        if (TextUtils.isEmpty(trainTypeName)) {
            paramsMap.put("trainTypeId", "");
        } else {
            boolean ret = false;
            String id = "";
            for (TrainTypeModel o : trainTypeModelList) {
                if (trainTypeName.equals(o.getTrainTypeName())) {
                    id = o.getTrainTypeId();
                    ret = true;
                    break;
                }
            }
            if (ret) {
                paramsMap.put("trainTypeId", id);
            } else {
                paramsMap.put("trainTypeId", "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        trainTypeAutoCompleteTextView.setText(null);
                        trainTypeAutoCompleteTextView.setHint("选择正确的车型");
                        trainTypeAutoCompleteTextView.setHintTextColor(getResources().getColor(R.color.errorAccent));
                    }
                });
                return false;
            }
        }
        return true;
    }

    public void getDriver() {
        Intent intent = new Intent(WorkExamineEditSmallActivity.this, SearchDriverActivity.class);
        startActivityForResult(intent, SEARCH_NUM);
    }

    public void goBack() {
        dialog = new AlertDialog.Builder(this).setMessage("取消创建小添乘？？？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    public void goArea() {
        Intent intent = new Intent();
        intent.setClass(WorkExamineEditSmallActivity.this, SearchAreaActivity.class);
        intent.putExtra("params", (Serializable) new HashMap<>());
        startActivityForResult(intent, SEARCH_NUM);
    }

    public void showExplain() {
        new AlertDialog.Builder(this).setTitle("信息说明").setMessage("输入车型，车号，车次可以查询机车相关信息").show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void editData() {
        trainInfoModel.setTrainTypeId(DataUtil.StringData((String) paramsMap.get("trainTypeId")));
        trainInfoModel.setDriverId(DataUtil.StringData((String) paramsMap.get("driverId")));
        trainInfoModel.setAssistantDriverId(DataUtil.StringData((String) paramsMap.get("fdriverId")));
        trainInfoModel.setDriverName(DataUtil.StringData((String) paramsMap.get("driverName")));
        trainInfoModel.setAssistantDriverName(DataUtil.StringData((String) paramsMap.get("fdriverName")));
        trainInfoModel.setZz(zzEditText.getText().toString());
        trainInfoModel.setLs(lsEditText.getText().toString());
        trainInfoModel.setJc(jcEditText.getText().toString());
        trainInfoModel.setTrainId(trainIdEditText.getText().toString());
        trainInfoModel.setTrainOrder(trainOrderET.getText().toString());
        StringBuffer place = new StringBuffer();
        String tx = "; ";
        if (areaAndLineModelList != null && areaAndLineModelList.size() > 0) {
            for (int i = 0, num = areaAndLineModelList.size(); i < num; i++) {
                AreaAndLineModel one = areaAndLineModelList.get(i);
                place.append(one.getAreaName());
                place.append(tx);
            }
        }
        workPojo.setPlace(place.toString());
        workPojo.setTrainInfo(trainInfoModel);
        Intent intent = new Intent();
        intent.putExtra("work", workPojo);
        setResult(900, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_NUM) {
            if (resultCode == 1001) {
                AreaAndLineModel areaAndLineModel = (AreaAndLineModel) data.getSerializableExtra("areaItem");
                areaAndLineModelList.add(areaAndLineModel);
                handler.sendEmptyMessage(AREA_WHAT);
            } else if (resultCode == 800) {
                PersonModel personModel = (PersonModel) data.getSerializableExtra("driver");
                if (isDriver) {
                    paramsMap.put("driverId", personModel.getPersonId());
                    paramsMap.put("driverName", personModel.getPersonName());
                    handler.sendEmptyMessage(DRIVER_WHAT);
                } else {
                    paramsMap.put("fdriverId", personModel.getPersonId());
                    paramsMap.put("fdriverName", personModel.getPersonName());
                    handler.sendEmptyMessage(F_DRIVER_WHAT);
                }
            }
        }
    }

    public void ToastShow(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    public void submit() {
        if (!validateArea()) {
            ToastShow("请添加区域信息！");
        } else if (validateTrainType()) {
            final LoadingDialog loadingDialog = LoadingDialog.getInstance(WorkExamineEditSmallActivity.this);
            loadingDialog.show();
            final String id = DataUtil.getOnlyId();
            UserModel userModel = dataUtil.getUser();
            String userId = userModel.getUserId();
            String appTime = DateTimeUtil.getNewDateShow();
            String status = DataUtil.WORK_STATUS_UNSTART + "";
            String unitId = userModel.getUnitId();
            String type = DataUtil.WORK_TYPE_SMALL_TC;
            paramsMap.put("userId", userId);
            paramsMap.put("status", status);
            paramsMap.put("type", type);
            paramsMap.put("id", id);
            TrainInfoModel trainInfoModel = new TrainInfoModel();
            trainInfoModel.setTrainTypeId(DataUtil.StringData((String) paramsMap.get("trainTypeId")));
            trainInfoModel.setTrainOrder(DataUtil.StringData(trainOrderET.getText().toString()));
            trainInfoModel.setTrainId(DataUtil.StringData(trainIdEditText.getText().toString()));
            trainInfoModel.setZz(zzEditText.getText().toString());
            trainInfoModel.setLs(lsEditText.getText().toString());
            trainInfoModel.setJc(jcEditText.getText().toString());
            trainInfoModel.setDriverId(DataUtil.StringData((String) paramsMap.get("driverId")));
            trainInfoModel.setDriverName(DataUtil.StringData((String) paramsMap.get("driverName")));
            trainInfoModel.setAssistantDriverId(DataUtil.StringData((String) paramsMap.get("fdriverId")));
            trainInfoModel.setAssistantDriverName(DataUtil.StringData((String) paramsMap.get("fdriverName")));
            paramsMap.put("trainId", trainInfoModel.getTrainId());
            paramsMap.put("trainOrder", trainInfoModel.getTrainOrder());
            paramsMap.put("zz", trainInfoModel.getZz());
            paramsMap.put("ls", trainInfoModel.getLs());
            paramsMap.put("jc", trainInfoModel.getJc());
            String place = "";
            String tx = "; ";
            if (areaAndLineModelList != null && areaAndLineModelList.size() > 0) {
                for (int i = 0, num = areaAndLineModelList.size(); i < num; i++) {
                    AreaAndLineModel one = areaAndLineModelList.get(i);
                    place += one.getAreaName();
                    place += tx;
                }
            }
            workPojo.setWorkId(id);
            workPojo.setAreaList(areaAndLineModelList);
            workPojo.setWorkStatus(status);
            workPojo.setWorkType(type);
            workPojo.setPlace(place);
            workPojo.setTrainInfo(trainInfoModel);
            workPojo.setUserId(userId);
            workPojo.setStartTime(appTime);
            workPojo.setPlace(place);
            workPojo.setUnitId(unitId);
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil = DBUtil.getInstance();
            dbUtil.insert(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel));
            dataUtil.addWorkPojoList(workPojo);
            loadingDialog.cancel();
            new AlertDialog.Builder(WorkExamineEditSmallActivity.this).setTitle("操作提示").setMessage("工作创建成功！")
                    .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            ToastShow("请输入正确信息！");
        }
    }

    public void cancelEditData() {
        Intent intent = new Intent();
        intent.putExtra("work", workPojo);
        setResult(901, intent);
        finish();
    }
}
