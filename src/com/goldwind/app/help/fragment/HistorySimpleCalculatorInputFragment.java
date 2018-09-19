package com.goldwind.app.help.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.CalculatorOutPutActivity;
import com.goldwind.app.help.util.SPUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 简单计算器的输入界面
 */
public class HistorySimpleCalculatorInputFragment extends BaseFragment {
    String TAG = "SimpleCalculatorInputFragment";
    @Bind(R.id.frag_cal_input_compute_btn)
    Button computeBtn;

    @Bind(R.id.frag_cal_input_edit_inA)
    TextView inA;

    @Bind(R.id.frag_cal_input_edit_inB)
    TextView inB;

    @Bind(R.id.frag_cal_input_edit_inC)
    TextView inC;

    @Bind(R.id.frag_cal_input_edit_inD)
    TextView inD;

    @Bind(R.id.frag_cal_input_edit_inE)
    TextView inE;

    @Bind(R.id.input_simple_right_inF_txt)
    TextView inF;

    @Bind(R.id.input_simple_right_inG_txt)
    TextView inG;

    @Bind(R.id.input_simple_right_inH_txt)
    TextView inH;
    @Bind(R.id.input_simple_right_inI_txt)
    TextView inI;

    @Bind(R.id.input_simple_right_inJ_txt)
    TextView inJ;
    List<String> mListA, mListB, mListC, mListD, mListE;
    private String mKey;
    private boolean isHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.history_fragment_simple_calculator_input, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;//super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {
        computeBtn.setText("查看计算结果");
    }

    @Override
    protected void initListener() {
        computeBtn.setOnClickListener(this);


    }

    @Override
    protected void initData() {
        initHistoryData();
    }

    @Override
    public void onClick(View v) {
        if (v == computeBtn) {
            Intent intent = new Intent();
            intent.putExtra("key", mKey);
            intent.putExtra("isHistory", true);
            intent.setClass(getActivity(), CalculatorOutPutActivity.class);
            startActivity(intent);
        }
    }


    private void initHistoryData() {
        SPUtils.setFileName(SPUtils.FILE_INPUT_NAME);
        String data = (String) SPUtils.get(this.getActivity(), mKey, "");
        try {
            JSONObject jb = JSON.parseObject(data);
            inA.setText(jb.getString("inA"));
            inB.setText(jb.getString("inB"));
            inC.setText(jb.getString("inC"));
            inD.setText(jb.getString("inD"));
            inE.setText(jb.getString("inE"));
            inF.setText(jb.getString("inF"));
            inG.setText(jb.getString("inG"));
            inH.setText(jb.getString("inH"));
            inI.setText(jb.getString("inI"));
            inJ.setText(jb.getString("inJ"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setIsHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    public void setKey(String key) {
        this.mKey = key;
    }
}
