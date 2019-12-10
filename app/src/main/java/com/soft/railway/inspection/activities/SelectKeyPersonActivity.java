package com.soft.railway.inspection.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.KeyPersonAdapter;
import com.soft.railway.inspection.models.KeyPersonModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.pojos.PersonPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SelectKeyPersonActivity extends BaseActivity {
    private ListView lv;
    private TextView oldPerson;
    private TextView noPersonHintTV;
    private List<PersonPojo> list;
    private PersonPojo selectedPersonPojo;
    private KeyPersonAdapter adapter;
    private String url;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private KeyPersonModel keyPersonModel;
    private KeyPersonModel lastKeyPersonModel;
    private String keyType;
    private UserModel userModel;
    private EditText causeET;
    private Button submitBtn;
    private EditText teachET;
    private Button cancelBtn;

    private Button changeBtn;
    private LinearLayout personLVLL;
    private LinearLayout changeETLL;
    private EditText changeCauseET;
    private int isFirstTime=0;
    private static final int STATUS_FIRST=0;//本月和上月都为空
    private static final int STATUS_HAS_LAST=1;//本月为空，上月不为空
    private static final int STATUS_HAS=3;//本月，上月都不为空
    private static final int STATUS_HAS_LAST_EDIT=2;//本月为空，上月不为空,进入编辑状态
    private static final int CHANGE_PERSON_TEXT=0;
    private static final int CHANGE_VIEW=1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case CHANGE_PERSON_TEXT:
                    oldPerson.setText(selectedPersonPojo.getPersonName());
                    adapter.notifyDataSetChanged();
                    break;
                case CHANGE_VIEW:
                    isFirstTime=STATUS_HAS_LAST_EDIT;
                    changeView();
                    break;
            }
        }
    };

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            adapter.select(position);
            selectedPersonPojo = list.get(position);
            handler.sendEmptyMessage(CHANGE_PERSON_TEXT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_key_person);
        Toolbar toolbar = findViewById(R.id.toolbar4);
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
        dataUtil = DataUtil.getInstance();
        httpUtil = HttpUtil.getInstance();
        userModel = dataUtil.getUser();
        Intent intent = getIntent();
        keyPersonModel = (KeyPersonModel) intent.getSerializableExtra("person");
        lastKeyPersonModel=(KeyPersonModel)intent.getSerializableExtra("lastPerson");
        if (keyPersonModel == null&&lastKeyPersonModel==null) {
            keyPersonModel = new KeyPersonModel();
            selectedPersonPojo=new PersonPojo();
            isFirstTime=STATUS_FIRST;
        }else if(keyPersonModel==null&&lastKeyPersonModel!=null){
            keyPersonModel=new KeyPersonModel();
            isFirstTime=STATUS_HAS_LAST;
            if(selectedPersonPojo==null){
                selectedPersonPojo=new PersonPojo();
                selectedPersonPojo.setPersonId(lastKeyPersonModel.getDriverId());
                selectedPersonPojo.setPersonName(lastKeyPersonModel.getDriverName());
            }

        }else if(keyPersonModel!=null&&lastKeyPersonModel!=null){
            isFirstTime=STATUS_HAS;
            if(selectedPersonPojo==null){
                selectedPersonPojo=new PersonPojo();
                selectedPersonPojo.setPersonId(keyPersonModel.getDriverId());
                selectedPersonPojo.setPersonName(keyPersonModel.getDriverName());
            }
        }
        keyType = intent.getStringExtra("keyType");
        list = getKeyPerson(keyPersonModel.getDriverId());
        adapter = new KeyPersonAdapter(list, this);
        if (DataUtil.DEBUG) {
            url = "/app/settingkeyperson";
        } else {
            url = "/app/settingkeyperson";
        }
    }

    public void initView() {
        lv = findViewById(R.id.select_key_person_lv);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(onItemClickListener);
        oldPerson = findViewById(R.id.select_key_person_old_tv);
        oldPerson.setText(selectedPersonPojo.getPersonName());
        causeET = findViewById(R.id.select_key_cause);
        causeET.setText(keyPersonModel.getCause());
        submitBtn = findViewById(R.id.select_key_submit_btn);
        submitBtn.setOnClickListener(this);
        cancelBtn = findViewById(R.id.select_key_cancel_btn);
        cancelBtn.setOnClickListener(this);
        teachET = findViewById(R.id.select_key_teach);
        teachET.setText(keyPersonModel.getTeach());
        noPersonHintTV=findViewById(R.id.select_key_person_tv);
        if(list==null||list.size()<=0){
            noPersonHintTV.setVisibility(View.VISIBLE);
            lv.setVisibility(View.GONE);
        }else{
            noPersonHintTV.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }
        personLVLL=findViewById(R.id.select_key_person_ll);
        changeETLL=findViewById(R.id.select_key_person_change_ll);
        changeBtn=findViewById(R.id.select_key_person_change__btn);
        changeBtn.setOnClickListener(this);
        changeCauseET=findViewById(R.id.select_key_person_change_cause_et);
        changeView();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.select_key_submit_btn:
                submit();
                break;
            case R.id.select_key_cancel_btn:
                finish();
                break;
            case R.id.select_key_person_change__btn:
                handler.sendEmptyMessage(CHANGE_VIEW);
                break;
        }
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.key_person, menu);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.key_person_detail) {
            showDetail();
        }
        return super.onOptionsItemSelected(item);
    }

    public void submit() {
        if (selectedPersonPojo == null) {
            Toast.makeText(SelectKeyPersonActivity.this, "需要选择一位关键人", Toast.LENGTH_SHORT).show();
        } else {
            userModel = dataUtil.getUser();
            final Map<String, String> paraMap = new HashMap();
            String cause = causeET.getText().toString();
            String teach = teachET.getText().toString();
            String changeCause=changeCauseET.getText().toString();
            if (!TextUtils.isEmpty(cause) && !TextUtils.isEmpty(teach)) {
                if((isFirstTime==STATUS_HAS||isFirstTime==STATUS_HAS_LAST_EDIT)&&TextUtils.isEmpty(changeCause)){
                   Toast.makeText(SelectKeyPersonActivity.this, "关键人转化原因不能为空！", Toast.LENGTH_LONG).show();
                }else{
                    final LoadingDialog loadingDialog = LoadingDialog.getInstance(SelectKeyPersonActivity.this);
                    loadingDialog.show(SelectKeyPersonActivity.this);
                    paraMap.put("driverId", selectedPersonPojo.getPersonId());
                    paraMap.put("driverName", selectedPersonPojo.getPersonName());
                    paraMap.put("userId", userModel.getUserId());
                    paraMap.put("unitId", userModel.getUnitId());
                    paraMap.put("keyPersonType", keyType);
                    paraMap.put("cause", cause);
                    paraMap.put("teach", teach);
                    paraMap.put("changeCause",changeCause);
                    paraMap.put("oldKeyPersonId",keyPersonModel.getKeyPersonId());
                    Map map = new HashMap();
                    map.put("data", JSONObject.toJSONString(paraMap));
                    httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            loadingDialog.dismiss();
                            MyException myException = new MyException();
                            myException.buildException(e);
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    String res = AESUtil.AESDecode(response.body().string());
                                    JSONObject jsonObject = JSONObject.parseObject(res);
                                    String errorCode = jsonObject.getString("errorCode");
                                    if ("0".equals(errorCode)) {
                                        loadingDialog.cancel();
                                        keyPersonModel.setDriverId(paraMap.get("driverId"));
                                        keyPersonModel.setDriverName(paraMap.get("driverName"));
                                        keyPersonModel.setKeyType(paraMap.get("keyPersonType"));
                                        keyPersonModel.setCause(paraMap.get("cause"));
                                        keyPersonModel.setTeach(paraMap.get("teach"));
                                        keyPersonModel.setChangeCause(paraMap.get("changeCause"));
                                        Intent intent = new Intent();
                                        intent.putExtra("keyPerson", keyPersonModel);
                                        setResult(308, intent);
                                        finish();
                                    }
                                } catch (Exception e) {
                                    MyException myException = new MyException();
                                    myException.buildException(e, SelectKeyPersonActivity.this);
                                } finally {
                                    loadingDialog.cancel();
                                }
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }
            } else {
                Toast.makeText(SelectKeyPersonActivity.this, "原因或专教措施不能为空！", Toast.LENGTH_LONG).show();
            }
        }
    }

    public List<PersonPojo> getKeyPerson(String personId) {
        List<PersonPojo> list = new ArrayList<>();
        String unitId = dataUtil.getUser().getUnitId();
        Map<String, PersonModel> personMap = dataUtil.getDriverMap();
        for (PersonModel o : personMap.values()
        ) {
            if (o.getUnitId().equals(unitId)) {
                PersonPojo personPojo = o.getPersonPojo(null);
                if (o.getPersonId().equals(personId)) {
                    personPojo.setSelect(true);
                    selectedPersonPojo = personPojo;
                    continue;
                }
                list.add(personPojo);
            }
        }
        return list;
    }

    //todo 后期显示关键人详细信息
    public void showDetail() {
        Intent intent = new Intent(SelectKeyPersonActivity.this, KeyPersonDetailActivity.class);
        intent.putExtra("data", keyPersonModel);
        startActivity(intent);
    }

    private void changeView(){
        switch (isFirstTime){
            case STATUS_FIRST:
                personLVLL.setVisibility(View.VISIBLE);
                changeETLL.setVisibility(View.GONE);
                changeBtn.setVisibility(View.GONE);
                break;
            case STATUS_HAS_LAST:
                personLVLL.setVisibility(View.GONE);
                changeETLL.setVisibility(View.GONE);
                changeBtn.setVisibility(View.VISIBLE);
                break;
            case STATUS_HAS:
                personLVLL.setVisibility(View.GONE);
                changeETLL.setVisibility(View.VISIBLE);
                changeBtn.setVisibility(View.VISIBLE);
                break;
            case STATUS_HAS_LAST_EDIT:
                personLVLL.setVisibility(View.VISIBLE);
                changeETLL.setVisibility(View.VISIBLE);
                break;
        }

    }
}
