package com.goldwind.app.help.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.CalculatorOutPutActivity;
import com.goldwind.app.help.adapter.CommonAdapter;
import com.goldwind.app.help.adapter.ViewHolder;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.util.SPUtils;
import com.goldwind.app.help.view.ClearEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 简单计算器的输入界面
 */
public class SimpleCalculatorInputFragment extends BaseFragment {
    String TAG = "SimpleCalculatorInputFragment";
    @Bind(R.id.frag_cal_input_compute_btn)
    Button computeBtn;

    @Bind(R.id.frag_cal_input_edit_inA)
    Spinner mSpinnerA;

    @Bind(R.id.frag_cal_input_edit_inB)
    Spinner mSpinnerB;

    @Bind(R.id.frag_cal_input_edit_inC)
    Spinner mSpinnerC;

    @Bind(R.id.frag_cal_input_edit_inD)
    Spinner mSpinnerD;

    @Bind(R.id.frag_cal_input_edit_inE)
    Spinner mSpinnerE;

    @Bind(R.id.input_simple_right_inF_txt)
    ClearEditText inF;

    @Bind(R.id.input_simple_right_inG_txt)
    ClearEditText inG;

    @Bind(R.id.input_simple_right_inH_txt)
    ClearEditText inH;
    @Bind(R.id.input_simple_right_inI_txt)
    ClearEditText inI;

