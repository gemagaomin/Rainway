package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.SearchPointItemAdapter;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.PointItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SearchPointActivity extends BaseActivity {
    private TextView selectNumTV;
    private EditText searchET;
    private ListView listView;
    private List<PointItemPojo> showList;
    private List<PointItemPojo> showListTemp;
    private SearchPointItemAdapter adapter;
    private WorkPojo workPojo;
    private DataUtil dataUtil;
    private DBUtil dbUtil;
    private String selectStr;
    private int selectNum=0;
    private static final int CHANGE_DATA=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            switch (what){
                case CHANGE_DATA:
                    selectNumTV.setText(selectNum+"");
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_point);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("添加检查项点");
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
        Intent intent=getIntent();
        workPojo=(WorkPojo) intent.getSerializableExtra("data");
        selectStr=workPojo.getPlanWorkItemsStr();
        selectNum=workPojo.getPlanWorkItems()!=null?workPojo.getPlanWorkItems().size():0;
        dataUtil=DataUtil.getInstance();
        dbUtil=DBUtil.getInstance();
        showListTemp=new ArrayList<>();
        showList=new ArrayList<>();
        showListTemp.addAll(PointItemPojo.getList(dataUtil.getSelfPointList(false),selectStr));
        showList.addAll(showListTemp);
        adapter=new SearchPointItemAdapter(showList,this);
    }

    public void initView(){
        selectNumTV=findViewById(R.id.search_pointitem_num);
        selectNumTV.setText(selectNum+"");
        searchET=findViewById(R.id.search_point_ev);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeData(null);
            }
        });
        listView=findViewById(R.id.search_point_lv);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PointItemPojo pointItemPojo=showList.get(position);
                boolean select=!pointItemPojo.isSelect();
                pointItemPojo.setSelect(select);
                changeData(pointItemPojo);
                return true;
            }
        });
    }

    public void changeData(PointItemPojo pointItemPojo){
        String searchKey=searchET.getText().toString();
        boolean isChange=!TextUtils.isEmpty(searchKey);
        int keyLength=searchKey.length();
        List<PointItemPojo> pointItemPojos=workPojo.getPlanWorkItems()==null?new ArrayList<PointItemPojo>():workPojo.getPlanWorkItems();
        selectStr="";
        if(showList!=null)
            showList.clear();
        if(pointItemPojo!=null){
            boolean isSelect=pointItemPojo.isSelect();
            for (int i=0,num=showListTemp.size();i<num;i++) {
                PointItemPojo p=showListTemp.get(i);
                if(p.getItemTypeId().equals(pointItemPojo.getItemTypeId())){
                    p.setSelect(isSelect);
                    showListTemp.set(i,p);
                    break;
                }
            }
            if(isSelect){
                pointItemPojos.add(pointItemPojo);
            }else{
                int changeNum=-1;
                for (int j=0,numj=pointItemPojos.size();j<numj;j++) {
                    PointItemPojo p=pointItemPojos.get(j);
                    String id=p.getItemTypeId();
                    if(id.equals(pointItemPojo.getItemTypeId())){
                        changeNum=j;
                        break;
                    }
                }
                pointItemPojos.remove(changeNum);
            }
            for (int j=0,numj=pointItemPojos.size();j<numj;j++) {
                PointItemPojo p=pointItemPojos.get(j);
                String id=p.getItemTypeId();
                selectStr+=id;
                if(j<numj-1){
                    selectStr+=",";
                }
            }
        }
        if(isChange) {
            for (PointItemPojo p:showListTemp) {
                String value = p.getItemTypeName();
                if (!TextUtils.isEmpty(value) && value.length() >= keyLength && searchKey.equals(value.substring(0, keyLength)))
                    showList.add(p);
            }
        }else{
            showList.addAll(showListTemp);
        }
        selectNum=pointItemPojos.size();
        workPojo.setPlanWorkItems(pointItemPojos);
        workPojo.setPlanWorkItemsStr(selectStr);
        dataUtil.editWorkPojoListNoRefresh(workPojo);
        WorkModel workModel = new WorkModel(workPojo);
        dbUtil.update(DataUtil.TableNameEnum.WORK.toString(), workModel.getContentValues(workModel), " workid=? ", new String[]{workPojo.getWorkId()});
        handler.sendEmptyMessage(CHANGE_DATA);
    }

    @Override
    public void onNoDoubleClick(View v) {
        int id=v.getId();
        switch (id){

        }
    }

    public void close(){
        Intent intent=new Intent();
        intent.putExtra("data",workPojo);
        setResult(900,intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        close();
    }
}
