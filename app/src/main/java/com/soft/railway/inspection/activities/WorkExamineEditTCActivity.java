package com.soft.railway.inspection.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
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

public class WorkExamineEditTCActivity extends BaseActivity {
    private static final int SEARCH_NUM = 5;
    private static final int LINE_WHAT = 0;
    private static final int DRIVER_WHAT = 1;
    private static final int F_DRIVER_WHAT = 2;
    private static final int TRAIN_INFO = 3;
    private static final int NO_TRAIN_INFO = 4;
    private static final int IS_MISSION = 6;
    private boolean isMissionTrain = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case LINE_WHAT:
                    lineStationAdapter.notifyDataSetChanged();
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
                case IS_MISSION:
                    if (isMissionTrain) {
                        missionTrain.setImageResource(R.drawable.ic_radio_select);
                    } else {
                        missionTrain.setImageResource(R.drawable.ic_radio);
                    }
                    break;
            }
        }
    };
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
    private ImageView missionTrain;
    private ImageView fDriverIV;
    private EditText trainIdEditText;
    private Button submitBtn;
    private Button cancelBtn;
    private Button editSubmitBtn;
    private Button editCancelBtn;
    private TextView searchTextView;
    private ImageView explain;
    private ImageView addLineStationIV;
    private ListView lineStationLV;
    private List<AreaAndLineModel> areaAndLineModelList;
    private LineStationAdapter lineStationAdapter;
    private LinearLayout missionLL;
    private EditText zzEditText;
    private EditText lsEditText;
    private EditText jcEditText;
    private boolean isDriver = false;
    private LineStationAdapter.ClickListener clickListener = new LineStationAdapter.ClickListener() {
        @Override
        public void OnClick(int index) {
            areaAndLineModelList.remove(index);
            handler.sendEmptyMessage(LINE_WHAT);
        }
    };
    private LinearLayout addLinearLayout;
    private LinearLayout editLinearLayout;
    private boolean isEdit = false;
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
        setContentView(R.layout.activity_work_examine_edit_tc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.work_edit_toolbar);
        Intent intent = getIntent();
        workPojo = (WorkPojo) intent.getSerializableExtra("work");
        addLinearLayout = (LinearLayout) findViewById(R.id.work_tc_add_linear_layout);
        editLinearLayout = (LinearLayout) findViewById(R.id.work_tc_edit_linear_layout);
        dataUtil = DataUtil.getInstance();
        if (workPojo != null) {
            Log.d("onCreate" + workPojo.toString());
            toolbar.setTitle("编辑添乘检查");
            isEdit = true;
            addLinearLayout.setVisibility(View.GONE);
            editLinearLayout.setVisibility(View.VISIBLE);
            trainInfoModel = workPojo.getTrainInfo();
            areaAndLineModelList = workPojo.getRouteStationList();
        } else {
            workPojo = new WorkPojo(dataUtil.getUser());
            trainInfoModel = new TrainInfoModel();
            toolbar.setTitle("创建添乘检查");
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
        lineStationAdapter = new LineStationAdapter(areaAndLineModelList, WorkExamineEditTCActivity.this, clickListener, R.layout.item_line_station_xc);
    }

    public void initView() {
        addLineStationIV = (ImageView) findViewById(R.id.work_examine_edit_tc_add_line);
        addLineStationIV.setOnClickListener(this);
        lineStationLV = (ListView) findViewById(R.id.work_examine_edit_tc_line_list);
        lineStationLV.setAdapter(lineStationAdapter);
        trainOrderET = (EditText) findViewById(R.id.work_tc_order);
        trainTypeAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.work_tc_traintype);
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
                    if (validateLine())
                        view.showDropDown();
                } else {
                    validateTrainType();
                }
            }
        });
        driverTV = (TextView) findViewById(R.id.work_tc_driver);
        driverIV = (ImageView) findViewById(R.id.work_tc_driver_btn);
        driverIV.setOnClickListener(this);
        fDriverTV = (TextView) findViewById(R.id.work_tc_f_driver);
        fDriverIV = (ImageView) findViewById(R.id.work_tc_f_driver_btn);
        fDriverIV.setOnClickListener(this);
        trainIdEditText = (EditText) findViewById(R.id.work_tc_train_id);
        trainIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (!validateLine()) {
                        MyApplication.hideSoftKeyboard(WorkExamineEditTCActivity.this);
                    }
                } else {
                    paramsMap.put("trainId", trainIdEditText.getText().toString());
                }
            }
        });
        submitBtn = (Button) findViewById(R.id.work_tc_submit);
        submitBtn.setOnClickListener(this);
        cancelBtn = (Button) findViewById(R.id.work_tc_cancle);
        cancelBtn.setOnClickListener(this);
        editSubmitBtn = (Button) findViewById(R.id.work_tc_edit_sumit);
        editSubmitBtn.setOnClickListener(this);
        editCancelBtn = (Button) findViewById(R.id.work_tc_edit_cancle);
        editCancelBtn.setOnClickListener(this);
        searchTextView = (TextView) findViewById(R.id.work_tc_search_cc);
        searchTextView.setOnClickListener(this);
        zzEditText = (EditText) findViewById(R.id.work_tc_zz);
        lsEditText = (EditText) findViewById(R.id.work_tc_ls);
        jcEditText = (EditText) findViewById(R.id.work_tc_jc);
        explain = (ImageView) findViewById(R.id.tc_explain);
        explain.setOnClickListener(this);
        missionTrain = findViewById(R.id.work_tc_mission_train_iv);
        missionLL = findViewById(R.id.work_tc_mission_train_ll);
        String mission = workPojo.getMissionTrain();
        if ("1".equals(mission)) {
            missionTrain.setImageResource(R.drawable.ic_radio_select);
        } else {
            missionTrain.setImageResource(R.drawable.ic_radio);
        }
        missionLL.setOnClickListener(this);
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
            if ("1".equals(workPojo.getMissionTrain())) {
                isMissionTrain = true;
            }
            editLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.work_tc_driver_btn:
                isDriver = true;
                getDriver();
                break;
            case R.id.work_tc_f_driver_btn:
                isDriver = false;
                getDriver();
                break;
            case R.id.work_examine_edit_tc_add_line:
                goSearchLineSEStation();
                break;
            case R.id.work_tc_submit:
                submit();
                break;
            case R.id.work_tc_cancle:
                goBack();
                break;
            case R.id.work_tc_search_cc:
                getTrainInfo();
                break;
            case R.id.work_tc_edit_cancle:
                cancelEditData();
                break;
            case R.id.work_tc_edit_sumit:
                editData();
                break;
            case R.id.tc_explain:
                showExplain();
                break;
            case R.id.work_tc_mission_train_ll:
                isMissionTrain = !isMissionTrain;
                handler.sendEmptyMessage(IS_MISSION);
                break;
        }
    }

    public void goSearchLineSEStation() {
        Intent intent = new Intent();
        intent.setClass(WorkExamineEditTCActivity.this, SearchLineStationActivity.class);
        intent.putExtra("params", (Serializable) new HashMap<>());
        startActivityForResult(intent, SEARCH_NUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_NUM) {
            if (resultCode == 1000) {
                Map<String, String> paramsSearchMap = (Map) data.getSerializableExtra("params");
                String line = paramsSearchMap.get("lineName");
                String startStation = paramsSearchMap.get("startStationName");
                String endStation = paramsSearchMap.get("endStationName");
                String lineId = paramsSearchMap.get("lineId");
                String startStationId = paramsSearchMap.get("startStationId");
                String endStationId = paramsSearchMap.get("endStationId");
                AreaAndLineModel areaAndLineModel = new AreaAndLineModel();
                areaAndLineModel.setLineName(line);
                areaAndLineModel.setEndStationName(endStation);
                areaAndLineModel.setStationName(startStation);
                areaAndLineModel.setLineId(lineId);
                areaAndLineModel.setEndStationId(endStationId);
                areaAndLineModel.setStationId(startStationId);
                areaAndLineModelList.add(areaAndLineModel);
                handler.sendEmptyMessage(LINE_WHAT);
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

    public void submit() {
        if (!validateLine()) {
            ToastShow("请添加线路和车站信息！");
        } else if (validateTrainType()) {
            final LoadingDialog loadingDialog = LoadingDialog.getInstance(WorkExamineEditTCActivity.this);
            loadingDialog.show();
            final String id = DataUtil.getOnlyId();
            UserModel userModel = dataUtil.getUser();
            String userId = userModel.getUserId();
            String appTime = DateTimeUtil.getNewDateShow();
            String status = DataUtil.WORK_STATUS_UNSTART + "";
            String unitId = userModel.getUnitId();
            String type = DataUtil.WORK_TYPE_TC;
            paramsMap.put("userId", userId);
            paramsMap.put("status", status);
            paramsMap.put("type", type);
            paramsMap.put("id", id);
            workPojo.setRouteStationList(areaAndLineModelList);
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
                    place += one.getLineName() + ":" + one.getStationName();
                    if (!TextUtils.isEmpty(one.getEndStationId())) {
                        place += " -> " + one.getEndStationName();
                    }
                    place += tx;
                }
            }
            workPojo.setWorkId(id);
            workPojo.setWorkStatus(status);
            workPojo.setWorkType(type);
            workPojo.setPlace(place);
            workPojo.setTrainInfo(trainInfoModel);
            workPojo.setUserId(userId);
            workPojo.setStartTime(appTime);
            workPojo.setPlace(place);
            workPojo.setUnitId(unitId);
            if (isMissionTrain) {
                workPojo.setMissionTrain(DataUtil.IS_MISSION_TRAIN);
            } else {
                workPojo.setMissionTrain(DataUtil.IS_NOT_MISSION_TRAIN);
            }
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil = DBUtil.getInstance();
            dbUtil.insert(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel));
            dataUtil.addWorkPojoList(workPojo);
            loadingDialog.cancel();
            new AlertDialog.Builder(WorkExamineEditTCActivity.this).setTitle("操作提示").setMessage("工作创建成功！")
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

    /**
     * 判断是否有线路信息
     *
     * @return false 没有线路信息 true 有线路信息
     */
    public boolean validateLine() {
        if (areaAndLineModelList.size() > 0) {//大于0说明有线路信息
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

    public void goBack() {
        dialog = new AlertDialog.Builder(this).setMessage("取消创建添乘检查？？？")
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

    public void ToastShow(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
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
        String place = "";
        String tx = "; ";
        if (areaAndLineModelList != null && areaAndLineModelList.size() > 0) {
            for (int i = 0, num = areaAndLineModelList.size(); i < num; i++) {
                AreaAndLineModel one = areaAndLineModelList.get(i);
                place += one.getLineName() + ":" + one.getStationName();
                if (!TextUtils.isEmpty(one.getEndStationId())) {
                    place += " -> " + one.getEndStationName();
                }
                place += tx;
            }
        }
        workPojo.setPlace(place);
        workPojo.setMissionTrain(missionTrain());
        workPojo.setTrainInfo(trainInfoModel);
        Intent intent = new Intent();
        intent.putExtra("work", workPojo);
        setResult(400, intent);
        finish();
    }

    public void cancelEditData() {
        Intent intent = new Intent();
        intent.putExtra("work", workPojo);
        setResult(401, intent);
        finish();
    }

    public void getDriver() {
        Intent intent = new Intent(WorkExamineEditTCActivity.this, SearchDriverActivity.class);
        startActivityForResult(intent, SEARCH_NUM);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
            httpUtil.asynch(DataUtil.GET_TRAIN_INFO_URL, httpUtil.TYPE_POST, params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    loadingDialog.dismiss();
                    MyException myException = new MyException();
                    myException.buildException(e, WorkExamineEditTCActivity.this);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response != null && response.code() != 500 && response.isSuccessful()) {
                        try {
                            String str = response.body().string();
                            String result = AESUtil.AESDecode(str);
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
                            loadingDialog.dismiss();
                            MyException myException = new MyException();
                            myException.buildException(e, WorkExamineEditTCActivity.this);
                        } finally {
                            loadingDialog.dismiss();
                        }
                    }
                    loadingDialog.dismiss();
                }
            });
        }
    }

    public void showExplain() {
        new AlertDialog.Builder(this).setTitle("信息说明").setMessage("输入车型，车号，车次可以查询机车相关信息").show();
    }

    public String missionTrain() {
        String missionTrain = DataUtil.IS_NOT_MISSION_TRAIN;
        if (isMissionTrain) {
            missionTrain = DataUtil.IS_MISSION_TRAIN;
        }
        return missionTrain;
    }
}