    @Bind(R.id.input_simple_right_inJ_txt)
    ClearEditText inJ;
    List<String> mListA, mListB, mListC, mListD, mListE;
    /**
     * 处理保存本次输入量
     * 用SharePreference保存key 为名称如(主投方案2016.4.14.13:50计算结果)
     * 从保存列表点击此次保存就到当前页面显示对应的信息
     */
    SimpleDateFormat mSimpleDateFormat;
    private String mKey;
    private String mParams;
    private boolean isHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simple_calculator_input, container, false);
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
        computeBtn.setText(getString(R.string.str_start_compute_simple));
    }

    @Override
    protected void initListener() {
        computeBtn.setOnClickListener(this);
        mSpinnerA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mListA.size() > 0) {
                    String area = mListA.get(position);
                    if (!TextUtils.isEmpty(area)) {
                        handleArea(area);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    /**
     * 级联
     *
     * @param area
     */
    private void handleArea(String area) {
        LoadDataFromServer taskC = new LoadDataFromServer(getActivity(), Constant.Api.GET_LOCATION_DETAIL_URL + area);
        taskC.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    //number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");
                    if (jsonArray != null) {
                        mListC.clear();
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if ("内蒙古西部".equals(jsonArray.get(i).toString())) {
                            number = i;
                        }
                        mListC.add(jsonArray.get(i).toString());
                    }
                    if (mListC != null && mListC.size() > 0) {
                        mSpinnerC.setAdapter(new CommonAdapter<String>(getActivity(), mListC, R.layout.input_simple_calcu_spinner_item) {
                            @Override
                            public void convert(ViewHolder helper, String item) {
                                helper.setText(R.id.input_list_single_item_txt, item);
                            }
                        });
                    }
                    mSpinnerC.setSelection(number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //taskC = null;
    }

    @Override
    protected void initData() {
        mListA = new ArrayList<String>();
        mListB = new ArrayList<String>();
        mListC = new ArrayList<String>();
        mListD = new ArrayList<String>();
        mListE = new ArrayList<String>();
        String[] reses = getResources().getStringArray(R.array.input_simple_calcu_register_location);
        mListB = Arrays.asList(reses);

        mSpinnerB.setAdapter(new CommonAdapter<String>(getActivity(), mListB, R.layout.input_simple_calcu_spinner_item) {
            @Override
            public void convert(ViewHolder helper, String item) {
                helper.setText(R.id.input_list_single_item_txt, item);
            }
        });
        mSpinnerB.setSelection(1);

        LoadDataFromServer taskA = new LoadDataFromServer(getActivity(), Constant.Api.GET_LOCATION_URL);
        taskA.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    //number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");
                    if (jsonArray != null) {
                        mListA.clear();
                    }

                    for (int i = 0; i < jsonArray.size(); i++) {
                        if ("华北".equals(jsonArray.get(i).toString())) {
                            number = i;
                        }
                        mListA.add(jsonArray.get(i).toString());
                    }
                    if (mListA != null && mListA.size() > 0) {
                        mSpinnerA.setAdapter(new CommonAdapter<String>(getActivity(), mListA, R.layout.input_simple_calcu_spinner_item) {
                            @Override
                            public void convert(ViewHolder helper, String item) {
                                helper.setText(R.id.input_list_single_item_txt, item);
                            }
                        });
                        mSpinnerA.setSelection(number);

                        /*mSpinnerC.setAdapter(new CommonAdapter<String>(getActivity(), mListA, R.layout.input_simple_calcu_spinner_item) {
                            @Override
                            public void convert(ViewHolder helper, String item) {
                                helper.setText(R.id.input_list_single_item_txt, item);
                            }
                        });*/
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        LoadDataFromServer taskD = new LoadDataFromServer(getActivity(), Constant.Api.GET_FENG_TYPE_URL);
        taskD.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    Log.d(TAG, "result " + data.toString());
                    jsonArray = data.getJSONArray("data");
                    if (jsonArray != null) {
                        mListD.clear();
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if ("III".equals(jsonArray.get(i).toString())) {
                            number = i;
                        }
                        mListD.add(jsonArray.get(i).toString());
                    }
                    mSpinnerD.setAdapter(new CommonAdapter<String>(getActivity(), mListD, R.layout.input_simple_calcu_spinner_item) {
                        @Override
                        public void convert(ViewHolder helper, String item) {
                            helper.setText(R.id.input_list_single_item_txt, item);
                        }
                    });
                    mSpinnerD.setSelection(number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        LoadDataFromServer taskE = new LoadDataFromServer(getActivity(), Constant.Api.GET_JIZU_TYPE_URL);
        taskE.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    Log.d(TAG, "result " + data.toString());
                    //number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");
                    if (jsonArray != null) {
                        mListE.clear();
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        if ("121/2500".equals(jsonArray.get(i).toString())) {
                            number = i;
                        }
                        mListE.add(jsonArray.get(i).toString());
                    }
                    mSpinnerE.setAdapter(new CommonAdapter<String>(getActivity(), mListE, R.layout.input_simple_calcu_spinner_item) {
                        @Override
                        public void convert(ViewHolder helper, String item) {
                            helper.setText(R.id.input_list_single_item_txt, item);
                        }
                    });
                    mSpinnerE.setSelection(number);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == computeBtn) {
            Toast.makeText(getActivity(), "正在处理当前结果", Toast.LENGTH_LONG).show();
            handleSaveInputValues();
//            handleSaveOutputValues();
            Intent intent = new Intent();
            intent.putExtra("key", mKey);
            intent.putExtra("params", mParams);
            intent.setClass(getActivity(), CalculatorOutPutActivity.class);
            startActivity(intent);
        }
    }

    private void handleSaveInputValues() {
        JSONObject inputJson = new JSONObject();
        String input = "in";
        inputJson.put("inA", mSpinnerA.getSelectedItem().toString());
        inputJson.put("inB", mSpinnerB.getSelectedItem().toString());
        inputJson.put("inC", mSpinnerC.getSelectedItem().toString());
        inputJson.put("inD", mSpinnerD.getSelectedItem().toString());
        inputJson.put("inE", mSpinnerE.getSelectedItem().toString());
        String inFStr = TextUtils.isEmpty(inF.getText().toString()) ? inF.getHint().toString() : inF.getText().toString();
        String inGStr = TextUtils.isEmpty(inG.getText().toString()) ? inG.getHint().toString() : inG.getText().toString();
        String inHStr = TextUtils.isEmpty(inH.getText().toString()) ? inH.getHint().toString() : inH.getText().toString();
        String inIStr = TextUtils.isEmpty(inI.getText().toString()) ? inI.getHint().toString() : inI.getText().toString();
        String inJStr = TextUtils.isEmpty(inJ.getText().toString()) ? inJ.getHint().toString() : inJ.getText().toString();

        inputJson.put("inF", inFStr);
        inputJson.put("inG", inGStr);
        inputJson.put("inH", inHStr);
        inputJson.put("inI", inIStr);
        inputJson.put("inJ", inJStr);

        mParams = "inA=" + mSpinnerA.getSelectedItem().toString() + "&"
                + "inB=" + mSpinnerB.getSelectedItem().toString() + "&"
                + "inC=" + mSpinnerC.getSelectedItem().toString() + "&"
                + "inD=" + mSpinnerD.getSelectedItem().toString() + "&"
                + "inE=" + mSpinnerE.getSelectedItem().toString() + "&"

                + "inF=" + inFStr + "&"
                + "inG=" + inGStr + "&"
                + "inH=" + inHStr + "&"
                + "inI=" + inIStr + "&"
                + "inJ=" + inJStr;

        Log.d("tiger", inputJson.toString());
        Log.d("tiger", "params " + mParams);

        if (mSimpleDateFormat == null) {
            mSimpleDateFormat = new SimpleDateFormat();
        }


        long currentTime = System.currentTimeMillis();
        Date date = new Date();
        date.setTime(currentTime);
        mKey = getString(R.string.str_main_calculator) + mSimpleDateFormat.format(new Date()) + "输入结果" + ":" + currentTime;
        SPUtils.setFileName(SPUtils.FILE_INPUT_NAME);
        SPUtils.put(getActivity(), mKey, inputJson.toString());
    }


    private void initHistoryData() {
        String data = (String) SPUtils.get(this.getActivity(), mKey, "");

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
