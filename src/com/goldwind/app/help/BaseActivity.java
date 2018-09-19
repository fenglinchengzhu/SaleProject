package com.goldwind.app.help;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.goldwind.app.help.loading.KProgressHUD;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Activity的基类 new SnackBar.Builder(this).withMessage("再按一次退出程序")
 * .withDuration(SnackBar.SHORT_SNACK).show();
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

    private boolean isActive = true;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!this.isTaskRoot()) { // 判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            // 如果你就放在launcher Activity中话，这里可以直接return了
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;// finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
            }
        }
    }

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
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected void initData();

    /**
     * 点击事件
     */
    public abstract void onClick(View v);

    /**
     * 初始化View
     */
    protected abstract void initViews();

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
//		overridePendingTransition(0, R.anim.base_slide_right_out);
    }

    @Override
    public void finish() {
        super.finish();
//		overridePendingTransition(0, R.anim.base_slide_right_out);
    }

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
