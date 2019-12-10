package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.PhotoRecycleAdapter;
import com.soft.railway.inspection.adapters.PointAutoTextViewAdapter;
import com.soft.railway.inspection.adapters.SearchUnitAutoTextViewAdapter;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.models.TrainTypeModel;
import com.soft.railway.inspection.models.UnitModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkItemModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.observers.Observer;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.LoadingDialog;
import com.soft.railway.inspection.utils.PhotoObserverableUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointEditActivity extends BaseActivity implements Observer {
    private static final int WHAT_PHOTO = 1;
    private static final int WHAT_VIDEO = 0;
    private static final int IS_OTHER_AUTO_VIEW = 3;
    private static final int NOT_OTHER_AUTO_VIEW = 4;
    private static final int OTHER_AUTO_VIEW = 5;
    private static final int SET_TRAIN_INFO = 6;
    private boolean isTC = false;
    private Map<String, TrainTypeModel> trainTypeModelMap;
    private List<PointItemModel> pointModels;
    private List<PointItemModel> pointOtherModels;
    private Map<String, PersonModel> personModelMap;
    private LinearLayout otherLinearLayout;
    private FileUtil fileUtil;
    private PhotoObserverableUtil photoObserverableUtil;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String showMsg = bundle.getString("data");
            int what = msg.what;
            switch (what) {
                case WHAT_PHOTO:
                    photoAdapter.notifyDataSetChanged();
                    break;
                case WHAT_VIDEO:
                    videoAdapter.notifyDataSetChanged();
                    break;
                case IS_OTHER_AUTO_VIEW:
                    otherLinearLayout.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(showMsg)) {
                        otherTextView.setText(showMsg);
                        otherTextView.setSelection(showMsg.length());
                    }
                    otherAdapter.notifyDataSetChanged();
                    break;
                case NOT_OTHER_AUTO_VIEW:
                    otherLinearLayout.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(showMsg)) {
                        pointTypeIdAutoCompleteTextView.setText(showMsg);
                        pointTypeIdAutoCompleteTextView.setSelection(showMsg.length());
                    }
                    pointAutoTextViewAdapter.notifyDataSetChanged();
                    break;
                case OTHER_AUTO_VIEW:
                    if (!TextUtils.isEmpty(showMsg)) {
                        otherTextView.setText(showMsg);
                        otherTextView.setSelection(showMsg.length());
                    }
                    otherAdapter.notifyDataSetChanged();
                    break;
                case SET_TRAIN_INFO:
                    String order = trainInfoModel.getTrainOrder();
                    itemTrainInfoTV.setText(trainInfoModel.getTrainTypeIdName(trainTypeModelMap) + "  " + (TextUtils.isEmpty(order) ? "" : order));
                    peopleUnitAutoCompleteTextView.setText(workItemPojo.getUnitName());
                    peopleUnitAutoCompleteTextView.setSelection(workItemPojo.getUnitName().length());
                    peoplesTextView.setText(workItemPojo.getShowPeoples());
                    break;
            }
        }
    };
    private WorkItemPojo workItemPojo;
    private AutoCompleteTextView pointTypeIdAutoCompleteTextView;
    private PointAutoTextViewAdapter pointAutoTextViewAdapter;
    private List<PointItemModel> pointList;
    private AutoCompleteTextView otherTextView;
    private PointAutoTextViewAdapter otherAdapter;
    private List<PointItemModel> pointOtherList;
    private EditText pointSupplementTextView;
    private TextView peoplesTextView;
    private ImageView peopleImageView;
    private AutoCompleteTextView peopleUnitAutoCompleteTextView;
    private SearchUnitAutoTextViewAdapter unitAdapter;
    private LinearLayout otherLL;
    // private LinearLayout    sjLL    ;
    private TextView sjTV;
    private TextView fsjTV;
    private ImageView addSJIV;
    private ImageView addFSJIV;
    private ImageView sjZrrIV;
    private ImageView fsjZrrIV;
    private boolean isSjZrr;
    private boolean isFsjZrr;
    private List<UnitModel> unitModelList;
    private RecyclerView photoRV;
    private PhotoRecycleAdapter photoAdapter;
    private List<FileModel> photoList;
    private PhotoRecycleAdapter.OnRecycleViewItemClickListener photoRVICL = new PhotoRecycleAdapter.OnRecycleViewItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            Intent intent = new Intent(PointEditActivity.this, ShowPhotoVideoActivity.class);
            intent.putExtra("isfinish", false);
            intent.putExtra("data", (Serializable) photoList.get(position));
            startActivity(intent);
        }

        @Override
        public void DeleteItemClick(View view, int position) {
            photoList.remove(position);
            handler.sendEmptyMessage(WHAT_PHOTO);
        }
    };
    private RecyclerView videoRV;
    private List<FileModel> videoList;
    private PhotoRecycleAdapter videoAdapter;
    private PhotoRecycleAdapter.OnRecycleViewItemClickListener videoRVICL = new PhotoRecycleAdapter.OnRecycleViewItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            Intent intent = new Intent(PointEditActivity.this, ShowPhotoVideoActivity.class);
            intent.putExtra("isfinish", false);
            intent.putExtra("data", (Serializable) videoList.get(position));
            startActivity(intent);
        }

        @Override
        public void DeleteItemClick(View view, int position) {
            videoList.remove(position);
            handler.sendEmptyMessage(WHAT_VIDEO);
        }
    };
    private EditText remarksTextView;
    private ImageView add_photo;
    private ImageView add_video;
    private Button cancelBtn;
    private Button submitBtn;
    private DataUtil dataUtil;
    private DBUtil dbUtil;
    private WorkPojo workPojo;
    private TrainInfoModel trainInfoModel;
    private boolean isEdit = false;

    private TextView itemTrainInfoTV;
    private ImageView itemTrainInfoIV;
    private LinearLayout itemTrainInfoLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_edit);
        Intent intent = getIntent();
        workPojo = (WorkPojo) intent.getSerializableExtra("work");
        dataUtil = DataUtil.getInstance();
        WorkItemPojo workItemPojoOne = (WorkItemPojo) intent.getSerializableExtra("pointData");
        String toolbarTitle = "添加问题信息";
        if (workItemPojoOne != null) {
            workItemPojo = workItemPojoOne;
            isEdit = true;
            toolbarTitle = "修改问题信息";
            photoList = workItemPojo.getPhotos() == null ? new ArrayList<FileModel>() : workItemPojo.getPhotos();
            videoList = workItemPojo.getVideos() == null ? new ArrayList<FileModel>() : workItemPojo.getVideos();
        } else {
            UserModel userModel = dataUtil.getUser();
            workItemPojo = new WorkItemPojo(userModel);
            workItemPojo.setItemId(DataUtil.getOnlyId());
            workItemPojo.setWorkId(workPojo.getWorkId());
            photoList = new ArrayList<>();
            photoList.clear();
            videoList = new ArrayList<>();
            videoList.clear();
        }
        if (DataUtil.WORK_TYPE_TC.equals(workPojo.getWorkType()) || DataUtil.WORK_TYPE_SMALL_TC.equals(workPojo.getWorkType())) {
            trainInfoModel = workPojo.getTrainInfo() != null ? workPojo.getTrainInfo() : new TrainInfoModel();
            isTC = true;
        } else {
            trainInfoModel = workItemPojo.getTrainInfoModel() != null ? workItemPojo.getTrainInfoModel() : new TrainInfoModel();
            isTC = false;
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(toolbarTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        initView();
        initPhotoRecycleView();
        initVideoRecycleView();
        if (DataUtil.WORK_TYPE_TC.equals(workPojo.getWorkType()) || DataUtil.WORK_TYPE_SMALL_TC.equals(workPojo.getWorkType())) {
            otherLL.setVisibility(View.VISIBLE);
            if (workPojo.getTrainInfo() != null && !TextUtils.isEmpty(workPojo.getTrainInfo().getTrainTypeId())) {
                itemTrainInfoLL.setVisibility(View.GONE);
            } else {
                itemTrainInfoLL.setVisibility(View.VISIBLE);
            }
        } else {
            otherLL.setVisibility(View.VISIBLE);
            itemTrainInfoLL.setVisibility(View.VISIBLE);
        }
        setDateForView();
        setDriverData(trainInfoModel);
    }

    private void initData() {
        fileUtil = FileUtil.getInstance();
        dbUtil = DBUtil.getInstance();
        personModelMap = dataUtil.getDriverMap();
        photoObserverableUtil = PhotoObserverableUtil.getInstance();
        photoObserverableUtil.registerObserver(this);
        pointList = dataUtil.getSelfPointList(true);
        pointModels = new ArrayList<>();
        pointModels.addAll(pointList);
        pointOtherList = dataUtil.getNotSelfPointList();
        pointOtherModels = new ArrayList<>();
        pointOtherModels.addAll(pointOtherList);
        pointAutoTextViewAdapter = new PointAutoTextViewAdapter(pointModels, this);
        otherAdapter = new PointAutoTextViewAdapter(pointOtherModels, this);
        unitModelList = dataUtil.getUnitList();
        trainTypeModelMap = dataUtil.getTrainTypeMap();
    }

    private void initView() {
        otherLinearLayout = (LinearLayout) findViewById(R.id.other_type_layout);
        submitBtn = (Button) findViewById(R.id.point_edit_submit_btn);
        submitBtn.setOnClickListener(this);
        pointTypeIdAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.point_edit_point_id);
        pointTypeIdAutoCompleteTextView.setAdapter(pointAutoTextViewAdapter);
        pointTypeIdAutoCompleteTextView.setDropDownHeight(400);
        pointTypeIdAutoCompleteTextView.setOnItemClickListener(pointOnItemClickListener);
        pointTypeIdAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        pointTypeIdAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
        otherTextView = (AutoCompleteTextView) findViewById(R.id.point_edit_point_id_other);
        otherTextView.setAdapter(otherAdapter);
        otherTextView.setDropDownHeight(400);
        otherTextView.setOnItemClickListener(pointOtherOnItemClickListener);
        otherTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                view.showDropDown();
            }
        });
        otherTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
        pointSupplementTextView = (EditText) findViewById(R.id.point_edit_point_supplement);
        peoplesTextView = (TextView) findViewById(R.id.point_edit_people_ids);
        peopleImageView = (ImageView) findViewById(R.id.point_edit_add_people_btn);
        peopleImageView.setOnClickListener(this);
        peopleUnitAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.point_edit_unit_ids);
        unitAdapter = new SearchUnitAutoTextViewAdapter(unitModelList, this);
        peopleUnitAutoCompleteTextView.setAdapter(unitAdapter);
        peopleUnitAutoCompleteTextView.setOnItemClickListener(unitOnItemClickListener);
        peopleUnitAutoCompleteTextView.setDropDownHeight(400);
        peopleUnitAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });
        photoRV = (RecyclerView) findViewById(R.id.point_edit_photo_path_rv_list_view);
        videoRV = (RecyclerView) findViewById(R.id.point_edit_video_path_rv_list_view);
        remarksTextView = (EditText) findViewById(R.id.point_edit_remarks);
        cancelBtn = (Button) findViewById(R.id.point_edit_cancel_btn);
        cancelBtn.setOnClickListener(this);
        submitBtn = (Button) findViewById(R.id.point_edit_submit_btn);
        submitBtn.setOnClickListener(this);
        add_photo = (ImageView) findViewById(R.id.point_edit_add_photo);
        add_photo.setOnClickListener(this);
        add_video = (ImageView) findViewById(R.id.point_edit_add_video);
        add_video.setOnClickListener(this);
        /**
         * 添乘时定位到司机 2019-10-22
         */
        otherLL = findViewById(R.id.point_edit_other_ll);
        sjTV = findViewById(R.id.point_edit_people_ids_sj);
        fsjTV = findViewById(R.id.point_edit_people_ids_fsj);
        addSJIV = findViewById(R.id.point_edit_sj_add_people_btn);
        addFSJIV = findViewById(R.id.point_edit_fsj_add_people_btn);
        sjZrrIV = findViewById(R.id.point_edit_people_ids_sj_zrr);
        fsjZrrIV = findViewById(R.id.point_edit_people_ids_fsj_zrr);
        addSJIV.setOnClickListener(this);
        addFSJIV.setOnClickListener(this);

        /**
         * 添加机车信息2019-10-22
         */
        itemTrainInfoTV = findViewById(R.id.point_edit_item_train_info_ids);
        itemTrainInfoIV = findViewById(R.id.point_edit_add_item_train_info_btn);
        itemTrainInfoLL = findViewById(R.id.point_edit_item_train_info_ll);
        itemTrainInfoIV.setOnClickListener(this);
    }

    private void initPhotoRecycleView() {
        photoRV.setHasFixedSize(true);
        photoRV.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        photoRV.setLayoutManager(manager);
        photoAdapter = new PhotoRecycleAdapter(photoList, this, R.layout.item_photo_recycle_layout);
        photoAdapter.setFinish(false);
        photoAdapter.setOnItemClickListener(photoRVICL);
        photoRV.setAdapter(photoAdapter);
    }

    private void initVideoRecycleView() {
        videoRV.setHasFixedSize(true);
        videoRV.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        videoRV.setLayoutManager(linearLayoutManager);
        videoAdapter = new PhotoRecycleAdapter(videoList, this, R.layout.item_photo_recycle_layout);
        videoAdapter.setFinish(false);
        videoAdapter.setOnItemClickListener(videoRVICL);
        videoRV.setAdapter(videoAdapter);
    }

    private void setDateForView() {
        if (isEdit) {
            pointTypeIdAutoCompleteTextView.setText(workItemPojo.getPointContent());
            pointTypeIdAutoCompleteTextView.setSelection(workItemPojo.getPointContent().length());
            pointSupplementTextView.setText(workItemPojo.getRemarks());
            peoplesTextView.setText(workItemPojo.getShowPeoples());
            peopleUnitAutoCompleteTextView.setText(workItemPojo.getUnitName());
            peopleUnitAutoCompleteTextView.setSelection(workItemPojo.getUnitName().length());
            photoList = workItemPojo.getPhotos();
            videoList = workItemPojo.getVideos();
            remarksTextView.setText(workItemPojo.getRemarks());
        }
    }

    private AdapterView.OnItemClickListener pointOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PointItemModel pointMode = pointModels.get(position);
            if (DataUtil.POINT_NO_ZR.equals(pointMode.getItemTypeName())) {
                Message message = new Message();
                message.what = IS_OTHER_AUTO_VIEW;
                handler.sendMessage(message);
            } else {
                workItemPojo.setPointId(pointMode.getItemTypeId());
                String str =TextUtils.isEmpty(pointMode.getItemNumber())?pointMode.getItemTypeName(): pointMode.getItemNumber() + "." + pointMode.getItemTypeName();
                workItemPojo.setPointContent(str);
                workItemPojo.setIsSelf(pointMode.getIsSelf());
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("data", str);
                message.setData(bundle);
                message.what = NOT_OTHER_AUTO_VIEW;
                handler.sendMessage(message);
            }
        }
    };

    private AdapterView.OnItemClickListener pointOtherOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            PointItemModel pointMode = pointOtherModels.get(position);
            workItemPojo.setPointId(pointMode.getItemTypeId());
            String str = pointMode.getItemNumber() + "." + pointMode.getItemTypeName();
            workItemPojo.setPointContent(str);
            workItemPojo.setIsSelf(pointMode.getIsSelf());
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("data", str);
            message.setData(bundle);
            message.what = OTHER_AUTO_VIEW;
            handler.sendMessage(message);
        }
    };

    private AdapterView.OnItemClickListener unitOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            UnitModel unitModel = unitModelList.get(position);
            workItemPojo.setUnitId(unitModel.getgId());
            workItemPojo.setUnitName(unitModel.getgName());
        }
    };

    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.point_edit_submit_btn:
                Submit();
                break;
            case R.id.point_edit_cancel_btn:
                Cancel();
                break;
            case R.id.point_edit_add_people_btn:
                showDialog();
                break;
            case R.id.point_edit_add_photo:
                AddPhoto();
                break;
            case R.id.point_edit_add_video:
                AddVideo();
                break;
            case R.id.point_edit_add_item_train_info_btn:
                goToTrainInfoSet();//去往机车信息设置界面
                break;
        }
    }

    public void Submit() {
        if (!Verification()) {
            Toast.makeText(this, "修改红色提示的信息", Toast.LENGTH_LONG).show();
        } else {
            final LoadingDialog loadingDialog = LoadingDialog.getInstance(PointEditActivity.this);
            loadingDialog.show();
            updateDb();
            loadingDialog.cancel();
        }
    }

    public boolean Verification() {
        boolean res = true;
        if(!DataUtil.ADD_PROBLEM_IS_CAN_NULL){
            String remark = pointSupplementTextView.getText().toString();
            if (TextUtils.isEmpty(remark)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pointSupplementTextView.setHint("请填写问题信息");
                        pointSupplementTextView.setHintTextColor(getResources().getColor(R.color.errorAccent));
                    }
                });
                res = false;
            }
        }
        if(!DataUtil.ADD_POINT_IS_CAN_NULL){
            String pointType = workItemPojo.getPointId();
            if (TextUtils.isEmpty(pointType)) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pointTypeIdAutoCompleteTextView.setHint("请选择项点类型");
                        pointTypeIdAutoCompleteTextView.setHintTextColor(getResources().getColor(R.color.errorAccent));
                    }
                });
                res = false;
            }
        }
        String pointSupplement = pointSupplementTextView.getText().toString();
        workItemPojo.setRemarks(TextUtils.isEmpty(pointSupplement)?"无":pointSupplement);
        workItemPojo.setInsertTime(DateTimeUtil.getNewDateShow());
        String unit = workItemPojo.getUnitId();
        if (TextUtils.isEmpty(unit)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    peopleUnitAutoCompleteTextView.setHint("请选择单位");
                    peopleUnitAutoCompleteTextView.setHintTextColor(getResources().getColor(R.color.errorAccent));
                }
            });
            res = false;
        } else {
            workItemPojo.setUnitId(unit);
        }
        workItemPojo.setPhotos(photoList);
        workItemPojo.setVideos(videoList);
        workItemPojo.setWorkId(workPojo.getWorkId());
        workItemPojo.setInsertTime(DateTimeUtil.getNewDateShow());
        workItemPojo.setUserId(workPojo.getUserId());
        return res;
    }

    public void Cancel() {
        finish();
    }

    public void showDialog() {
        Intent intent = new Intent();
        intent.setClass(PointEditActivity.this, DialogPeopleActivity.class);
        intent.putExtra("data", JSONObject.toJSONString(workItemPojo.getPeoples()));
        startActivityForResult(intent, 100);
    }

    public void AddPhoto() {
        Intent intent = new Intent(PointEditActivity.this, Camera2Activity.class);
        intent.putExtra(DataUtil.CAMERA_ORIENTATION, 1);
        startActivity(intent);
    }

    public void AddVideo() {
        Intent intent = new Intent(PointEditActivity.this, VideoActivity.class);
        startActivityForResult(intent, 130);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 10) {
                List<PersonModel> personModelList = (List<PersonModel>) data.getSerializableExtra("data");
                if (personModelList != null) {
                    workItemPojo.setPeoples(personModelList);
                    String peopleShow = "";
                    String unitShow = "";
                    String unitId = "";
                    for (PersonModel o : personModelList
                    ) {
                        if (!TextUtils.isEmpty(o.getPersonId())) {
                            peopleShow += o.getPersonName() + ";";
                            String unit = o.getUnitId();
                            String unitName = o.getUnitName();
                            if (unitId.indexOf(unit) == -1) {
                                unitId += unit + ",";
                                unitShow += unitName + ";";
                            }
                        }
                    }
                    workItemPojo.setShowPeoples(peopleShow);
                    workItemPojo.setUnitId(unitId);
                    workItemPojo.setUnitName(unitShow);
                } else {
                    workItemPojo.setPeoples(new ArrayList<PersonModel>());
                    workItemPojo.setUnitId("");
                    workItemPojo.setUnitName("");
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        peoplesTextView.setText(workItemPojo.getShowPeoples());
                        peopleUnitAutoCompleteTextView.setText(workItemPojo.getUnitName());
                        peopleUnitAutoCompleteTextView.setSelection(workItemPojo.getUnitName().length());
                        videoAdapter.notifyDataSetChanged();
                    }
                });
            }
        } else if (requestCode == 130) {
            SharedPreferences sharedPreferences = getSharedPreferences("video", Activity.MODE_PRIVATE);
            String fileName = sharedPreferences.getString("fileName", "");
            if (!TextUtils.isEmpty(fileName)) {
                FileModel fileModel = new FileModel();
                fileModel.setFileId(fileName);
                fileModel.setFileName(fileName);
                String name = fileName.substring(0, fileName.length() - 4) + ".jpg";
                String path = DataUtil.PHOTO_PATH + File.separator + name;
                fileModel.setFilePath(path);
                fileModel.setFileStatus(FileUtil.FILE_STATUS_NOT_CAN_UPLOADED);
                fileModel.setFileRank(FileUtil.FILE_RANK_LOWER);
                fileModel.setFileType(FileUtil.FILE_TYPE_VIDEO);
                fileModel.setItemId(workItemPojo.getItemId());
                fileModel.setWorkId(workPojo.getWorkId());
                fileModel.setUserId(workPojo.getUserId());
                fileModel.setFileTime(DateTimeUtil.getNewDateShow());
                videoList.add(0, fileModel);
                fileUtil.insertFile(fileModel);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("fileName", "");
                editor.commit();
                handler.sendEmptyMessage(WHAT_VIDEO);
            }
        } else if (requestCode == 122) {
            if (resultCode == 123) {
                trainInfoModel = (TrainInfoModel) data.getSerializableExtra("data");
                setDriverData(trainInfoModel);
            }
        }
    }

    @Override
    public void refreshView() {
        SharedPreferences sharedPreferences = getSharedPreferences("photo", Activity.MODE_PRIVATE);
        String fileName = sharedPreferences.getString("fileName", "");
        if (!TextUtils.isEmpty(fileName)) {
            FileModel fileModel = new FileModel();
            fileModel.setFileId(fileName);
            fileModel.setFileName(fileName);
            fileModel.setFilePath(DataUtil.PHOTO_PATH + File.separator + fileName);
            fileModel.setFileStatus(FileUtil.FILE_STATUS_NOT_CAN_UPLOADED);
            fileModel.setFileRank(FileUtil.FILE_RANK_LOWER);
            fileModel.setFileType(FileUtil.FILE_TYPE_PHOTO);
            fileModel.setItemId(workItemPojo.getItemId());
            fileModel.setWorkId(workPojo.getWorkId());
            fileModel.setUserId(workPojo.getUserId());
            fileModel.setFileTime(DateTimeUtil.getNewDateShow());
            photoList.add(0, fileModel);
            fileUtil.insertFile(fileModel);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("fileName", "");
            editor.commit();
            handler.sendEmptyMessage(WHAT_PHOTO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        photoObserverableUtil.removeObserver(this);
    }

    public void updateDb() {
        dataUtil.editWorkPojoList(workPojo);
        if (isTC) {
            workPojo.setTrainInfo(trainInfoModel);
        } else {
            workItemPojo.setTrainInfoModel(trainInfoModel);
        }
        if (isEdit) {
            WorkItemModel workItemModel = new WorkItemModel(workItemPojo);
            dbUtil.update(DataUtil.TableNameEnum.ITEM.toString(), workItemModel.getContentValues(workItemModel, true), " itemid=? ", new String[]{workItemPojo.getItemId()});
            dataUtil.editPointDetail(workItemPojo);
        } else {
            WorkItemModel workItemModel = new WorkItemModel(workItemPojo);
            dbUtil.insert(DataUtil.TableNameEnum.ITEM.toString(), workItemModel.getContentValues(workItemModel, false));
            dataUtil.addPointDetail(workItemPojo);
        }
        WorkModel workModel = new WorkModel(workPojo);
        dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
        Intent intent = new Intent();
        intent.putExtra("work", workPojo);
        intent.putExtra("pointData", workItemPojo);
        setResult(isEdit ? 81 : 80, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 通过机车信息给问题人员赋值
     */
    public void setDriverData(TrainInfoModel trainInfoModel) {
        String driverId = trainInfoModel.getDriverId();
        String assistantDriverId = trainInfoModel.getAssistantDriverId();
        List<PersonModel> personList = workItemPojo.getPeoples();
        if (TextUtils.isEmpty(driverId)) {
            isSjZrr = false;
        } else {
            isSjZrr = true;
            PersonModel driver = personModelMap.get(driverId);
            if (driver != null) {
                boolean isHas = false;
                for (PersonModel o : personList
                ) {
                    if (o.getPersonId().equals(driverId)) {
                        isHas = true;
                        break;
                    }
                }
                if (!isHas)
                    personList.add(driver);
            }
        }
        if (TextUtils.isEmpty(assistantDriverId)) {
            isFsjZrr = false;
        } else {
            isFsjZrr = true;
            PersonModel fdriver = personModelMap.get(assistantDriverId);
            if (fdriver != null) {
                boolean isHas = false;
                for (PersonModel o : personList
                ) {
                    if (o.getPersonId().equals(assistantDriverId)) {
                        isHas = true;
                        break;
                    }
                }
                if (!isHas)
                    personList.add(fdriver);
            }
        }
        workItemPojo.setPeoples(personList);
        workItemPojo.setShowPeoples(workItemPojo.getShowPeopleByPeoples());
        StringBuffer selectedDriverId = new StringBuffer();
        for (int i = 0, num = personList.size(); i < num; i++) {
            selectedDriverId.append(personList.get(i).getPersonId());
            if (i < num - 1)
                selectedDriverId.append(",");
        }
        setUnit(selectedDriverId.toString());
    }

    /**
     * 通过司机信息给单位赋值
     *
     * @param driverIds
     */
    public void setUnit(String driverIds) {
        String[] arr = driverIds.split(",");
        String unitId = "";
        String unitName = "";
        if (arr != null && arr.length > 0) {
            for (int i = 0; i < arr.length; i++) {
                PersonModel personModel = dataUtil.getDriverMap().get(arr[i]);
                if (personModel != null && unitId.indexOf(personModel.getUnitId()) == -1) {
                    unitId += personModel.getUnitId();
                    unitName += personModel.getUnitName();
                }
                if (i < arr.length - 1) {
                    unitId += ",";
                    unitName += ";";
                }
            }
        }
        workItemPojo.setUnitId(unitId);
        workItemPojo.setUnitName(unitName);
        handler.sendEmptyMessage(SET_TRAIN_INFO);
    }

    public void goToTrainInfoSet() {
        Intent intent = new Intent();
        intent.setClass(PointEditActivity.this, SetTrainInfoActivity.class);
        intent.putExtra("data", trainInfoModel);
        startActivityForResult(intent, 122);
    }

}
