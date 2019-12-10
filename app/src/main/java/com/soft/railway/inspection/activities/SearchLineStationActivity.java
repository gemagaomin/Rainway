package com.soft.railway.inspection.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.SearchLineAdapter;
import com.soft.railway.inspection.adapters.SearchStationAdapter;
import com.soft.railway.inspection.models.LineModel;
import com.soft.railway.inspection.models.StationModel;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.Log;
import com.soft.railway.inspection.utils.MyApplication;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchLineStationActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private static final int WHAT_LINE_TEXT_VIEW=0;
    private static final int WHAT_START_STATION_TEXT_VIEW=1;
    private static final int WHAT_END_STATION_TEXT_VIEW=2;
    private EditText lineEditText;
    private ListView lineListView;
    private List<LineModel> lineModelList;
    private List<LineModel> showLineModelList=new ArrayList<LineModel>();
    private SearchLineAdapter lineSearchAdapter;
    private Map paramsMap=new HashMap();
    private Boolean ifStartStation=true;
    private EditText startStationEditText;
    private EditText endStationEditText;
    private ListView stationListView;
    private List<StationModel> stationModelList;
    private List<StationModel> showStationModelList=new ArrayList<StationModel>();
    private SearchStationAdapter searchStationAdapter;
    private Button button;
    private String SelectLineId="";
    private boolean isXC=false;
    private LinearLayout linearLayout;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            Bundle bundle=msg.getData();
            String showMsg=bundle.getString("data");
            switch (what){
                case WHAT_LINE_TEXT_VIEW:
                    if(!TextUtils.isEmpty(showMsg)){
                        lineEditText.setText(showMsg);
                        lineEditText.setSelection(showMsg.length());
                    }
                    lineSearchAdapter.notifyDataSetChanged();
                    break;
                case WHAT_START_STATION_TEXT_VIEW:
                    if(!TextUtils.isEmpty(showMsg)){
                        startStationEditText.setText(showMsg);
                        startStationEditText.setSelection(showMsg.length());
                    }
                    searchStationAdapter.notifyDataSetChanged();
                    break;
                case WHAT_END_STATION_TEXT_VIEW:
                    if(!TextUtils.isEmpty(showMsg)){
                        endStationEditText.setText(showMsg);
                        endStationEditText.setSelection(showMsg.length());
                    }
                    searchStationAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private DataUtil dataUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_line_station);
        Intent intent=getIntent();
        paramsMap= (Map) intent.getSerializableExtra("params");
        String xc=intent.getStringExtra("xc");
        if(!TextUtils.isEmpty(xc)){
            isXC=true;
        }
        Toolbar toolbar=(Toolbar)findViewById(R.id.search_toolbar);
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
        initShow();
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.search_sure_btn:
                MyApplication.hideSoftKeyboard(SearchLineStationActivity.this);
                String line=lineEditText.getText().toString();
                String startStation=startStationEditText.getText().toString();
                String endStation=endStationEditText.getText().toString();
                Boolean ifCanSubmit=false;
                if(TextUtils.isEmpty(line)){
                    Toast.makeText(getApplicationContext(),"线路不能为空",Toast.LENGTH_LONG).show();
                }else{
                    boolean ifTrue=false;
                    for (LineModel o:lineModelList
                    ) {
                        String name=o.getLineName();
                        if(line.equals(name)){
                            SelectLineId=o.getLineId();
                            paramsMap.put("lineId",SelectLineId);
                            paramsMap.put("lineName",name);
                            ifTrue=true;
                            break;
                        }
                    }
                    if(ifTrue){
                        if(TextUtils.isEmpty(startStation)){
                            Toast.makeText(getApplicationContext(),"请选择开始站",Toast.LENGTH_LONG).show();
                        }else{
                            ifTrue=false;
                            for(StationModel o:stationModelList){
                                String name=o.getStationName();
                                String lineId=o.getLineId();
                                if(lineId.indexOf(SelectLineId)!=-1&&startStation.equals(name)){
                                    paramsMap.put("startStationId",o.getStationId());
                                    paramsMap.put("startStationName",o.getStationName());
                                    ifTrue=true;
                                    break;
                                }
                            }
                            if(ifTrue){
                                if(!TextUtils.isEmpty(endStation)){
                                    ifTrue=false;
                                    for(StationModel o:stationModelList){
                                        String name=o.getStationName();
                                        String lineId=o.getLineId();
                                        if(lineId.indexOf(SelectLineId)!=-1&&endStation.equals(name)){
                                            paramsMap.put("endStationId",o.getStationId());
                                            paramsMap.put("endStationName",o.getStationName());
                                            ifTrue=true;
                                            break;
                                        }
                                    }
                                    if(ifTrue){
                                        ifCanSubmit=true;
                                    }else{
                                        Toast.makeText(getApplicationContext(),"请选择结束站",Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    paramsMap.put("endStationId","");
                                    paramsMap.put("endStationName","");
                                    ifCanSubmit=true;
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"请选择开始站",Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"请选择线路",Toast.LENGTH_LONG).show();
                    }
                }
                if(ifCanSubmit){
                    Intent intent=new Intent();
                    List<Activity> list=MyApplication.list;
                    if(list!=null&&list.size()>1){
                        Activity activity=list.get(list.size()-1);
                        intent.setClass(SearchLineStationActivity.this,activity.getClass());
                        intent.putExtra("params",(Serializable) paramsMap);
                        setResult(1000,intent);
                        finish();
                    }

                }
                break;
        }
    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        lineModelList=dataUtil.getLineList();
        showLineModelList.addAll(dataUtil.getLineList());
        stationModelList=dataUtil.getStationList();
        showStationModelList.addAll(dataUtil.getStationList());
    }

    public void initView(){
        lineEditText=(EditText) findViewById(R.id.search_line_text_view);
        lineEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showLineListView();
                refreshShowLineListView(lineEditText.getText().toString(),WHAT_LINE_TEXT_VIEW,null);
                return false;
            }
        });
        lineEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("TextWatcher beforeTextChanged"+s+"  "+start+"  "+count+"  "+after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TextWatcher onTextChanged"+s+"  "+start+"  "+before+"  "+count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshShowLineListView(s.toString(),WHAT_LINE_TEXT_VIEW,null);
                Log.d("TextWatcher afterTextChanged"+s.toString());
            }
        });
        lineListView=(ListView) findViewById(R.id.search_line_list_view);
        lineSearchAdapter=new SearchLineAdapter(showLineModelList,getApplicationContext());
        lineListView.setAdapter(lineSearchAdapter);
        lineListView.setOnItemClickListener(this);
        stationListView=(ListView) findViewById(R.id.search_station_list_view);
        searchStationAdapter=new SearchStationAdapter(showStationModelList,getApplicationContext());
        stationListView.setAdapter(searchStationAdapter);
        stationListView.setOnItemClickListener(this);
        startStationEditText=(EditText) findViewById(R.id.search_start_station_text_view);
        startStationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ifStartStation=true;
                showStationListView();
                refreshStationView(startStationEditText.getText().toString(),SelectLineId,WHAT_START_STATION_TEXT_VIEW,null);
                return false;
            }
        });
        startStationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChanged12"+s.toString());
                refreshStationView(s.toString(),SelectLineId,WHAT_START_STATION_TEXT_VIEW,null);
            }
        });
        endStationEditText=(EditText) findViewById(R.id.search_end_station_text_view);
        linearLayout=(LinearLayout) findViewById(R.id.search_end_station_linear_layout);
        if(isXC){
            linearLayout.setVisibility(View.GONE);
        }else{
            linearLayout.setVisibility(View.VISIBLE);
        }
        endStationEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ifStartStation=false;
                showStationListView();
                refreshStationView(endStationEditText.getText().toString(),SelectLineId,WHAT_END_STATION_TEXT_VIEW,null);
                return false;
            }
        });
        endStationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshStationView(s.toString(),SelectLineId,WHAT_END_STATION_TEXT_VIEW,null);
            }
        });
        button=(Button) findViewById(R.id.search_sure_btn);
        button.setOnClickListener(this);
    }

    public void initShow(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(isXC){
                    String line=(String)paramsMap.get("lineName");
                    String startStation=(String)paramsMap.get("stationName");
                    if(!TextUtils.isEmpty(line)){
                        lineEditText.setText(line);
                        SelectLineId=(String) paramsMap.get("lineId");
                        lineEditText.setSelection(line.length());
                    }

                    if(!TextUtils.isEmpty(startStation)){
                        startStationEditText.setText(startStation);
                        startStationEditText.setSelection(startStation.length());
                    }
                }else{
                    String line=(String)paramsMap.get("lineName");
                    String startStation=(String)paramsMap.get("startStationName");
                    String endStation=(String)paramsMap.get("endStationName");
                    if(!TextUtils.isEmpty(line)){
                        lineEditText.setText(line);
                        SelectLineId=(String) paramsMap.get("lineId");
                        lineEditText.setSelection(line.length());
                    }

                    if(!TextUtils.isEmpty(startStation)){
                        startStationEditText.setText(startStation);
                        startStationEditText.setSelection(startStation.length());
                    }

                    if(!TextUtils.isEmpty(endStation)){
                        endStationEditText.setText(endStation);
                        endStationEditText.setSelection(endStation.length());
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int viewId=parent.getId();
        switch (viewId){
            case R.id.search_line_list_view:
                    LineModel lineModel=showLineModelList.get(position);
                    paramsMap.put("lineId",lineModel.getLineId());
                    paramsMap.put("lineName",lineModel.getLineName());
                    refreshShowLineListView(lineModel.getLineName(),WHAT_LINE_TEXT_VIEW,lineModel);
                break;
            case R.id.search_station_list_view:
                StationModel stationModel=showStationModelList.get(position);
                int textView=WHAT_START_STATION_TEXT_VIEW;
                if(!ifStartStation){
                    paramsMap.put("endStationId",stationModel.getStationId());
                    paramsMap.put("endStationName",stationModel.getStationName());
                    textView=WHAT_END_STATION_TEXT_VIEW;
                }else{
                    paramsMap.put("startStationId",stationModel.getStationId());
                    paramsMap.put("startStationName",stationModel.getStationName());
                }
                refreshStationView(stationModel.getStationName(),SelectLineId,textView,stationModel);
                break;
        }
    }

    public void changeListView(int what,String showMessage){
        Message message=new Message();
        message.what=what;
        Bundle bundle=new Bundle();
        bundle.putString("data",showMessage);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    public void refreshShowLineListView(String search,int what,LineModel lineModel){
        showLineModelList.clear();
        String showMsg="";
        if(TextUtils.isEmpty(search)){
            showLineModelList.addAll(lineModelList);
        }else{
            for (LineModel o:lineModelList
                 ) {
                String name=o.getLineName().length()>=search.length()?o.getLineName().substring(0,search.length()):o.getLineName();
                if(search.equals(name)){
                    showLineModelList.add(o);
                }
            }
        }
        if(lineModel!=null){
            SelectLineId=lineModel.getLineId();
            showMsg=lineModel.getLineName();
        }
        changeListView(what,showMsg);
    }

    public void refreshStationView(String search,String lineId,int what,StationModel stationModel){
        showStationModelList.clear();
        List<StationModel> tempList=new ArrayList<StationModel>();
        for (StationModel o:stationModelList
             ) {
            String lineIds=o.getLineId();
            if(!TextUtils.isEmpty(lineId)&&lineIds.indexOf(lineId)==-1){
                continue;
            }
            tempList.add(o);
        }
        if(TextUtils.isEmpty(search)){
            showStationModelList.addAll(tempList);
        }else{
            for (StationModel o:tempList
            ) {
                if(o.getStationName().length()>=search.length()){
                    String name=o.getStationName().substring(0,search.length());
                    if(search.equals(name)){
                        showStationModelList.add(o);
                    }
                }
            }
        }
        String showMsg="";
        if(stationModel!=null){
            showMsg=stationModel.getStationName();
        }
        changeListView(what,showMsg);
    }

    public void showLineListView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                lineListView.setVisibility(View.VISIBLE);
                stationListView.setVisibility(View.GONE);
            }
        });
    }

    public void showStationListView(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                lineListView.setVisibility(View.GONE);
                stationListView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
