package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.RecordFileAdapter;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.MediaRecorderUtil;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RecordActivity extends BaseActivity {
    private DataUtil dataUtil;
    private FileUtil fileUtil;
    private DBUtil dbUtil;
    private TextView startBtn;
    private Button palyBtn;
    private Button startPalyBtn;
    private Button stopPalyBtn;
    private ImageView imageView;
    private ProgressBar progressBar;
    private boolean isRecorder = false;//在录音
    private MediaRecorderUtil mediaRecorderUtil;
    private String fileName = "";
    private Timer timer;
    private TimerTask timerTask;
    private int LONGTIME = 100;


    private ListView listView;
    private List<FileModel> fileNameList;
    private RecordFileAdapter adapter;
    private RecordFileAdapter.RecorderListener listener=new RecordFileAdapter.RecorderListener() {
        @Override
        public void onClick(View v, int position) {
            FileModel fileModel=fileNameList.get(position);
            Intent intent=new Intent(RecordActivity.this,ShowMp3Activity.class);
            intent.putExtra("data",fileModel.getFileName());
            startActivity(intent);
        }
    };

    private WorkPojo workPojo;

    public int timeNum = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 0:
                    if (isRecorder) {
                        startBtn.setText("停止");
                        startBtn.setTextColor(getResources().getColor(R.color.circleInner));
                        startBtn.setBackgroundDrawable(getDrawable(R.drawable.ic_add_circle_recorder_btn_background_red));
                        progressBar.setProgress(timeNum);
                    } else {
                        progressBar.setProgress(timeNum);
                        timeNum = 0;
                        startBtn.setText("开始");
                        startBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                        startBtn.setBackgroundDrawable(getDrawable(R.drawable.ic_add_circle_recorder_btn_background));
                    }
                    imageView.getDrawable().setLevel(0);
                    break;
                case 1:
                    double test = mediaRecorderUtil.updateMicStatus();
                    test = test * 100;
                    BigDecimal df = new BigDecimal(test);
                    test = df.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    int height = Double.valueOf(test).intValue();
                    imageView.getDrawable().setLevel(height);
                    progressBar.setProgress(timeNum);
                    break;
                case 3:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.record_toolbar);
        toolbar.setTitle("添加录音");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        initData();
        initView();

    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        fileUtil = FileUtil.getInstance();
        dbUtil=DBUtil.getInstance();
        Intent intent=getIntent();
        workPojo=(WorkPojo) intent.getSerializableExtra("data");
        if(workPojo==null){
            workPojo=new WorkPojo(dataUtil.getUser());
        }
        fileNameList = new ArrayList<>();
        if(workPojo.getRecorders()!=null&&workPojo.getRecorders().size()>0){
            fileNameList.addAll(workPojo.getRecorders());
        }
        adapter=new RecordFileAdapter(fileNameList,this);
        adapter.setMlistener(listener);
        if (mediaRecorderUtil == null) {
            mediaRecorderUtil = MediaRecorderUtil.getInstance();
        }
    }

    public void initView(){
        startBtn = findViewById(R.id.start);
        palyBtn = findViewById(R.id.paly);
        startPalyBtn = findViewById(R.id.pause_paly);
        stopPalyBtn = findViewById(R.id.stop_paly);
        imageView = findViewById(R.id.record_drawable);
        imageView.getDrawable().setLevel(0);
        startBtn.setOnClickListener(this);
        palyBtn.setOnClickListener(this);
        startPalyBtn.setOnClickListener(this);
        stopPalyBtn.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(LONGTIME);
        listView=findViewById(R.id.record_lv);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNoDoubleClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start:
                isRecorder = !isRecorder;
                if (isRecorder) {
                    fileName=new Date().getTime() + "";
                    mediaRecorderUtil.startRecording(fileName, "");
                    timer = new Timer();
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            timeNum++;
                            if (timeNum == LONGTIME) {
                                if (isRecorder) {
                                    fileName = mediaRecorderUtil.stopRecording();
                                    fileNameList.add(settingFileModel(fileName));
                                    handler.sendEmptyMessage(3);
                                }
                                isRecorder = !isRecorder;
                                clear();
                                handler.sendEmptyMessage(0);
                            } else if (timeNum < LONGTIME) {
                                handler.sendEmptyMessage(1);
                            }
                        }
                    };
                    timer.schedule(timerTask, 0, 500);
                } else {
                    clear();
                    fileName = mediaRecorderUtil.stopRecording();
                    fileNameList.add(settingFileModel(fileName));
                    handler.sendEmptyMessage(3);
                }
                handler.sendEmptyMessage(0);
                break;
        }
    }

    public void clear() {
        if (timerTask != null)
            timerTask.cancel();
        if (timer != null)
            timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRecorder && mediaRecorderUtil != null) {
            mediaRecorderUtil.stopRecording();
        }
        clear();
    }

    private FileModel settingFileModel(String name){
        int numLength=(DataUtil.RECORDER_PATH+ File.separator).length();
        int nameLength=name.length();
        if(numLength>=nameLength)
            return null;
        File file = new File( name);
        String saveName=nameLength>numLength?name.substring(numLength,nameLength):"";
        FileModel fileModel = new FileModel();
        fileModel.setFileId(saveName);
        fileModel.setFileName(saveName);
        fileModel.setFilePath(file.getAbsolutePath());
        fileModel.setFileStatus(FileUtil.FILE_STATUS_NOT_CAN_UPLOADED);
        fileModel.setFileRank(FileUtil.FILE_RANK_LOWER);
        fileModel.setFileType(FileUtil.FILE_TYPE_RECORDER);
        fileModel.setItemId("");
        fileModel.setWorkId(workPojo.getWorkId());
        fileModel.setUserId(workPojo.getUserId());
        fileModel.setFileTime(DateTimeUtil.getNewDateShow());
        fileUtil.insertFile(fileModel);
        return fileModel;
    }

    public void close(){
        if (isRecorder && mediaRecorderUtil != null) {
            mediaRecorderUtil.stopRecording();
        }
        workPojo.setRecorders(fileNameList);
        dataUtil.editWorkPojoListNoRefresh(workPojo);
        WorkModel workModel = new WorkModel(workPojo);
        dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
        Intent intent=new Intent();
        intent.putExtra("data",workPojo);
        setResult(1900,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
    }

}
