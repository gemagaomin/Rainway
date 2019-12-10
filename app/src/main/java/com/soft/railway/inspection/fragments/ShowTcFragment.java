package com.soft.railway.inspection.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.models.TrainInfoModel;
import com.soft.railway.inspection.utils.DataUtil;


public class ShowTcFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView trainTextView;
    private TextView trainType;
    private TextView driverId;
    private TextView fdriverId;
    private TrainInfoModel trainInfoModel;
    private DataUtil dataUtil;
    private TextView jcTV;
    private TextView lsTV;
    private TextView zzTV;

    public ShowTcFragment() {
        // Required empty public constructor
    }

    public TrainInfoModel getTrainInfoModel() {
        return trainInfoModel;
    }

    public void setTrainInfoModel(TrainInfoModel trainInfoModel) {
        this.trainInfoModel = trainInfoModel;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowTcFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowTcFragment newInstance(String param1, String param2) {
        ShowTcFragment fragment = new ShowTcFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_show_tc, container, false);
        dataUtil=DataUtil.getInstance();
        trainTextView=(TextView)view.findViewById(R.id.work_detail_finish_train);
        trainType=(TextView)view.findViewById(R.id.work_detail_finish_train_type);
        driverId=(TextView)view.findViewById(R.id.work_detail_finish_driver);
        fdriverId=(TextView)view.findViewById(R.id.work_detail_finish_fdriver);
        jcTV=(TextView)view.findViewById(R.id.work_detail_finish_train_jc);
        lsTV=(TextView)view.findViewById(R.id.work_detail_finish_ls);
        zzTV=(TextView)view.findViewById(R.id.work_detail_finish_zz);

        trainTextView.setText(trainInfoModel.getTrainId());
        trainType.setText(trainInfoModel.getTrainTypeIdName(dataUtil.getTrainTypeMap()));
        driverId.setText(trainInfoModel.getDriverName());
        fdriverId.setText(trainInfoModel.getAssistantDriverName());
        jcTV.setText(trainInfoModel.getJc());
        lsTV.setText(trainInfoModel.getLs());
        zzTV.setText(trainInfoModel.getZz());
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
