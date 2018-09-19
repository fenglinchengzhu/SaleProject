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
 * 简单计算器展示输出量
 */
public class SimpleCalculatorOutFragment extends BaseFragment {

    private static final String TAG = "simple";
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
    String mKey;
    boolean isHistory;
    private String mParams;

    public SimpleCalculatorOutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_template_detail_all, container, false);
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
            try {
                SPUtils.setFileName(SPUtils.FILE_OUTPUT_NAME);
                String data = (String) SPUtils.get(getActivity(), mKey + TAG, "");
                JSONObject jb = JSON.parseObject(data);
                setValues(jb);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            String outPutUrl = Constant.Api.GET_CAL_OUT_PARAMS_URL + "?" + mParams;
            LoadDataFromServer taskData = new LoadDataFromServer(getActivity(), outPutUrl);
            taskData.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(JSONObject data) {
                    try {
                        if (data.getInteger("code") == 200) {
                            Log.d("tiger", "key==========" + mKey + TAG);
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

    private String get(String key) {
        String result = "";

        return result;
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

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
