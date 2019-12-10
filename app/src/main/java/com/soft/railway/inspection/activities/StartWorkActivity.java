package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PointItemModel;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StartWorkActivity extends BaseActivity implements Observer {
    private WorkPojo workPojo;
    private TextView workTypeTextView;
    private TextView workPlaceTextView;
    private ImageView photoImageView;
    private Button workSubmitBtn;
    private Button workCancelBtn;
    private Uri uri;
    private String fileName;
    private ImageView photographImageView;
    private PhotoObserverableUtil photoObserverableUtil;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            switch (what){
                case 1:
                    photoImageView.setImageURI(uri);
                    photoImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                case 2:
                    new AlertDialog.Builder(StartWorkActivity.this).setTitle("操作提示").setMessage("工作开启成功！")
                            .setCancelable(false).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(StartWorkActivity.this, RunningWorkActivity.class);
                            intent.putExtra("data",workPojo);
                            startActivity(intent);
                            finish();
                        }
                    }).show();
                    break;
            }
        }
    };
    private DBUtil dbUtil;
    private DataUtil dataUtil;
    private boolean ifCanStart;
    private Toolbar toolbar;
    private Button addPointEditBtn;
    private ListView pointLV;
    private List<WorkItemPojo> workItemList;
    private List<PointItemModel> pointItemModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start_work);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("开始检查");
        setSupportActionBar(toolbar);
        photoObserverableUtil=PhotoObserverableUtil.getInstance();
        photoObserverableUtil.registerObserver(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        workPojo=(WorkPojo)intent.getSerializableExtra("data");
        initData();
        initView();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        getMenuInflater().inflate(R.menu.right_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.right_menu_delete_work){
            showDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(){
        new AlertDialog.Builder(StartWorkActivity.this).setTitle("提示")
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

    public void initData(){
        dbUtil = DBUtil.getInstance();
        dataUtil=DataUtil.getInstance();
        ifCanStart=isIfCanStart();
    }

    private void initView(){
        workTypeTextView=(TextView)findViewById(R.id.start_work_type);
        workTypeTextView.setText(DataUtil.WorkTypeMap.get(workPojo.getWorkType()));
        workPlaceTextView=(TextView)findViewById(R.id.start_wrok_place);
        workPlaceTextView.setText(workPojo.getPlace());
        workCancelBtn=(Button) findViewById(R.id.start_work_cancel);
        workCancelBtn.setOnClickListener(this);
        workSubmitBtn=(Button) findViewById(R.id.start_work_submit_btn);
        workSubmitBtn.setOnClickListener(this);
        photoImageView=(ImageView) findViewById(R.id.start_work_show_photo);
        photographImageView=(ImageView) findViewById(R.id.start_work_photograph_btn);
        photographImageView.setOnClickListener(this);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.start_work_cancel:
                finish();
                break;
            case R.id.start_work_photograph_btn:
                Intent intent=new Intent(StartWorkActivity.this,Camera2Activity.class);
                intent.putExtra(DataUtil.CAMERA_ORIENTATION,0);
                startActivity(intent);
                break;
            case R.id.start_work_submit_btn:
                if(ifCanStart){
                    SubmitData();
                }else{
                    Toast.makeText(getApplicationContext(),"完成已经开始的工作，才可以开始新的工作",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void SubmitData(){
        if(!Verification()){
            Toast.makeText(this,"拍照后才可以开始检查",Toast.LENGTH_LONG).show();
        }else {
            final LoadingDialog loadingDialog =LoadingDialog.getInstance(StartWorkActivity.this);
            loadingDialog.show(StartWorkActivity.this);
            final Map<String, String> map = new HashMap<>();
            workPojo.setWorkStatus(DataUtil.WORK_STATUS_RUNNING + "");
            dataUtil.editWorkPojoList(workPojo);
            WorkModel workModel=new WorkModel(workPojo);
            dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
            handler.sendEmptyMessage(2);
            loadingDialog.dismiss();
        }
    }

    public boolean isIfCanStart(){
        List<WorkPojo> list=dataUtil.getWorkPojoList();
        if (!list.isEmpty()){
            for (WorkPojo o:list
                 ) {
                if(DataUtil.WORK_STATUS_RUNNING==Integer.valueOf(o.getWorkStatus())){
                    return false;
                }
            }
        }
        return true;
    }

    public void deleteWork(){
        dataUtil.removeWorkPojoList(workPojo);
        dbUtil.delete(DataUtil.TableNameEnum.ITEM.toString()," workid=? ",new String[]{workPojo.getWorkId()});
        dbUtil.delete(DataUtil.TableNameEnum.WORK.toString()," workid=? ",new String[]{workPojo.getWorkId()});
        dbUtil.delete(DataUtil.TableNameEnum.SUBMITFILE.toString()," workid=? ",new String[]{workPojo.getWorkId()});
        finish();
    }

    @Override
    public void refreshView() {
        if(workPojo.getCapturePhotos()==null||workPojo.getCapturePhotos().size()==0){
            SharedPreferences sharedPreferences=getSharedPreferences("selfphoto", Activity.MODE_PRIVATE);
            fileName=sharedPreferences.getString("fileName","");
            if(!TextUtils.isEmpty(fileName)){
                FileUtil fileUtil=FileUtil.getInstance();
                File file=new File(DataUtil.PHOTO_PATH+File.separator+fileName);
                FileModel fileModel =new FileModel();
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
                List<FileModel> fileModels=new ArrayList<>();
                fileModels.add(fileModel);
                workPojo.setCapturePhotos(fileModels);
                fileUtil.insertFile(fileModel);
                uri= FileProvider.getUriForFile(this,"com.soft.railway.inspection.fileProvider",file);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("fileName","");
                editor.commit();
                handler.sendEmptyMessage(1);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoObserverableUtil.removeObserver(this);
    }

    public boolean Verification(){
        boolean ret=true;
        if(workPojo.getCapturePhotos()==null||workPojo.getCapturePhotos().size()==0){
            ret= false;
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
