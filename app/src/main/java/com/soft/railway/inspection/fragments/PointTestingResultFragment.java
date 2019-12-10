package com.soft.railway.inspection.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.activities.PointCardDetailActivity;
import com.soft.railway.inspection.adapters.PointTestingResultRecyclerViewAdapter;
import com.soft.railway.inspection.models.PointTestingResultModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.HttpUtil;
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


public class PointTestingResultFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String url="/examine/pointtestingresult";
    private RecyclerView rl;
    private String searchTime="";

    private TextView showdateTV;
    private ImageView selectDateIV;
    private LinearLayout noDataLL;
    private ImageView tipsIV;
    private final int DATA_REFRESH=0;
    private final int CLOSE_REFRESH=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int what=msg.what;
            switch (what){
                case DATA_REFRESH:
                    showdateTV.setText(searchTime);
                    notifyData(dataUtil.getPointTestingResultModelList());
                    swipeRefreshLayout.setRefreshing(false);
                    break;
                case CLOSE_REFRESH:
                    swipeRefreshLayout.setRefreshing(false);
                    break;

            }
        }
    };
    private PointTestingResultRecyclerViewAdapter pointTestingResultAdapter;
    private List<PointTestingResultModel> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DataUtil dataUtil;
    private HttpUtil httpUtil;
    private SwipeRefreshLayout.OnRefreshListener refreshListener= new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
        }
    };

    private PointTestingResultRecyclerViewAdapter.OnItemClickListener onItemClickListener= new PointTestingResultRecyclerViewAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            goPointCard(position);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            goPointCard(position);
        }
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PointTestingResultFragment() {
        // Required empty public constructor
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PointTestingResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PointTestingResultFragment newInstance(String param1, String param2) {
        PointTestingResultFragment fragment = new PointTestingResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if(DataUtil.DEBUG){
            url="/app/pointtestingresult";
        }else{
            url="/examine/pointtestingresult";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_point_testing_result, container, false);
        initData();
        initView(view);
        initSwipeRefreshLayout(view);
        initRecyclerView(view);
        refreshData();
        return  view;
    }

    public void initData(){
        dataUtil=DataUtil.getInstance();
        httpUtil=HttpUtil.getInstance();
        searchTime=DateTimeUtil.getNewDateStringShow();
        list=new ArrayList<>();
        pointTestingResultAdapter= new PointTestingResultRecyclerViewAdapter(list,getContext());
        pointTestingResultAdapter.setOnItemClickListener(onItemClickListener);
    }

    public  void initView(View view){
        tipsIV=(ImageView) view.findViewById(R.id.point_explain_btn);
        tipsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTips();
            }
        });
        noDataLL=view.findViewById(R.id.point_sr_layout_hint);
        selectDateIV=view.findViewById(R.id.point_explain_date_iv);
        selectDateIV.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                changeTime();
            }
        });
        showdateTV=view.findViewById(R.id.point_explain_date_tv);
        showdateTV.setText(searchTime);
    }

    public void initSwipeRefreshLayout(View view){
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.point_sr_layout);
        swipeRefreshLayout.setColorSchemeColors(view.getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(refreshListener);
        swipeRefreshLayout.setRefreshing(true);
    }

    public void initRecyclerView(View view){
        LinearLayoutManager manager=new LinearLayoutManager(view.getContext());
        rl=(RecyclerView) view.findViewById(R.id.point_rc_view);
        rl.setAdapter(pointTestingResultAdapter);
        rl.setLayoutManager(manager);
        DividerItemDecoration itemDecoration=new DividerItemDecoration(view.getContext(),DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_custom_divider));
        rl.addItemDecoration(itemDecoration);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refreshData(){
        final UserModel userModel=dataUtil.getUser();
        final Map map=new HashMap();
        map.put("userId",userModel.getUserId());
        map.put("time",searchTime);
        Map params=new HashMap();
        params.put("data",JSONObject.toJSONString(map));
        httpUtil.asynch(url, httpUtil.TYPE_POST, params, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.sendEmptyMessage(CLOSE_REFRESH);
                MyException myException=new MyException();
                myException.buildException(e,getContext());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try{
                        String result=AESUtil.AESDecode(response.body().string());
                        JSONObject jsonObject=JSONObject.parseObject(result);
                        String errorCode=jsonObject.getString("errorCode");
                        if("0".equals(errorCode)){
                            list.clear();
                            String pointTestingResult=jsonObject.getString("pointTestResults");
                            if(!TextUtils.isEmpty(pointTestingResult)){
                                List<PointTestingResultModel> tempList= JSONArray.parseArray(pointTestingResult,PointTestingResultModel.class);
                                dataUtil.setPointTestingResultModelList(tempList);
                                list.addAll(tempList);
                            }
                        }
                    }catch (Exception e){
                        MyException myException=new MyException();
                        myException.buildException(e,getContext());
                    }finally {
                        handler.sendEmptyMessage(DATA_REFRESH);
                    }
                }
                handler.sendEmptyMessage(CLOSE_REFRESH);
            }
        });
    }
    public void showTips(){
        new AlertDialog.Builder(getActivity()).setTitle("信息说明").setMessage("红色为未检测项点，绿色为已检测项点。").show();
    }
    public void changeTime(){
        String newDate = DateTimeUtil.getNewDateStringShow();
        final String[] date = {DateTimeUtil.getDateStringSearch(newDate, -2), DateTimeUtil.getDateStringSearch(newDate, -1), DateTimeUtil.getNewDateStringShow()};
        new AlertDialog.Builder(getActivity()).setTitle("选择日期").setItems(date, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchTime = date[which];
                refreshData();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();    }

    public void goPointCard(int index){
        Intent intent=new Intent(getActivity(), PointCardDetailActivity.class);
        intent.putExtra("data",list.get(index));
        startActivity(intent);
    }

    public void notifyData(List<PointTestingResultModel> poiItemList) {
        if (poiItemList != null) {
            int previousSize = list.size();
            list.clear();
            pointTestingResultAdapter.notifyItemRangeRemoved(0, previousSize);
            list.addAll(poiItemList);
            if (list.size() == 0) {
                noDataLL.setVisibility(View.VISIBLE);
            } else {
                noDataLL.setVisibility(View.GONE);
            }
            pointTestingResultAdapter.notifyItemRangeInserted(0, poiItemList.size());
        }
    }
}
