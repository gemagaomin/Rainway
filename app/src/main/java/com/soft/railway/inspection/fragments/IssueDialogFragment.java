package com.soft.railway.inspection.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.soft.railway.inspection.R;
import com.soft.railway.inspection.activities.BaseActivity;


public class IssueDialogFragment extends DialogFragment {
    private View issueLinearLayout;
    private View issueValueLinearLayout;
    private EditText issueValueText;
    private static final int ISSUESHOW=3;
    private static final int ISSUEVALUESHOW=4;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what=msg.what;
            switch (what){
                case ISSUESHOW:
                    issueLinearLayout.setVisibility(View.VISIBLE);
                    issueValueLinearLayout.setVisibility(View.GONE);
                    break;
                case ISSUEVALUESHOW:
                    issueLinearLayout.setVisibility(View.GONE);
                    issueValueLinearLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private IssueDialogInteractionListener mListener;

    public IssueDialogFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static IssueDialogFragment newInstance() {
        IssueDialogFragment fragment = new IssueDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View issueView=inflater.inflate(R.layout.issue_layout, container, false);
        issueLinearLayout=issueView.findViewById(R.id.dialog_issue_if_has_linear_layout);
        issueValueLinearLayout=issueView.findViewById(R.id.dialog_issue_submit_linear_layout);
        issueView.findViewById(R.id.dialog_issue_has).setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                handler.sendEmptyMessage(ISSUEVALUESHOW);
            }
        });

        issueView.findViewById(R.id.dialog_issue_not_has).setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                mListener.SubmitClick("");
                handler.sendEmptyMessage(ISSUESHOW);
            }
        });
        issueView.findViewById(R.id.dialog_issue__submit).setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                mListener.SubmitClick(issueValueText.getText().toString());
            }
        });
        issueView.findViewById(R.id.dialog_issue__cancel).setOnClickListener(new BaseActivity() {
            @Override
            public void onNoDoubleClick(View v) {
                HideIssueDialog();
            }
        });
        issueValueText=(EditText) issueView.findViewById(R.id.dialog_issue_value);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_BACK){
                    return true;
                }
                return false;
            }
        });
        return issueView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface IssueDialogInteractionListener {
        // TODO: Update argument type and name
        void SubmitClick(String value);
    }

    public void HideIssueDialog(){
        handler.sendEmptyMessage(ISSUESHOW);
        getDialog().dismiss();
    }

    public IssueDialogInteractionListener getmListener() {
        return mListener;
    }

    public void setmListener(IssueDialogInteractionListener mListener) {
        this.mListener = mListener;
    }
}
