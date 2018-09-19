package com.goldwind.app.help.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.util.SPUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 复杂计算器输出量
 */
public class ComplexCalculatorOutFragment extends BaseFragment {

    private static final String TAG = "complex";
    @Bind(R.id.frag_cal_out_outA)
    TextView frag_cal_out_outA;
    @Bind(R.id.frag_cal_out_outB)
    TextView frag_cal_out_outB;
    @Bind(R.id.frag_cal_out_outE1)
    TextView frag_cal_out_outE1;
    @Bind(R.id.frag_cal_out_outF1)
    TextView frag_cal_out_outF1;
    @Bind(R.id.frag_cal_out_outC)
    TextView frag_cal_out_outC;
    @Bind(R.id.frag_cal_out_outI)
    TextView frag_cal_out_outI;
    @Bind(R.id.frag_cal_out_outM)
    TextView frag_cal_out_outM;
    @Bind(R.id.frag_cal_out_outP)
    TextView frag_cal_out_outP;
    @Bind(R.id.frag_cal_out_outY)
    TextView frag_cal_out_outY;
    @Bind(R.id.frag_cal_out_outZ)
    TextView frag_cal_out_outZ;
    @Bind(R.id.frag_cal_out_outC1)
    TextView frag_cal_out_outC1;
    @Bind(R.id.frag_cal_out_outD1)
    TextView frag_cal_out_outD1;
    @Bind(R.id.frag_cal_out_outD)
    TextView frag_cal_out_outD;
    @Bind(R.id.frag_cal_out_outE)
    TextView frag_cal_out_outE;
    @Bind(R.id.frag_cal_out_outF)
    TextView frag_cal_out_outF;
    @Bind(R.id.frag_cal_out_outG)
    TextView frag_cal_out_outG;
    @Bind(R.id.frag_cal_out_outH)
    TextView frag_cal_out_outH;
    @Bind(R.id.frag_cal_out_outJ)
    TextView frag_cal_out_outJ;
    @Bind(R.id.frag_cal_out_outK)
    TextView frag_cal_out_outK;
    @Bind(R.id.frag_cal_out_outL)
    TextView frag_cal_out_outL;
    @Bind(R.id.frag_cal_out_outN)
    TextView frag_cal_out_outN;
    @Bind(R.id.frag_cal_out_outO)
    TextView frag_cal_out_outO;
    @Bind(R.id.frag_cal_out_outQ)
    TextView frag_cal_out_outQ;
    @Bind(R.id.frag_cal_out_outR)
    TextView frag_cal_out_outR;
    @Bind(R.id.frag_cal_out_outS)
    TextView frag_cal_out_outS;
    @Bind(R.id.frag_cal_out_outT)
    TextView frag_cal_out_outT;
    @Bind(R.id.frag_cal_out_outU)
    TextView frag_cal_out_outU;
    @Bind(R.id.frag_cal_out_outV)
    TextView frag_cal_out_outV;
    @Bind(R.id.frag_cal_out_outW)
    TextView frag_cal_out_outW;
    @Bind(R.id.frag_cal_out_outX)
    TextView frag_cal_out_outX;
    @Bind(R.id.frag_cal_out_outA1)
    TextView frag_cal_out_outA1;
    @Bind(R.id.frag_cal_out_outB1)
    TextView frag_cal_out_outB1;
    @Bind(R.id.frag_cal_out_outG1)
    TextView frag_cal_out_outG1;
    @Bind(R.id.frag_cal_out_outH1)
    TextView frag_cal_out_outH1;
    String mKey;
    boolean isHistory;
    private String mParams;

