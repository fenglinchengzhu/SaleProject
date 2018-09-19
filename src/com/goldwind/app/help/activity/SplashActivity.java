package com.goldwind.app.help.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.awsdownload.Util;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.download.StorageUtils;
import com.goldwind.app.help.model.UserInfoResult;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.JsonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    private static final int OPEN_HOME = 1000;
    private static final int OPEN_LOGIN = 1001;
    public static String accessKeyID;
    public static String secretKey;
    public static String bucketName;
    @Bind(R.id.tv_version)
    TextView tv_version;
    @Bind(R.id.welecome_over)
    ImageView welecome_over;
    int windowHeight;
    int windowWidth;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == OPEN_HOME) {
                openHome();
            } else if (msg.what == OPEN_LOGIN) {
                openLogin();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        if (CommonUtil.spGetInt(getApplicationContext(), "guide") == 0) {
            CommonUtil.spPutInt(getApplicationContext(), "guide", 1);
            startActivity(new Intent(this, GuideActivity.class));
//            finish();
            return;
        }


        initAll();


        if (TextUtils.isEmpty(CommonUtil.spGetString(this, "accessKeyID"))
                && TextUtils.isEmpty(accessKeyID)) {
            accessKeyID = "AKIAOKZOY5SBT7QTTXSQ";
            secretKey = "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";
            bucketName = "fengjihelp";
            Util.initS3Client(accessKeyID, secretKey
                    , bucketName);
        } else if (!TextUtils.isEmpty(CommonUtil.spGetString(this, "accessKeyID"))
                && TextUtils.isEmpty(accessKeyID)) {
            accessKeyID = CommonUtil.spGetString(this, "accessKeyID");
            secretKey = CommonUtil.spGetString(this, "secretKey");
            bucketName = CommonUtil.spGetString(this, "bucketName");
            Util.initS3Client(accessKeyID, secretKey
                    , bucketName);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextPage();
            }
        }, 2000);


    }


    private void nextPage() {
        String firstStart = CommonUtil.spGetString(getApplicationContext(), Constant.Key.FIRST_START);
        if (TextUtils.isEmpty(firstStart)) {

            CommonUtil.spPutInt(getApplicationContext(), "epub", 0);
            CommonUtil.spPutInt(getApplicationContext(), "isnight", 0);
            new Thread() {
                public void run() {
                    StorageUtils.delete(new File(Constant.BASE_PATH));
                    StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                    mHandler.sendEmptyMessage(OPEN_LOGIN);
                }

                ;
            }.start();
            CommonUtil.spPutString(getApplicationContext(), Constant.Key.FIRST_START, "NO");
            return;
        }


        String staffname = CommonUtil.spGetString(getApplicationContext(), Constant.Key.CURRENT_USER);
        if (TextUtils.isEmpty(staffname)) {// 没有登陆
            new Thread() {
                public void run() {
                    StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                    mHandler.sendEmptyMessage(OPEN_LOGIN);
                }

                ;
            }.start();
        } else {// 登陆过
            String jsonStr = CommonUtil.spGetString(getApplicationContext(), staffname);
            UserInfoResult.Data data = JsonUtil.fromJson(jsonStr, UserInfoResult.Data.class);
            if (System.currentTimeMillis() - data.time > 0 && System.currentTimeMillis() - data.time
                    < 86400000L * 30L) { // 登录状态不超时
                Constant.saveUserInfo(getApplicationContext(), data);
                new Thread() {
                    public void run() {
                        // 更新文件的下载状态
                        MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
                        StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                        mHandler.sendEmptyMessage(OPEN_HOME);
                    }

                    ;
                }.start();
            } else { // 登录状态超时
                new Thread() {
                    public void run() {
                        StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                        mHandler.sendEmptyMessage(OPEN_LOGIN);
                    }

                    ;
                }.start();
            }
        }
    }

    @Override
    protected void initParam() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        windowHeight = dm.widthPixels;
        windowWidth = dm.heightPixels;
    }

    @Override
    protected void initViews() {
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.welcome, welecome_over);
        tv_version.setText("版本：V" + ApiUtil._appversion);
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }

    private void openLogin() {
        createDirs();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
//        finish();
    }

    private void openHome() {
        createDirs();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
//        finish();
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

    @Override
    public void onClick(View v) {
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
