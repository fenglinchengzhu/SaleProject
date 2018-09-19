package com.goldwind.app.help.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import com.goldwind.app.help.view.MyProgressDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity {
    private static final int OPEN_HOME = 1000;
    @Bind(R.id.et_account)
    EditText etAccount;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.cb_auto_login)
    CheckBox cbAutoLogin;
    @Bind(R.id.ll_auto_login)
    LinearLayout llAutoLogin;
    @Bind(R.id.bt_login)
    Button btLogin;
    @Bind(R.id.login_main_bg)
    ImageView mainBg;
    @Bind(R.id.img_splash_logo)
    ImageView logoImg;
    private boolean isSnackBar = false;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == OPEN_HOME) {
                openHome();
            }
        }

        ;
    };
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        //mainLay.setBackground(new BitmapDrawable(ImageLoader.getInstance().displayImage("drawable://" + R.drawable.welcome,mainLay)));
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.login_bg, mainBg);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.img_splash_logo, logoImg);
        if (btLogin == null) {
            Log.d("error", "btLogin is null");
        }
        if (etAccount == null) {
            Log.d("error", "etAccount is null");
        }
        if (etPassword == null) {
            Log.d("error", "etPassword is null");
        }
        if (llAutoLogin == null) {
            Log.d("error", "llAutoLogin is null");
        }
        if (cbAutoLogin == null) {
            Log.d("error", "cbAutoLogin is null");
        }
        initAll();
    }

    @Override
    protected void initParam() {

    }

    @Override
    protected void initViews() {

//		etAccount.setText("25577");
//		etPassword.setText("910227lln");
        etAccount.setText("");
        etPassword.setText("");
    }

    @Override
    protected void initListener() {
        btLogin.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("fromLogin", true);
        startActivity(intent);
        finish();

    }

    private boolean contains(String[] stringArray, String source) {
        return Arrays.asList(stringArray).contains(source);
    }

    private void requestLogin() {
        String account = etAccount.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            if (getApplicationContext() == null) {
                return;
            }
            return;
        }

        //有权限的用户
        String[] users = {"25577", "1125", "2130", "4091", "1955", "4218", "10003", "30681", "28375", "3558", "5752", "26162", "9165", "25024", "28410", "29340", "6617", "23711", "25718", "27027", "29525", "2709", "7839", "10365", "30236", "3871", "23983", "26660", "31116", "2136", "5612", "2029", "25171", "7903", "4572", "1705", "5282", "29526", "30376", "3538", "5729", "8085", "2160", "4623", "2710", "9791", "4652", "27358", "30690", "30788", "10682", "29919", "2015", "1873", "27302", "1726", "26197", "1915", "2016", "6162", "1766", "27438", "2133", "28373", "30831", "28412", "30208", "31153", "26944", "26194", "26526", "26531", "26798", "28413", "2190", "29686", "2005", "26199", "1208", "7796", "1852", "11917", "31027", "26919", "30246", "1952", "11250", "8662", "26938", "30695", "2191", "2988", "11009", "29632", "1627", "2706", "30173", "2132", "24750", "31029", "4621", "2703", "2193", "5755", "30767", "2153", "28115", "25907", "25906", "26089", "30118", "29642", "4753", "30237", "30864", "6275", "29360", "11813", "29362", "4739", "29527", "26136", "26137", "26138", "26497", "27321", "30159", "28515", "28783", "29504", "29516", "29517", "29518", "29519", "29520", "29521", "29522", "29523", "29897", "30424", "30919", "3378", "3785", "3797", "8589", "26736", "27260", "27261", "27475", "28109", "28378", "29359", "29361", "29363", "29364", "29366", "29399", "29514", "29515", "29524", "31118", "31119", "31123", "31124", "2369", "2559", "2558"};
        if (!contains(users, account)) {
            if (getApplicationContext() == null) {
                return;
            }
            return;
        }

        if (TextUtils.isEmpty(password)) {
            if (getApplicationContext() == null) {
                return;
            }
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userName", account);
        map.put("passWord", password);
//		mHandler.sendEmptyMessage(OPEN_HOME);
//		if(true){
//			return;
//		}
        ApiUtil.request(new ApiUtil.MyHttpRequest<UserInfoResult>(getApplicationContext(), Constant.Api.USER_INFO, ApiUtil.genRequestMap(map)) {

            @Override
            protected void onRequestStart() {
                MyProgressDialog.showDialog(LoginActivity.this, "正在登录");
            }

            @Override
            protected void onNetOrServerFailure() {
                MyProgressDialog.closeDialog();

                // 有网
                if (CommonUtil.checkNetworkAvailable(getApplicationContext())) {
                    if (getApplicationContext() == null) {
                        return;
                    }
                    return;
                }

                // 无网络
                String staffname = etAccount.getText().toString().trim();
                String jsonStr = CommonUtil.spGetString(getApplicationContext(), staffname);
                if (TextUtils.isEmpty(jsonStr)) {
                    if (getApplicationContext() == null) {
                        return;
                    }
                } else {
                    UserInfoResult.Data data = JsonUtil.fromJson(jsonStr, UserInfoResult.Data.class);
                    if (System.currentTimeMillis() - data.time > 0 && System.currentTimeMillis() - data.time < 86400000L * 30L) { // 登录状态不超时

                        if (!TextUtils.equals(etPassword.getText().toString().trim(), data.password)) {
                            if (getApplicationContext() == null) {
                                return;
                            }
                            return;
                        }

                        Constant.saveUserInfo(getApplicationContext(), data);

                        // 记住登录状态
                        if (cbAutoLogin.isChecked()) {
                            CommonUtil.spPutString(getApplicationContext(), Constant.Key.CURRENT_USER, data.staffname);
                        } else {
                            CommonUtil.spPutString(getApplicationContext(), Constant.Key.CURRENT_USER, "");
                        }

                        new Thread() {
                            public void run() {
                                // 更新文件的下载状态
                                MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
                                StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
//								Constant.newUpdate = MyDB.getInstance(getApplicationContext()).getResourceCount(Constant.getCurrentUser(getApplicationContext()).staffid);
//								CommonUtil.spPutInt(getApplicationContext(), "newUpdate", Constant.newUpdate);
                                mHandler.sendEmptyMessage(OPEN_HOME);
                            }

                            ;
                        }.start();
                    } else { // 登录状态超时
                        if (getApplicationContext() == null) {
                            return;
                        }
                    }
                }
            }

            @Override
            protected void onNetAndServerSuccess() {
                MyProgressDialog.closeDialog();
            }

            @Override
            public void handleResult(UserInfoResult result) {
                switch (result.result) {
                    case 200: {
                        result.data.time = System.currentTimeMillis();
                        result.data.password = etPassword.getText().toString().trim();

                        CommonUtil.spPutString(getApplicationContext(), result.data.staffname, JsonUtil.toJson(result.data));
                        CommonUtil.spPutString(getApplicationContext(), "accessKeyID", result.data.accessKeyID);
                        CommonUtil.spPutString(getApplicationContext(), "secretKey", result.data.secretKey);
                        CommonUtil.spPutString(getApplicationContext(), "bucketName", result.data.bucketName);

                        SplashActivity.accessKeyID = CommonUtil.spGetString(getApplicationContext(), "accessKeyID");
                        SplashActivity.secretKey = CommonUtil.spGetString(getApplicationContext(), "secretKey");
                        SplashActivity.bucketName = CommonUtil.spGetString(getApplicationContext(), "bucketName");
                        Util.initS3Client(SplashActivity.accessKeyID, SplashActivity.secretKey
                                , SplashActivity.bucketName);

                        if (cbAutoLogin.isChecked()) {
                            CommonUtil.spPutString(getApplicationContext(), Constant.Key.CURRENT_USER, result.data.staffname);
                        } else {
                            CommonUtil.spPutString(getApplicationContext(), Constant.Key.CURRENT_USER, "");
                        }

                        Constant.saveUserInfo(getApplicationContext(), result.data);

                        // 是否有版本号
                        String version = CommonUtil.spGetString(getApplicationContext(), result.data.staffname + "_version");
                        if (TextUtils.isEmpty(version)) {
                            Intent intent = new Intent(LoginActivity.this, FirstUpdateActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            new Thread() {
                                public void run() {
                                    StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
                                    MyDB.getInstance(getApplicationContext()).checkDownloadState(Constant.getCurrentUser(getApplicationContext()).staffid, Constant.getCurrentUser(getApplicationContext()).role);
//								Constant.newUpdate = MyDB.getInstance(getApplicationContext()).getResourceCount(Constant.getCurrentUser(getApplicationContext()).staffid);
//								CommonUtil.spPutInt(getApplicationContext(), "newUpdate", Constant.newUpdate);
                                    mHandler.sendEmptyMessage(OPEN_HOME);
                                }

                                ;
                            }.start();
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
            case R.id.bt_login: {
                requestLogin();
//			openHome();
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
                if (getApplicationContext() == null) {
                    return super.onKeyDown(keyCode, event);
                }
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
