package com.soft.railway.inspection.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.adapters.DriverAdapter;
import com.soft.railway.inspection.models.PersonModel;
import com.soft.railway.inspection.utils.DataUtil;
import java.util.List;

public class SearchDriverActivity extends BaseActivity {
    private EditText editText;
    private ListView driverLV;
    private List<PersonModel> driverShowList;
    private List<PersonModel> driverModelList;
    private DriverAdapter driverAdapter;
    private DataUtil dataUtil;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            driverAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("查找人员信息");
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

    @Override
    public void onNoDoubleClick(View v) {

    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        Intent intent=getIntent();
        String band=intent.getStringExtra("band");
        driverModelList=dataUtil.getDriverList();
        driverShowList= PersonModel.getPersonListByBand(band,driverModelList);
        driverAdapter=new DriverAdapter(driverShowList,SearchDriverActivity.this);
    }

    public void initView(){
        editText=(EditText) findViewById(R.id.search_driver_auto_text_view);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                refreshView(s.toString());
            }
        });
        driverLV=(ListView) findViewById(R.id.search_driver_list_view);
        driverLV.setAdapter(driverAdapter);
        driverLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonModel driverModel=driverShowList.get(position);
                Intent intent=new Intent();
                intent.putExtra("driver",driverModel);
                setResult(800,intent);
                finish();
            }
        });

    }

    public void refreshView(String str){
        driverShowList.clear();
        driverShowList.addAll(PersonModel.getPersonListByString(str,driverModelList));
        handler.sendEmptyMessage(0);
    }

}
