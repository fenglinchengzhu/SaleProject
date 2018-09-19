package com.goldwind.app.help.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.download.StorageUtils;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.JsonUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

public class FirstUpdateActivity extends BaseActivity {

    private static final int OPEN_HOME = 1000;
    private static final int CLOSE_APP = 1001;
    @Bind(R.id.kp_bar)
    View kp_bar;
    private Thread thread;
    private GetResourcesResult mResult;
    private boolean isSnackBar = false;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == OPEN_HOME) {
                createDirs();

                CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_version", JsonUtil.toJson(mResult.data.version));
                CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_groupids", mResult.data.groupids);

                Intent intent = new Intent(FirstUpdateActivity.this, HomeActivity.class);
                intent.putExtra("fromLogin", true);
                startActivity(intent);
                finish();
            } else if (msg.what == CLOSE_APP) {
                finish();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_update);
        initAll();
    }

    @Override
    protected void initParam() {
        thread = new Thread() {
            @Override
            public void run() {
                StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                if (mResult != null) {
                    MyDB.getInstance(getApplicationContext()).update(getApplicationContext(), mResult, true, Constant.getCurrentUser(getApplicationContext()).staffid);
                    MyDB.getInstance(getApplicationContext()).checkFileDelete(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
                    MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
                    Constant.newUpdate = MyDB.getInstance(getApplicationContext()).getResourceCount(Constant.getCurrentUser(getApplicationContext()).staffid);
                    CommonUtil.spPutInt(getApplicationContext(),
                            Constant.getCurrentUser(getApplicationContext()).staffname + "_newUpdate",
                            Constant.newUpdate);
                }
                mHandler.sendEmptyMessageDelayed(OPEN_HOME, 1000);
            }
        };
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        requestGetResources();
    }

    @Override
    public void onClick(View v) {
    }

    private void createDirs() {
        File file = new File(Constant.BASE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(Constant.BASE_CACHE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(Constant.BASE_FILE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(Constant.BASE_DECODE_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // 请求服务器资源
    private void requestGetResources() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid", Constant.getCurrentUser(getApplicationContext()).staffid);
        map.put("categoryVersion", "0");
        map.put("resourceVersion", "0");
        map.put("labelVersion", "0");
        map.put("userName", Constant.getCurrentUser(getApplicationContext()).staffname);
        map.put("passWord", Constant.getCurrentUser(getApplicationContext()).password);
        ApiUtil.request(new ApiUtil.MyHttpRequest<GetResourcesResult>(getApplicationContext(), Constant.Api.GET_RESOURCES, ApiUtil.genRequestMap(map)) {

            @Override
            protected void onRequestStart() {
            }

            @Override
            protected void onNetOrServerFailure() {
                if (getApplicationContext()
                        == null) {
                    return;
                }
                mHandler.sendEmptyMessageDelayed(CLOSE_APP, 2500);
            }

            @Override
            protected void onNetAndServerSuccess() {

            }

            @Override
            public void handleResult(GetResourcesResult result) {
                if (TextUtils.isEmpty(Constant.getCurrentUser(getApplicationContext()).staffid)) {
                    return;
                }
                switch (result.result) {
                    case 200: {
                        if (result.data != null && result.data.resource != null) {
                            for (ResourceItem item : result.data.resource) {
                                item.staffid = Integer.valueOf(Constant.getCurrentUser(getApplicationContext()).staffid);
                            }
                        }

                        Constant.getCurrentUser(getApplicationContext()).role = result.data.staffRole;
                        CommonUtil.spPutString(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname,
                                JsonUtil.toJson(Constant.getCurrentUser(getApplicationContext())));

                        mResult = result;
                        thread.start();
                        break;
                    }
                    default: {
                        if (getApplicationContext()
                                == null) {
                            return;
                        }
                        break;
                    }
                }
            }
        });
    }
}
