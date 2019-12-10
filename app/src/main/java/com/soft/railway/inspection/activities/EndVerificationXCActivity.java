package com.soft.railway.inspection.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.AreaAndLineAdapter;
import com.soft.railway.inspection.adapters.LineStationAdapter;
import com.soft.railway.inspection.fragments.IssueDialogFragment;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.AreaAndLineModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EndVerificationXCActivity extends BaseActivity {
    private WorkPojo workPojo;
    private DataUtil dataUtil;
    private DBUtil dbUtil;
    private HttpUtil httpUtil;
    private IssueDialogFragment fragment;
    private String url = "/app/save";
    private ListView lineStationLV;
    private List<AreaAndLineModel> lineAndStationList;
    private LineStationAdapter lineAndStationAdapter;
    private ListView areaLV;
    private List<AreaAndLineModel> areaList;
    private AreaAndLineAdapter areaAdapter;
    private TextView type;
    private EditText teachET;
    private Button cancel;
    private Button submit;
    private TextView editBtn;
    private ImageView showPhoto;
    private ImageView photoBtn;
    private static final int WHAT = 1;
    private static final int TIPS = 2;
    private Uri uri;
    private ImageView recorderIV;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case WHAT:
                    showPhoto.setImageURI(uri);
                    break;
                case TIPS:
                    new AlertDialog.Builder(EndVerificationXCActivity.this).setTitle("操作提示").setMessage("结束现场检查！")
                            .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent("com.soft.railway.inspection.activities.broadcast");
                            sendBroadcast(intent1);
                            finish();
                            RunningWorkActivity.instance.finish();
                        }
                    }).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_end_verification_xc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("确认信息");
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
        if (DataUtil.DEBUG) {
            url = "/app/save";
        } else {
            url = "/examine/save";
        }
        Intent intent = getIntent();
        workPojo = (WorkPojo) intent.getSerializableExtra("work");
        httpUtil = HttpUtil.getInstance();
        dataUtil = DataUtil.getInstance();
        dbUtil = DBUtil.getInstance();
        areaList = workPojo.getAreaList();
        lineAndStationList = workPojo.getRouteStationList();
        lineAndStationAdapter = new LineStationAdapter(lineAndStationList, EndVerificationXCActivity.this, null, R.layout.item_line_station_xc);
        areaAdapter = new AreaAndLineAdapter(areaList, EndVerificationXCActivity.this, null, R.layout.item_area_xc);
    }

    public void initView() {
        teachET = (EditText) findViewById(R.id.end_verification_xc_teach);
        teachET.setText(workPojo.getTeach());
        type = (TextView) findViewById(R.id.end_verification_xc_type);
        type.setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
        areaLV = (ListView) findViewById(R.id.end_verification_xc_area_list_view);
        lineStationLV = (ListView) findViewById(R.id.end_verification_xc_line_station_list_view);
        areaLV.setAdapter(areaAdapter);
        lineStationLV.setAdapter(lineAndStationAdapter);
        showPhoto = (ImageView) findViewById(R.id.end_verification_xc_show_photo);
        photoBtn = (ImageView) findViewById(R.id.end_verification_xc_photograph_btn);
        photoBtn.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.end_verification_xc_cancel_btn);
        cancel.setOnClickListener(this);
        submit = (Button) findViewById(R.id.end_verification_xc_submit_btn);
        submit.setOnClickListener(this);
        editBtn = (TextView) findViewById(R.id.end_verification_xc_edit_btn);
        editBtn.setOnClickListener(this);
        recorderIV = findViewById(R.id.end_verification_xc_recorder_iv);
        recorderIV.setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.end_verification_xc_cancel_btn:
                Cancel();
                break;
            case R.id.end_verification_xc_photograph_btn:
                PhotoGraph();
                break;
            case R.id.end_verification_xc_submit_btn:
                showIssueDialog();
                break;
            case R.id.end_verification_xc_edit_btn:
                editData();
                break;
            case R.id.end_verification_xc_recorder_iv:
                goRecorder();
                break;
        }
    }

    public void Cancel() {
        finish();
    }

    public void PhotoGraph() {
        Intent intent = new Intent(EndVerificationXCActivity.this, Camera2Activity.class);
        intent.putExtra(DataUtil.CAMERA_ORIENTATION, 0);
        startActivity(intent);
    }

    public void showIssueDialog() {
        fragment = IssueDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), "issueDialog");
        fragment.setmListener(new IssueDialogFragment.IssueDialogInteractionListener() {
            @Override
            public void SubmitClick(String value) {
                workPojo.setWorkSuggest(value);
                Submit();
            }
        });
    }

    public void Submit() {
        fragment.HideIssueDialog();
        final LoadingDialog loadingDialog = LoadingDialog.getInstance(EndVerificationXCActivity.this);
        loadingDialog.show(EndVerificationXCActivity.this);
        workPojo.setEndTime(DateTimeUtil.getNewDateShow());
        workPojo.setTeach(teachET.getText().toString());
        workPojo.setWorkStatus(DataUtil.WORK_STATUS_UNFINISH + "");
        WorkModel workModel = new WorkModel(workPojo);
        final Map map = new HashMap();
        String str = JSONObject.toJSONString(workModel);
        map.put("data", str);
        httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingDialog.cancel();
                MyException myException = new MyException();
                myException.buildException(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response == null) {
                } else {
                    if (response.isSuccessful()) {
                        try {
                            String res = AESUtil.AESDecode(response.body().string());
                            if (!TextUtils.isEmpty(res)) {
                                JSONObject jsonObject = JSONObject.parseObject(res);
                                String errorCode = jsonObject.getString("errorCode");
                                if (!TextUtils.isEmpty(errorCode)) {
                                    if ("0".equals(errorCode)) {
                                        MyApplication.instance.end(workPojo);
                                        dataUtil.editWorkPojoList(workPojo);
                                        dbUtil.delete(DataUtil.TableNameEnum.ITEM.toString(), " workid =? ", new String[]{workPojo.getWorkId()});
                                        dbUtil.delete(DataUtil.TableNameEnum.WORK.toString(), " workid =? ", new String[]{workPojo.getWorkId()});
                                        if (FileUtil.getInstance().updateFileStatus(workPojo.getWorkId())) {
                                            List<FileModel> fileModelList = new ArrayList<>();
                                            Map<String, FileModel> fileModelMap = new HashMap<>();
                                            Cursor fileCursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.SUBMITFILE.toString() + " where filestatus=? group by workid,filetime,filerank ORDER BY filetime,filerank  ", new String[]{FileUtil.FILE_STATUS_WAIT_UPLOADED});
                                            if (fileCursor != null) {
                                                while (fileCursor.moveToNext()) {
                                                    FileModel fileModel = new FileModel(fileCursor);
                                                    fileModelList.add(fileModel);
                                                    fileModelMap.put(fileModel.getFileId(), fileModel);
                                                }
                                                fileCursor.close();
                                            }
                                            dataUtil.setFileModelList(fileModelList);
                                            dataUtil.setFileModelMap(fileModelMap);
                                        }
                                        handler.sendEmptyMessage(TIPS);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            MyException myException = new MyException();
                            myException.buildException(e, EndVerificationXCActivity.this);
                        } finally {
                            loadingDialog.cancel();
                        }
                    }
                }
                loadingDialog.cancel();
            }
        });

    }

    public void editData() {
        Intent intent = new Intent(EndVerificationXCActivity.this, WorkExamineEditXCActivity.class);
        intent.putExtra("work", workPojo);
        startActivityForResult(intent, 700);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 700 && resultCode == 500) {
            workPojo = (WorkPojo) data.getSerializableExtra("data");
            dataUtil.editWorkPojoList(workPojo);
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
            refreshViewMyself();
        } else if(requestCode == 1701 && resultCode == 1900) {
            workPojo = (WorkPojo) data.getSerializableExtra("data");
        }
    }

    public void refreshViewMyself() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                areaList.clear();
                areaList.addAll(workPojo.getAreaList());
                areaAdapter.notifyDataSetChanged();
                lineAndStationList.clear();
                lineAndStationList.addAll(workPojo.getRouteStationList());
                lineAndStationAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void goRecorder() {
        Intent intent = new Intent(EndVerificationXCActivity.this, RecordActivity.class);
        intent.putExtra("data", workPojo);
        startActivityForResult(intent, 1701);
    }
}
