package com.soft.railway.inspection.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;
import com.soft.railway.inspection.activities.RunningWorkActivity;
import com.soft.railway.inspection.activities.StartWorkActivity;
import com.soft.railway.inspection.activities.WorkDetailFinishActivity;
import com.soft.railway.inspection.activities.WorkExamineEditSmallActivity;
import com.soft.railway.inspection.activities.WorkExamineEditTCActivity;
import com.soft.railway.inspection.activities.WorkExamineEditXCActivity;
import com.soft.railway.inspection.activities.WorkExamineEditZXActivity;
import com.soft.railway.inspection.adapters.RecyclerAdapter;
import com.soft.railway.inspection.models.FileModel;
import com.soft.railway.inspection.models.PointItemModel;
import com.soft.railway.inspection.models.PointNumModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.models.WorkItemModel;
import com.soft.railway.inspection.models.WorkModel;
import com.soft.railway.inspection.pojos.Plan;
import com.soft.railway.inspection.pojos.WorkItemPojo;
import com.soft.railway.inspection.pojos.WorkPojo;
import com.soft.railway.inspection.utils.AESUtil;
import com.soft.railway.inspection.utils.DBUtil;
import com.soft.railway.inspection.utils.DataUtil;
import com.soft.railway.inspection.utils.DateTimeUtil;
import com.soft.railway.inspection.utils.FileUtil;
import com.soft.railway.inspection.utils.HttpUtil;
import com.soft.railway.inspection.utils.MyApplication;
import com.soft.railway.inspection.utils.MyException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.soft.railway.inspection.observers.Observer;
import com.soft.railway.inspection.utils.WorkObserverableUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link WorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkFragment extends Fragment implements Observer, RecyclerAdapter.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int DATA_REFRESH = 220;
    private WorkObserverableUtil workObserverableUtil;
    private String url = "/examine/list";
    private HttpUtil httpUtil;
    private DataUtil dataUtil;
    private DBUtil dbUtil;
    private String searchTime = "";
    Map<String, PointItemModel> pointItemModelMap;
    private static final int WHAT_HIDE_REFRESH = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case DATA_REFRESH:
                    showDateTime.setText(searchTime);
                    myRecyclerAdapter.notifyDataSetChanged();
                    notifyData(dataUtil.getWorkPojoListForData(searchTime));
                    mySwipeRefreshLayout.setRefreshing(false);
                    break;
                case WHAT_HIDE_REFRESH:
                    mySwipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    private List<WorkPojo> list = new ArrayList<>();
    private List<WorkPojo> listTemp = new ArrayList<>();
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener myOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            refreshData();
            mySwipeRefreshLayout.setRefreshing(false);
        }
    };
    private RecyclerView myRecyclerView;
    private RecyclerAdapter myRecyclerAdapter;
    private Button addBtn;
    private AlertDialog dialog;
    private ImageView workDateBtn;
    private UserModel userModel;
    private TextView showDateTime;
    private TextView nullDataTV;

    private LinearLayout txLLBtn;
    private LinearLayout xcLLBtn;
    private LinearLayout smallTcLLBtn;
    private LinearLayout zxLLBtn;


    public WorkFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static WorkFragment newInstance(String param1, String param2) {
        WorkFragment fragment = new WorkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workObserverableUtil = WorkObserverableUtil.getInstance();
        workObserverableUtil.registerObserver(this);
        if (DataUtil.DEBUG) {
            url = "/app/list";
        } else {
            url = "/examine/list";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_work, container, false);
        initData();
        initView(view);
        initSwipeRefreshLayout(view);
        initRecycler(view);
        return view;
    }

    public void initData() {
        httpUtil = HttpUtil.getInstance();
        dataUtil = DataUtil.getInstance();
        dbUtil = DBUtil.getInstance();
        userModel = dataUtil.getUser();
        pointItemModelMap = dataUtil.getPointMap();
        String time = DataUtil.selectSearchTime;
        if (TextUtils.isEmpty(time)) {
            time = DateTimeUtil.getNewDateStringShow();
            DataUtil.selectSearchTime = time;
        }
        searchTime = time;
        refreshData();
    }

    public void initView(View view) {
        addBtn = (Button) view.findViewById(R.id.add_work_floating_btn);
        if (list != null && list.size() >= 0) {

        }
        workDateBtn = (ImageView) view.findViewById(R.id.work_date_btn);
        workDateBtn.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                changeTime();
            }
        });
        showDateTime = (TextView) view.findViewById(R.id.work_date_time_work);
        showDateTime.setText(DateTimeUtil.getNewDateStringShow());
        nullDataTV = (TextView) view.findViewById(R.id.work_content_null);
        txLLBtn = view.findViewById(R.id.add_work_tc_btn);
        xcLLBtn = view.findViewById(R.id.add_work_xc_btn);
        smallTcLLBtn = view.findViewById(R.id.add_work_tc_small_btn);
        zxLLBtn = view.findViewById(R.id.add_work_zx_btn);
        txLLBtn.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                goAddActivity(0);
            }
        });
        xcLLBtn.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                goAddActivity(1);
            }
        });
        smallTcLLBtn.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                goAddActivity(2);
            }
        });
        zxLLBtn.setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                goAddActivity(3);
            }
        });
    }

    public void initSwipeRefreshLayout(View view) {
        mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.point_sr_layout);
        mySwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mySwipeRefreshLayout.setOnRefreshListener(myOnRefreshListener);
        mySwipeRefreshLayout.setRefreshing(true);
    }

    public void initRecycler(View view) {
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        myRecyclerView = (RecyclerView) view.findViewById(R.id.work_rc_view);
        myRecyclerAdapter = new RecyclerAdapter(list);
        myRecyclerAdapter.setOnItemClickListener(this);
        myRecyclerView.setLayoutManager(manager);
        myRecyclerView.setAdapter(myRecyclerAdapter);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_custom_divider));
        myRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void refreshData() {
        final UserModel userModel = dataUtil.getUser();
        final Map<String, String> map = new HashMap<String, String>();
        map.put("userId", userModel.getUserId());
        final Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("data", JSONObject.toJSONString(map));
        httpUtil.asynch(url, httpUtil.TYPE_POST, paramMap, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listTemp = new ArrayList<>();
                localRefresh();
                MyException myException = new MyException();
                myException.buildException(e, getContext());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        result = AESUtil.AESDecode(result);
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        String planStr = jsonObject.getString("months");
                        List<WorkModel> workList;
                        List<WorkPojo> workShowList = new ArrayList<>();
                        Map<String, Plan> planMap = new HashMap<>();
                        if (!TextUtils.isEmpty(planStr)) {
                            List<Plan> plans = JSONArray.parseArray(planStr, Plan.class);
                            if (plans != null && plans.size() > 0) {
                                for (int i = 0, num = plans.size(); i < num; i++) {
                                    Plan plan = plans.get(i);
                                    planMap.put(plan.getCreateTime(), plan);
                                    workList = plan.getWorks();
                                    if (workList != null && workList.size() > 0) {
                                        for (WorkModel o : workList
                                        ) {
                                            workShowList.add(new WorkPojo(o, pointItemModelMap));
                                        }
                                    }
                                }
                            }
                            listTemp = workShowList;
                            dataUtil.setPlanList(plans);
                            dataUtil.setPlanMap(planMap);
                        } else {
                            listTemp = new ArrayList<>();
                            dataUtil.setPlanList(new ArrayList<Plan>());
                            dataUtil.setPlanMap(new HashMap<String, Plan>());
                        }
                        String point = jsonObject.getString("point");
                        if (!TextUtils.isEmpty(point)) {
                            PointNumModel pointNumModel = JSONObject.parseObject(point, PointNumModel.class);
                            dataUtil.setPointNumModel(pointNumModel);
                        } else {
                            dataUtil.setPointNumModel(new PointNumModel()
                            );
                        }
                    }
                } catch (Exception e) {
                    MyException myException = new MyException();
                    myException.buildException(e, getContext());
                } finally {
                    localRefresh();
                }
            }
        });
    }

    public void ShowWorkTypeDialog(View v) {
        String[] workType = {DataUtil.WorkTypeMap.get(DataUtil.WORK_TYPE_TC), DataUtil.WorkTypeMap.get(DataUtil.WORK_TYPE_XCJC), DataUtil.WorkTypeMap.get(DataUtil.WORK_TYPE_SMALL_TC), DataUtil.WorkTypeMap.get(DataUtil.WORK_TYPE_ZXJC)};
        if (dialog == null) {
            dialog = new AlertDialog.Builder(v.getContext()).setTitle("选择工作类型").setItems(workType, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            Intent intent = new Intent();
                            intent.setClass(getContext(), WorkExamineEditTCActivity.class);
                            dialog.cancel();
                            getActivity().startActivity(intent);
                            break;
                        case 1:
                            Intent intentExamine = new Intent();
                            intentExamine.setClass(getContext(), WorkExamineEditXCActivity.class);
                            dialog.cancel();
                            getActivity().startActivity(intentExamine);
                            break;
                        case 2:
                            Intent intentSmall = new Intent();
                            intentSmall.setClass(getContext(), WorkExamineEditSmallActivity.class);
                            dialog.cancel();
                            getActivity().startActivity(intentSmall);
                            break;
                        case 3:
                            Intent intentZX = new Intent();
                            intentZX.setClass(getContext(), WorkExamineEditZXActivity.class);
                            dialog.cancel();
                            getActivity().startActivity(intentZX);
                            break;
                    }
                }
            }).show();
        } else {
            dialog.show();
        }
    }

    /**
     * 本地刷新数据
     */
    public void localRefresh() {
        Cursor cursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.WORK.toString() + " where userid=?  ORDER BY workstatus desc ,worktype,starttime", new String[]{userModel.getUserId()});
        if (cursor != null) {
            List<WorkPojo> responseList = new ArrayList<>();
            responseList.addAll(listTemp);
            while (cursor.moveToNext()) {
                WorkModel workModel = new WorkModel(cursor);
                if ((DataUtil.WORK_STATUS_RUNNING + "").equals(workModel.getWorkStatus())) {
                    MyApplication.instance.begin(workModel.getWorkId());
                    Cursor fileCursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.SUBMITFILE.toString() + " where workid=? and filestatus=? order by filetime ", new String[]{workModel.getWorkId(), FileUtil.FILE_STATUS_NOT_CAN_UPLOADED});
                    List<FileModel> list = new ArrayList<>();
                    List<FileModel> recorderList = new ArrayList<>();
                    String fileType = "";
                    if (fileCursor != null) {
                        while ((fileCursor.moveToNext())) {
                            FileModel fileModel = new FileModel(fileCursor);
                            fileType = fileModel.getFileType();
                            if (FileUtil.FILE_TYPE_RECORDER.equals(fileType)) {
                                recorderList.add(fileModel);
                            } else if (FileUtil.FILE_TYPE_PHOTO.equals(fileType) && FileUtil.FILE_RANK_HIGH.equals(fileModel.getFileRank())) {
                                list.add(fileModel);
                            }
                        }
                        fileCursor.close();
                    }
                    workModel.setCapturePhotos(list);
                    workModel.setRecorders(recorderList);
                }
                Cursor problemCursor = dbUtil.select("select * from " + DataUtil.TableNameEnum.ITEM.toString() + " where workid=?", new String[]{workModel.getWorkId()});
                List<WorkItemModel> workItemModelList = new ArrayList();
                List<WorkItemPojo> workItemPojoList = new ArrayList();
                Map<String, WorkItemPojo> pointDetailPojoMap = new HashMap<>();
                if (problemCursor != null) {
                    while (problemCursor.moveToNext()) {
                        WorkItemModel workItemModel = new WorkItemModel(problemCursor);
                        workItemModelList.add(workItemModel);
                        WorkItemPojo workItemPojo = new WorkItemPojo(workItemModel);
                        workItemPojoList.add(workItemPojo);
                        pointDetailPojoMap.put(workItemPojo.getItemId(), workItemPojo);
                    }
                    dataUtil.setWorkItemPojoList(workItemPojoList);
                    dataUtil.setPointDetailPojoMap(pointDetailPojoMap);
                }
                workModel.setItems(workItemModelList);
                responseList.add(0, new WorkPojo(workModel, pointItemModelMap));
            }
            dataUtil.setWorkPojoList(responseList);
        }
        handler.sendEmptyMessage(DATA_REFRESH);
    }

    @Override
    /**
     * 页面显示数据刷新
     */
    public void refreshView() {
        if (!TextUtils.isEmpty(DataUtil.selectSearchTime)) {
            searchTime = DataUtil.selectSearchTime;
        }
        handler.sendEmptyMessage(DATA_REFRESH);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        workObserverableUtil.removeObserver(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        WorkPojo workPojo = list.get(position);
        Intent intent = new Intent();
        int status = Integer.valueOf(workPojo.getWorkStatus());
        switch (status) {
            case DataUtil.WORK_STATUS_UNSTART:
                intent.setClass(getActivity(), StartWorkActivity.class);
                break;
            case DataUtil.WORK_STATUS_FINISH:
                intent.setClass(getActivity(), WorkDetailFinishActivity.class);
                break;
            default:
                intent.setClass(getActivity(), RunningWorkActivity.class);
                break;
        }
        intent.putExtra("data", workPojo);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    public void changeTime() {
        String newDate = DateTimeUtil.getNewDateStringShow();
        final String[] date = {DateTimeUtil.getDateStringSearch(newDate, -2), DateTimeUtil.getDateStringSearch(newDate, -1), DateTimeUtil.getNewDateStringShow()};
        new AlertDialog.Builder(getActivity()).setTitle("选择日期").setItems(date, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchTime = date[which];
                DataUtil.selectSearchTime = searchTime;
                mySwipeRefreshLayout.setRefreshing(true);
                if (list != null) {
                    list.clear();
                }
                List<WorkPojo> listTemp = dataUtil.getWorkPojoListForData(searchTime);
                list.addAll(listTemp);
                handler.sendEmptyMessage(DATA_REFRESH);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void notifyData(List<WorkPojo> poiItemList) {
        if (poiItemList != null) {
            int previousSize = list.size();
            list.clear();
            myRecyclerAdapter.notifyItemRangeRemoved(0, previousSize);
            list.addAll(poiItemList);
            if (list.size() == 0) {
                nullDataTV.setVisibility(View.VISIBLE);
            } else {
                nullDataTV.setVisibility(View.GONE);
            }
            myRecyclerAdapter.notifyItemRangeInserted(0, poiItemList.size());
        }
    }

    public void goAddActivity(int which) {
        switch (which) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(getContext(), WorkExamineEditTCActivity.class);
                getActivity().startActivity(intent);
                break;
            case 1:
                Intent intentExamine = new Intent();
                intentExamine.setClass(getContext(), WorkExamineEditXCActivity.class);
                getActivity().startActivity(intentExamine);
                break;
            case 2:
                Intent intentSmall = new Intent();
                intentSmall.setClass(getContext(), WorkExamineEditSmallActivity.class);
                getActivity().startActivity(intentSmall);
                break;
            case 3:
                Intent intentZX = new Intent();
                intentZX.setClass(getContext(), WorkExamineEditZXActivity.class);
                getActivity().startActivity(intentZX);
                break;
        }
    }
}