    public ComplexCalculatorOutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_template_detail_part, container, false);
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

    }

    @Override
    protected void initListener() {

    }

    public void setParam(String params) {
        this.mParams = params;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }

    @Override
    protected void initData() {
        if (isHistory) {
            SPUtils.setFileName(SPUtils.FILE_OUTPUT_NAME);
            String data = (String) SPUtils.get(getActivity(), mKey + TAG, "");
            JSONObject jb = JSON.parseObject(data);
            setValues(jb);
        } else {
            String outPutUrl = Constant.Api.GET_CAL_OUT_PARAMS_URL + "?" + mParams;
            LoadDataFromServer taskData = new LoadDataFromServer(getActivity(), outPutUrl);
            taskData.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(JSONObject data) {
                    try {
                        if (data.getInteger("code") == 200) {
                            Log.d("tiger", "key2==========" + mKey + TAG);
                            Log.d("tiger", data.toString());
                            SPUtils.setFileName(SPUtils.FILE_OUTPUT_NAME);
                            SPUtils.put(getActivity(), mKey + TAG, data.toString());
                            setValues(data);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            taskData = null;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void setValues(JSONObject data) {
        frag_cal_out_outA.setText(data.containsKey("outA") ? data.getString("outA") : "");
        frag_cal_out_outB.setText(data.containsKey("outB") ? data.getString("outB") : "");
        frag_cal_out_outE1.setText(data.containsKey("outE1") ? data.getString("outE1") : "");
        frag_cal_out_outF1.setText(data.containsKey("outF1") ? data.getString("outF1") : "");
        frag_cal_out_outC.setText(data.containsKey("outC") ? data.getString("outC") : "");
        frag_cal_out_outI.setText(data.containsKey("outI") ? data.getString("outI") : "");
        frag_cal_out_outM.setText(data.containsKey("outM") ? data.getString("outM") : "");
        frag_cal_out_outP.setText(data.containsKey("outP") ? data.getString("outP") : "");

        frag_cal_out_outY.setText(data.containsKey("outY") ? data.getString("outY") : "");
        frag_cal_out_outZ.setText(data.containsKey("outZ") ? data.getString("outZ") : "");
        frag_cal_out_outC1.setText(data.containsKey("outC1") ? data.getString("outC1") : "");
        frag_cal_out_outD1.setText(data.containsKey("outD1") ? data.getString("outD1") : "");

        frag_cal_out_outD.setText(data.containsKey("outD") ? data.getString("outD") : "");
        frag_cal_out_outE.setText(data.containsKey("outE") ? data.getString("outE") : "");
        frag_cal_out_outF.setText(data.containsKey("outF") ? data.getString("outF") : "");

        frag_cal_out_outG.setText(data.containsKey("outG") ? data.getString("outG") : "");
        frag_cal_out_outH.setText(data.containsKey("outH") ? data.getString("outH") : "");
        frag_cal_out_outJ.setText(data.containsKey("outJ") ? data.getString("outJ") : "");

        frag_cal_out_outK.setText(data.containsKey("outK") ? data.getString("outK") : "");
        frag_cal_out_outL.setText(data.containsKey("outL") ? data.getString("outL") : "");
        frag_cal_out_outN.setText(data.containsKey("outN") ? data.getString("outN") : "");

        frag_cal_out_outO.setText(data.containsKey("outO") ? data.getString("outO") : "");
        frag_cal_out_outQ.setText(data.containsKey("outQ") ? data.getString("outQ") : "");
        frag_cal_out_outR.setText(data.containsKey("outR") ? data.getString("outR") : "");

        frag_cal_out_outS.setText(data.containsKey("outS") ? data.getString("outS") : "");
        frag_cal_out_outT.setText(data.containsKey("outT") ? data.getString("outT") : "");
        frag_cal_out_outU.setText(data.containsKey("outU") ? data.getString("outU") : "");

        frag_cal_out_outV.setText(data.containsKey("outV") ? data.getString("outV") : "");
        frag_cal_out_outW.setText(data.containsKey("outW") ? data.getString("outW") : "");
        frag_cal_out_outX.setText(data.containsKey("outX") ? data.getString("outX") : "");

        frag_cal_out_outA1.setText(data.containsKey("outA1") ? data.getString("outA1") : "");
        frag_cal_out_outB1.setText(data.containsKey("outB1") ? data.getString("outB1") : "");
        frag_cal_out_outG1.setText(data.containsKey("outG1") ? data.getString("outG1") : "");
        frag_cal_out_outH1.setText(data.containsKey("outH1") ? data.getString("outH1") : "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
