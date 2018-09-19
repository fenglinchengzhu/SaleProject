package com.goldwind.app.help;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.goldwind.app.help.loading.KProgressHUD;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * FragmentActivity的基类
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements OnClickListener {

    private boolean isActive = true;
    private KProgressHUD hud;

    public void initAll() {
        initParam();
        initViews();
        initListener();
        initData();
    }

    /**
     * 初始化变量
     */
    protected abstract void initParam();

    /**
     * 初始化View
     */
    protected abstract void initViews();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 点击事件
     */
    public abstract void onClick(View v);

    @TargetApi(19)
    public void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void onResume() {
        super.onResume();
        if (!isActive) {
            isActive = true;
            Intent intent = new Intent();
            intent.putExtra("data", "request");
            intent.setAction("AwsJinFengDownloadServiceBroadcast");
            sendBroadcast(intent);
        }
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            isActive = false;
        }
    }

    /**
     * 程序是否在前台运行
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    protected void showLoadingDialog() {
        if (hud == null) {
            hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        hud.show();
    }

    protected void dismissLoadingDialog() {
        if (hud != null && hud.isShowing())
            hud.dismiss();

    }
}
