package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PointRunningAdapter;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PunishmentLevelModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RunningWorkActivity extends BaseActivity implements AdapterView.OnItemClickListener, PointRunningAdapter.OnClickListener {
    private WorkPojo workPojo;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private DBUtil dbUtil;
    public static RunningWorkActivity instance = null;
    private TextView typeTextView;
    private TextView placeTextView;
    private TextView startTimeTextView;
    private ListView listView;
    private List<WorkItemPojo> list;
    private PointRunningAdapter pointRunningAdapter;
    private String fileName = ".gps";
    private String url = "/examine/edit";
    private String dealUrl = "/app/editItemcard";
    private List<PunishmentLevelModel> punishmentLevelModels;
    private static final int TIPS = 2;
    private FloatingActionButton floatingActionButton;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case TIPS:
                    new AlertDialog.Builder(RunningWorkActivity.this).setTitle("操作提示").setMessage("工作完成！")
                            .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent("com.soft.railway.inspection.activities.broadcast");
                            sendBroadcast(intent);
                            finish();
                        }
                    }).show();
                    break;
                case 1:
                    pointRunningAdapter.notifyDataSetChanged();
                    break;
                case 100:
                    Toast.makeText(RunningWorkActivity.this, "请拍一张自拍照！", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(RunningWorkActivity.this, "至少添加一个项点或一个问题信息！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private Button addProblemPointBtn;
    private Button addPointBtn;
    private Button endVerificationBtn;
    private LinearLayout btnLinearLayout;
    private Button endWorkBtn;
    private LinearLayout endWorkBtnLinearLayout;
    private boolean isRunning = false;
    private int resource = -1;
    private String selectCardId = "";
    private int index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_running_work);
        dataUtil = DataUtil.getInstance();
        dbUtil = DBUtil.getInstance();
        Intent intent = getIntent();
        workPojo = (WorkPojo) intent.getSerializableExtra("data");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.running_work_point_floating_pic_btn);
        if (workPojo.getWorkStatus().equals(DataUtil.WORK_STATUS_RUNNING + "")) {
            isRunning = true;
            toolbar.setTitle("检查中");
            floatingActionButton.setVisibility(View.VISIBLE);
            String gpsName=MyApplication.instance.begin(workPojo.getWorkId());
            workPojo.setGpsFileName(gpsName);
            dataUtil.editWorkPojoList(workPojo);
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
        } else {
            resource = R.drawable.ic_fdan;
            toolbar.setTitle("发牌");
            floatingActionButton.setVisibility(View.GONE);
        }
        instance = this;
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
            url = "/app/editworkstatus";
            dealUrl = "/app/edititemcard";
        } else {
            url = "/examine/edit";
            dealUrl = "/app/edititemcard";
        }
        httpUtil = HttpUtil.getInstance();
        list = new ArrayList<>();
        list.addAll(workPojo.getItems());
        pointRunningAdapter = new PointRunningAdapter(list, this, resource, DataUtil.punishmentLevelModelMap);
        pointRunningAdapter.setMyClickListener(this);
    }

    public void initView() {
        typeTextView = (TextView) findViewById(R.id.running_work_type);
        typeTextView.setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
        placeTextView = (TextView) findViewById(R.id.running_work_place);
        placeTextView.setText(workPojo.getPlace());
        startTimeTextView = (TextView) findViewById(R.id.running_work_starttime);
        startTimeTextView.setText(workPojo.getStartTime());
        listView = (ListView) findViewById(R.id.running_work_point_list_view);
        listView.setAdapter(pointRunningAdapter);
        listView.setOnItemClickListener(this);
        floatingActionButton.setOnClickListener(this);
        endVerificationBtn = (Button) findViewById(R.id.running_work_end_verification_submit_btn);
        addProblemPointBtn = (Button) findViewById(R.id.running_work_add_problem_point_btn);
        addPointBtn = (Button) findViewById(R.id.running_work_add_point_btn);
        if (!isRunning) {
            punishmentLevelModels = dataUtil.getPunishmentLevelList();
            endWorkBtnLinearLayout = (LinearLayout) findViewById(R.id.card_work_btn_linear_layout);
            endWorkBtn = (Button) findViewById(R.id.running_work_end_work_submit_btn);
            btnLinearLayout = (LinearLayout) findViewById(R.id.running_work_btn_linear_layout);
            btnLinearLayout.setVisibility(View.GONE);
            endWorkBtnLinearLayout.setVisibility(View.VISIBLE);
            endWorkBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String endTime = DateTimeUtil.getNewDateShow();
                    workPojo.setWorkStatus(DataUtil.WORK_STATUS_FINISH + "");
                    workPojo.setEndTime(endTime);
                    String time = "";
                    String startTime = workPojo.getStartTime();
                    if (!TextUtils.isEmpty(startTime)) {
                        time += DateTimeUtil.getNewDate(startTime);
                        if (!TextUtils.isEmpty(endTime)) {
                            time += " - " + DateTimeUtil.getNewDate(endTime);
                        }
                    }
                    workPojo.setShowTime(time);
                    submit();
                }
            });
        }
        endVerificationBtn.setOnClickListener(this);
        addProblemPointBtn.setOnClickListener(this);
        addPointBtn.setOnClickListener(this);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (isRunning)
            getMenuInflater().inflate(R.menu.right_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isRunning) {
            if (item.getItemId() == R.id.right_menu_delete_work) {
                showDialog();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WorkItemPojo workItemPojo = list.get(position);
        if (isRunning) {
            Intent intent = new Intent();
            intent.setClass(RunningWorkActivity.this, PointEditActivity.class);
            intent.putExtra("work", workPojo);
            intent.putExtra("pointData", workItemPojo);
            startActivityForResult(intent, 100);
        } else {
            showGiveOutCard(workItemPojo.getCard(), position);
        }
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        Intent intent;
        switch (id) {
            case R.id.running_work_add_problem_point_btn:
                intent = new Intent();
                intent.setClass(RunningWorkActivity.this, PointEditActivity.class);
                intent.putExtra("work", workPojo);
                startActivityForResult(intent, 100);
                break;
            case R.id.running_work_end_verification_submit_btn:
                intent = new Intent();
                workPojo.setItems(list);
                if (!VerificationItems()) {
                    handler.sendEmptyMessage(101);
                } else if (!VerificationPhoto()) {
                    handler.sendEmptyMessage(100);
                } else {
                    if (DataUtil.WORK_TYPE_TC.equals(workPojo.getWorkType())) {
                        intent.setClass(RunningWorkActivity.this, EndVerificationTCActivity.class);
                    } else if (DataUtil.WORK_TYPE_XCJC.equals(workPojo.getWorkType())) {
                        intent.setClass(RunningWorkActivity.this, EndVerificationXCActivity.class);
                    } else if (DataUtil.WORK_TYPE_SMALL_TC.equals(workPojo.getWorkType())) {
                        intent.setClass(RunningWorkActivity.this, EndVerificationSmallActivity.class);
                    } else if (DataUtil.WORK_TYPE_ZXJC.equals(workPojo.getWorkType())) {
                        intent.setClass(RunningWorkActivity.this, EndVerificationZXActivity.class);
                    }
                    intent.putExtra("work", workPojo);
                    startActivityForResult(intent, 100);
                }
                break;
            case R.id.running_work_point_floating_pic_btn:
                intent = new Intent(RunningWorkActivity.this, Camera2Activity.class);
                intent.putExtra(DataUtil.CAMERA_ORIENTATION, 0);
                startActivityForResult(intent, 1300);
                break;
            case R.id.running_work_add_point_btn:
                intent = new Intent(RunningWorkActivity.this, SearchPointActivity.class);
                intent.putExtra("data", workPojo);
                startActivityForResult(intent, 1200);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null) {
            workPojo = (WorkPojo) data.getSerializableExtra("work");
            Log.d("Running onActivityResult " + workPojo.toString());
            if (resultCode == 80) {
                WorkItemPojo workItemPojo = (WorkItemPojo) data.getSerializableExtra("pointData");
                list.add(workItemPojo);
            } else if (resultCode == 81) {
                WorkItemPojo workItemPojo = (WorkItemPojo) data.getSerializableExtra("pointData");
                for (int i = 0, num = list.size(); i < num; i++) {
                    WorkItemPojo o = list.get(i);
                    if (o.getItemId().equals(workItemPojo.getItemId())) {
                        list.set(list.indexOf(o), workItemPojo);
                        break;
                    }
                }
            } else if (resultCode == 50) {
            }
            workPojo.getItems().clear();
            workPojo.getItems().addAll(list);
            //todo 2019-11-20晚上修改
            dataUtil.editWorkPojoList(workPojo);
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
            changeView();
        } else if (requestCode == 1300) {
            SharedPreferences sharedPreferences = getSharedPreferences("selfphoto", Activity.MODE_PRIVATE);
            fileName = sharedPreferences.getString("fileName", "");
            if (!TextUtils.isEmpty(fileName)) {
                FileUtil fileUtil = FileUtil.getInstance();
                File file = new File(DataUtil.PHOTO_PATH + File.separator + fileName);
                FileModel fileModel = new FileModel();
                fileModel.setFileId(fileName);
                fileModel.setFileName(fileName);
                fileModel.setFilePath(file.getAbsolutePath());
                fileModel.setFileStatus(FileUtil.FILE_STATUS_NOT_CAN_UPLOADED);
                fileModel.setFileRank(FileUtil.FILE_RANK_HIGH);
                fileModel.setFileType(FileUtil.FILE_TYPE_PHOTO);
                fileModel.setItemId("");
                fileModel.setWorkId(workPojo.getWorkId());
                fileModel.setUserId(workPojo.getUserId());
                fileModel.setFileTime(DateTimeUtil.getNewDateShow());
                List<FileModel> fileModels = workPojo.getCapturePhotos();
                fileModels.add(fileModel);
                //todo 2019-11-20晚上修改
                dataUtil.editWorkPojoList(workPojo);
                fileUtil.insertFile(fileModel);
                workPojo.setCapturePhotos(fileModels);
                WorkModel workModel = new WorkModel(workPojo);
                dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fileName", "");
                editor.commit();
            }
        } else if (requestCode == 1200) {
            if (resultCode == 900) {
                workPojo = (WorkPojo) data.getSerializableExtra("data");
                dataUtil.editWorkPojoList(workPojo);
            }
        }
    }

    @Override
    public void delete(int position) {
        WorkItemPojo workItemPojo = list.get(position);
        if (isRunning) {
            dbUtil.delete(DataUtil.TableNameEnum.ITEM.toString(), " itemid=? ", new String[]{workItemPojo.getItemId()});
            list.remove(position);
            workPojo.setItems(list);
            dataUtil.removePointDetail(workItemPojo);
            //todo 2019-11-20晚上修改
            dataUtil.editWorkPojoList(workPojo);
            WorkModel workModel = new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
            changeView();
        } else {
            showGiveOutCard(workItemPojo.getCard(), position);
        }
    }

    public void changeView() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                pointRunningAdapter.notifyDataSetChanged();
            }
        });
    }

    public void showDialog() {
        new AlertDialog.Builder(RunningWorkActivity.this).setTitle("提示")
                .setMessage("确定删除当前检查信息？？？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteWork();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    public void deleteWork() {
        MyApplication.instance.end(workPojo);
        dataUtil.removeWorkPojoList(workPojo);
        dataUtil.removePointDetail(workPojo.getWorkId());
        dbUtil.delete(DataUtil.TableNameEnum.ITEM.toString(), " workid=? ", new String[]{workPojo.getWorkId()});
        dbUtil.delete(DataUtil.TableNameEnum.WORK.toString(), " workid=? ", new String[]{workPojo.getWorkId()});
        dbUtil.delete(DataUtil.TableNameEnum.SUBMITFILE.toString(), " workid=? ", new String[]{workPojo.getWorkId()});
        finish();
    }

    //发牌
    public void showGiveOutCard(String card, int in) {
        index = in;
        int num = 0;
        final String arr[] = new String[punishmentLevelModels.size()];
        for (int i = 0, n = punishmentLevelModels.size(); i < n; i++) {
            PunishmentLevelModel s = punishmentLevelModels.get(i);
            arr[i] = s.getCardName();
            if (TextUtils.isEmpty(card)) {
                card = "0";
            }
            if (card.equals(s.getCardId())) {
                num = i;
            }
        }
        new AlertDialog.Builder(this).setSingleChoiceItems(arr, num, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectCardId = punishmentLevelModels.get(which).getCardId();
                Log.d("selectCardId  " + selectCardId);
            }
        }).setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DealWorkItem();
            }
        }).show();
    }

    public void changViewCard() {
        WorkItemPojo workItemPojo = list.get(index);
        workItemPojo.setCard(selectCardId);
        list.set(index, workItemPojo);
        handler.sendEmptyMessage(1);
    }

    public void DealWorkItem() {
        final LoadingDialog loadingDialog = LoadingDialog.getInstance(RunningWorkActivity.this);
        loadingDialog.show(RunningWorkActivity.this);
        Map<String, String> params = new HashMap();
        params.put("itemId", list.get(index).getItemId());
        params.put("dealTypeId", selectCardId);
        final Map map = new HashMap();
        map.put("data", JSONObject.toJSONString(params));
        httpUtil.asynch(dealUrl, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingDialog.cancel();
                MyException myException = new MyException();
                myException.buildException(e, RunningWorkActivity.this);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject = JSONObject.parseObject(res);
                        String ret = jsonObject.getString("errorCode");
                        if ("0".equals(ret)) {
                            changViewCard();
                        }
                    } catch (Exception e) {
                        MyException myException = new MyException();
                        myException.buildException(e, RunningWorkActivity.this);
                    } finally {
                        loadingDialog.cancel();
                    }
                }
                loadingDialog.cancel();
            }
        });
    }

    public void submit() {
        final LoadingDialog loadingDialog = LoadingDialog.getInstance(RunningWorkActivity.this);
        loadingDialog.show(RunningWorkActivity.this);
        workPojo.setWorkStatus(DataUtil.WORK_STATUS_FINISH + "");
        workPojo.setEndTime(DateTimeUtil.getNewDateShow());
        workPojo.setItems(list);
        Map<String, String> parasMap = new HashMap();
        parasMap.put("workId", workPojo.getWorkId());
        Map map = new HashMap();
        map.put("data", JSONObject.toJSONString(parasMap));
        httpUtil.asynch(url, httpUtil.TYPE_POST, map, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                loadingDialog.cancel();
                MyException myException = new MyException();
                myException.buildException(e, RunningWorkActivity.this);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject = JSONObject.parseObject(res);
                        String ret = jsonObject.getString("errorCode");
                        if ("0".equals(ret)) {
                            workPojo.setWorkStatus(DataUtil.WORK_STATUS_FINISH + "");
                            dataUtil.editWorkPojoList(workPojo);
                            handler.sendEmptyMessage(TIPS);
                        }
                    } catch (Exception e) {
                        loadingDialog.cancel();
                        MyException myException = new MyException();
                        myException.buildException(e, RunningWorkActivity.this);
                    } finally {
                        loadingDialog.cancel();
                    }
                }
                loadingDialog.cancel();
            }
        });
    }

    public boolean VerificationItems() {
        boolean ret = true;
        if ((workPojo.getPlanWorkItems() == null || workPojo.getPlanWorkItems().size() == 0) && (workPojo.getItems() == null || workPojo.getItems().size() == 0))
            ret = false;
        return ret;
    }

    public boolean VerificationPhoto() {
        boolean ret = true;
        if (workPojo.getCapturePhotos() == null || workPojo.getCapturePhotos().size() < 2)
            ret = false;
        return ret;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}