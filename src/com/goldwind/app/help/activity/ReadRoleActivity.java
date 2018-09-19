package com.goldwind.app.help.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.model.GetApplyPermissionResult;
import com.goldwind.app.help.model.GetApplyPermissionResult.DataBean;
import com.goldwind.app.help.model.GetApplyPermissionResult.StartBean;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.JsonUtil;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.SildingFinishLayout;
import com.goldwind.app.help.view.SildingFinishLayout.OnSildingFinishListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadRoleActivity extends BaseActivity {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    @Bind(R.id.ll_apply_start)
    LinearLayout ll_apply_start;
    @Bind(R.id.ll_apply_data)
    LinearLayout ll_apply_data;

    private ReceiveBroadCast mReceiveBroadCast;

    private boolean isSnackBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_role);
        ButterKnife.bind(this);
        initAll();

        SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                finish();
            }
        });
        mSildingFinishLayout.setTouchView(mSildingFinishLayout);


        mReceiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ReadRoleActivityBroadCast");
        registerReceiver(mReceiveBroadCast, filter);
    }

    @Override
    protected void onDestroy() {
        if (mReceiveBroadCast != null) {
            try {
                unregisterReceiver(mReceiveBroadCast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        requestGetApplyPermission();
    }

    private String getRoleName(int i) {
        if (i == 1) {
            return "公开";
        }
        if (i == 2) {
            return "秘密";
        }
        if (i == 3) {
            return "机密";
        }
        if (i == 4) {
            return "绝密";
        }
        return "";
    }

    private void requestGetApplyPermission() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid", Constant.getCurrentUser(getApplicationContext()).staffid);
        ApiUtil.request(new ApiUtil.MyHttpRequest<GetApplyPermissionResult>(getApplicationContext(), Constant.Api.GET_APPLY_PERMISSION, ApiUtil
                .genRequestMap(map)) {

            @Override
            protected void onRequestStart() {
                MyProgressDialog.showDialog(ReadRoleActivity.this, "加载中");
            }

            @Override
            protected void onNetOrServerFailure() {
                MyProgressDialog.closeDialog();
            }

            @Override
            protected void onNetAndServerSuccess() {
                MyProgressDialog.closeDialog();
            }

            @Override
            public void handleResult(GetApplyPermissionResult result) {
                switch (result.result) {
                    case 200: {
                        if (result.data == null) {
                            return;
                        }


                        if (!TextUtils.isEmpty(result.data.role)) {
                            Constant.getCurrentUser(getApplicationContext()).role = result.data.role;
                            CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname,
                                    JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));
                        }


                        if (result.data.data != null && result.data.data.size() > 0) {
                            for (DataBean dataBean : result.data.data) {
                                View view = View.inflate(ReadRoleActivity.this, R.layout.layout_apply_data, null);
                                TextView textView = (TextView) view.findViewById(R.id.tv_apply_data);
                                textView.setText("您申请的" + getRoleName(dataBean.applypermission) + "权限，于" + CommonUtil.date2Str(new Date(dataBean.createtime), "yyyy年MM月dd日")
                                        + "已经过审核！");
                                ll_apply_data.addView(view);
                            }
                        }
                        if (result.data.startApply != null && result.data.startApply.size() > 0) {
                            for (StartBean startBean : result.data.startApply) {
                                View view = View.inflate(ReadRoleActivity.this, R.layout.layout_apply_start, null);
                                TextView textView = (TextView) view.findViewById(R.id.tv_apply_start);
                                textView.setText("您申请的" + getRoleName(startBean.applypermission) + "权限，正在审核中，请耐心等待！");
                                ll_apply_start.addView(view);
                            }
                        }
                        break;
                    }
                    default: {
                        if (getApplicationContext() == null) {
                            return;
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: {
                finish();
                break;
            }
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("data");
            if (TextUtils.equals(message, "RoleChange")) {
                if (getApplicationContext() == null) {
                    return;
                }
            }
        }
    }
}
