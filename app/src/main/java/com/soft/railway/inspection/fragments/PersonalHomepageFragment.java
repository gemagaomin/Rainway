package com.soft.railway.inspection.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.KeyPersonSettingActivity;
import com.soft.railway.inspection.activities.SettingActivity;
import com.soft.railway.inspection.models.PointNumModel;
import com.soft.railway.inspection.models.UserModel;
import com.soft.railway.inspection.pojos.Plan;
import com.soft.railway.inspection.utils.DataUtil;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PersonalHomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalHomepageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DataUtil dataUtil;
    private UserModel userModel;
    private TextView userNameTextView;
    private TextView userUnitNameTextView;
    private boolean isUp=false;
    private TextView        xcTV        ;
    private TextView        tcTV        ;
    private TextView        selfPointTV ;
    private TextView        otherPointTV;
    private TextView        xcFinishedTV        ;
    private TextView        tcFinishedTV        ;
    private TextView        selfPointFinishedTV ;
    private ImageView workIV;
    private LinearLayout    workDetailLL;
    private ImageView pointIV;
    private LinearLayout    pointDetailLL;
    private LinearLayout dataSetting;
    private boolean isPointUp=false;
    private static final int POINT_UP=2;
    private static final int POINT_DOWN=3;
    private PointNumModel pointNumModel;
    private Plan plan;
    private static final int UP=0;
    private static final int DOWN=1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case UP:
                    workIV.setImageResource(R.drawable.ic_up_jt);
                    workDetailLL.setVisibility(View.VISIBLE);
                    if(plan==null){
                        xcTV.setText("0");
                        tcTV.setText("0");
                        xcFinishedTV.setText("0");
                        tcFinishedTV.setText("0");
                    }else{
                        xcTV.setText(plan.getInCheck());
                        tcTV.setText(plan.getTrainCheck());
                        xcFinishedTV.setText(plan.getInCheckFinished());
                        tcFinishedTV.setText(plan.getTrainCheckFinished());
                    }
                    break;
                case  DOWN:
                    workIV.setImageResource(R.drawable.ic_down_jt);
                    workDetailLL.setVisibility(View.GONE);
                    break;
                case POINT_UP:
                    pointDetailLL.setVisibility(View.VISIBLE);
                    pointIV.setImageResource(R.drawable.ic_up_jt);
                    if(pointNumModel==null){
                        selfPointTV.setText("0");
                        otherPointTV.setText("0");
                        selfPointFinishedTV.setText("0");
                    }else{
                        selfPointTV.setText(pointNumModel.getAllCheck());
                        otherPointTV.setText(pointNumModel.getAllOtherFinished());
                        selfPointFinishedTV.setText(Integer.valueOf(pointNumModel.getAllFinished())-Integer.valueOf(pointNumModel.getAllOtherFinished())+"");
                    }
                    break;
                case  POINT_DOWN:
                    pointIV.setImageResource(R.drawable.ic_down_jt);
                    pointDetailLL.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public PersonalHomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PersonalHomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalHomepageFragment newInstance(String param1, String param2) {
        PersonalHomepageFragment fragment = new PersonalHomepageFragment();
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
        dataUtil=DataUtil.getInstance();
        userModel=dataUtil.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_personal_homepage,container,false);
        userNameTextView=(TextView)view.findViewById(R.id.personal_user_name);
        userUnitNameTextView=(TextView)view.findViewById(R.id.people_user_unit_name);
        workIV=(ImageView)view.findViewById(R.id.personal_home_page_work_detail_btn);
        workDetailLL=view.findViewById(R.id.personal_detail_work_linear_layout);
        pointIV=(ImageView)view.findViewById(R.id.personal_home_page_point_detail_btn);
        pointDetailLL=view.findViewById(R.id.personal_detail_point_linear_layout);
        xcFinishedTV        =view.findViewById(R.id.personal_xc_finished_num_tv);
        tcFinishedTV        =view.findViewById(R.id.personal_tc_finished_num_tv);
        selfPointFinishedTV =view.findViewById(R.id.personal_xd_self_finished_num_tv);
        xcTV        =view.findViewById(R.id.personal_xc_num_tv);
        tcTV        =view.findViewById(R.id.personal_tc_num_tv);
        selfPointTV =view.findViewById(R.id.personal_xd_self_num_tv);
        otherPointTV=view.findViewById(R.id.personal_xd_other_finished_num_tv);
        userNameTextView.setText(userModel.getUserName());
        userUnitNameTextView.setText(userModel.getUnitName());
        LinearLayout linearLayout=(LinearLayout) view.findViewById(R.id.personal_work_linear_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plan=dataUtil.getPlan();
                if(isUp){
                    handler.sendEmptyMessage(DOWN);
                }else{
                    handler.sendEmptyMessage(UP);
                }
                isUp=!isUp;
            }
        });
        LinearLayout pointLinearLayout=(LinearLayout) view.findViewById(R.id.personal_point_linear_layout);
        pointLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointNumModel=dataUtil.getPointNumModel();
                if(isPointUp){
                    handler.sendEmptyMessage(POINT_DOWN);
                }else{
                    handler.sendEmptyMessage(POINT_UP);
                }
                isPointUp=!isPointUp;
            }
        });
        LinearLayout settingLinearLayout=(LinearLayout) view.findViewById(R.id.personal_user_setting_linear_layout);
        settingLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity().getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });


        dataSetting=(LinearLayout)view.findViewById(R.id.personal_homepage_data_ll);
        dataSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity().getApplicationContext(), KeyPersonSettingActivity.class);
                startActivity(intent);
            }
        });
        return view;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
