package com.goldwind.app.help.fragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.AboutMeActivity;
import com.goldwind.app.help.activity.LoginActivity;
import com.goldwind.app.help.activity.ReadRoleActivity;
import com.goldwind.app.help.model.GetApkVersionResult;
import com.goldwind.app.help.model.GetApkVersionResult.Data;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.util.LogUtil;
import com.goldwind.app.help.util.ToastUtil;
import com.goldwind.app.help.view.MyProgressDialog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人
 */
public class PersonFragment extends BaseFragment {
    private static final int WHAT_CLOSE_POP = 1100;
    @Bind(R.id.ll_about_me)
    RelativeLayout llAboutMe;
    @Bind(R.id.ll_station)
    LinearLayout llStation;
    @Bind(R.id.ll_test)
    LinearLayout llTest;
    @Bind(R.id.ll_read_role)
    RelativeLayout llReadRole;
    @Bind(R.id.ll_exit_login)
    RelativeLayout ll_exit_login;
    @Bind(R.id.ll_clear_data)
    LinearLayout ll_clear_data;
    @Bind(R.id.ll_update_app)
    RelativeLayout ll_update_app;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    private boolean isSnackBar = false;
    private BroadcastReceiver receiver;
    private Context mContext;
    private PopupWindow messagePopupWindow;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == WHAT_CLOSE_POP) {
                if (messagePopupWindow != null) {
                    messagePopupWindow.dismiss();
                }
            }
        }

        ;
    };
    private PopupWindow updateDialogPopupWindow;

    ;

    public static int getAPPVersionCodeFromAPP(Context ctx) {
        int currentVersionCode = 0;
        PackageManager manager = ctx.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
            String appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
            System.out.println(currentVersionCode + " " + appVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch blockd
            e.printStackTrace();
        }
        return currentVersionCode;
    }

    @Override
    public void onResume() {
        refreshRoleName();
        super.onResume();
    }

    public void refreshRoleName() {
        LogUtil.d("refreshRoleName");
//		int role = Integer.valueOf(Constant.getCurrentUser(getActivity().getApplicationContext()).role);
//		String roleName = HomeActivity.getRoleName(role);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getActivity();
        initAll();

        System.gc();
        System.gc();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlTopMenu.getLayoutParams();
            int h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            layoutParams.topMargin = h;
//			rlTopMenu.setBackgroundResource(R.color.c7);
            rlTopMenu.setLayoutParams(layoutParams);
            isSnackBar = true;
        }
        return view;
    }

    @Override
    protected void initParam() {
    }

    @Override
    protected void initViews() {

        String roleName = "";
//		if (TextUtils.equals(Constant.getCurrentUser(getActivity().getApplicationContext()).role, "1")) {
//			roleName = "公开";
//		} else if (TextUtils.equals(Constant.getCurrentUser(getActivity().getApplicationContext()).role, "2")) {
//			roleName = "秘密";
//		} else if (TextUtils.equals(Constant.getCurrentUser(getActivity().getApplicationContext()).role, "3")) {
//			roleName = "机密";
//		} else if (TextUtils.equals(Constant.getCurrentUser(getActivity().getApplicationContext()).role, "4")) {
//			roleName = "绝密";
//		}

    }

    @Override
    protected void initListener() {
        llAboutMe.setOnClickListener(this);
        llStation.setOnClickListener(this);
        llTest.setOnClickListener(this);
        llReadRole.setOnClickListener(this);
        ll_exit_login.setOnClickListener(this);
        ll_clear_data.setOnClickListener(this);
        ll_update_app.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    private void showMessagePop(String str) {
        if (messagePopupWindow == null) {
            View view = View.inflate(getActivity(), R.layout.pop_message_dialog, null);
            TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
            tv_text.setText(str);
            messagePopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            messagePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            messagePopupWindow.setFocusable(true);
            messagePopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        }
        messagePopupWindow.showAtLocation(ll_clear_data, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(WHAT_CLOSE_POP, 2000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about_me: {
                Intent intent = new Intent(getActivity(), AboutMeActivity.class);
                getActivity().startActivity(intent);
                break;
            }
            case R.id.ll_station: {
                break;
            }
            case R.id.ll_test: {
                break;
            }
            case R.id.ll_read_role: {
                Intent intent = new Intent(getActivity(), ReadRoleActivity.class);
                getActivity().startActivity(intent);
                break;
            }
            case R.id.ll_clear_data: {
                if (getActivity() == null || getActivity().getApplicationContext() == null) {
                    return;
                }
                break;
            }
            case R.id.ll_update_app: {
                requestGetApkVersion();
                break;
            }
            case R.id.bt_update_dialog_left: {
                if (updateDialogPopupWindow != null) {
                    updateDialogPopupWindow.dismiss();
                }
                break;
            }
            case R.id.bt_update_dialog_right: {
                Data data = (Data) v.getTag();
                if (data != null) {
                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(getActivity().DOWNLOAD_SERVICE);
                    String apkUrl = data.address;
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
                    request.setDestinationInExternalPublicDir("/jinfeng/", "JinFeng" + data.id + ".apk");
                    request.setTitle("金风风机课堂");
                    request.setDescription("金风风机课堂");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    final long myreference = downloadManager.enqueue(request);
                    ToastUtil.showToastShort(getActivity().getApplicationContext(), "正在下载…");

                    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                            if (reference == myreference) {
                                String serviceString = Context.DOWNLOAD_SERVICE;
                                try {
                                    DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
                                    Intent install = new Intent(Intent.ACTION_VIEW);
                                    Uri downloadFileUri = dManager.getUriForDownloadedFile(myreference);
                                    install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(install);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    };
                    getActivity().registerReceiver(receiver, filter);
                }
                if (updateDialogPopupWindow != null) {
                    updateDialogPopupWindow.dismiss();
                }
                break;
            }
            case R.id.ll_exit_login: {
                CommonUtil.spPutString(getActivity().getApplicationContext(), Constant.Key.CURRENT_USER, "");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (receiver != null) {
                getActivity().unregisterReceiver(receiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private void requestGetApkVersion() {
        MyProgressDialog.showDialog(getActivity(), "请求中");
        LoadDataFromServer task = new LoadDataFromServer(mContext, Constant.Api.GEY_UPDATE_URL.trim());
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(JSONObject data) {
                MyProgressDialog.closeDialog();
                Log.i("PersonFragment", data.toString());
                try {
                    String t_version = data.getString("version");
                    Log.i("PersonFragment", "t_version is:" + t_version);
                    int verison = Integer.parseInt(t_version);//data.getInteger("version");
                    if (getAPPVersionCodeFromAPP(mContext) < verison) {
                        GetApkVersionResult result = new GetApkVersionResult();
                        result.data = new GetApkVersionResult.Data();
                        result.data.address = Constant.Api.GOLD_DOMAIN + data.getString("url");
                        result.data.id = verison;
                        showUpdateDialogPop(result.data);
                    } else {
                        if (getActivity() == null || getActivity().getApplicationContext() == null) {
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showUpdateDialogPop(Data data) {
        if (updateDialogPopupWindow == null) {
            View view = View.inflate(getActivity(), R.layout.pop_update_dialog, null);
            Button bt_dialog_left = (Button) view.findViewById(R.id.bt_update_dialog_left);
            Button bt_dialog_right = (Button) view.findViewById(R.id.bt_update_dialog_right);
            bt_dialog_left.setOnClickListener(this);
            bt_dialog_right.setOnClickListener(this);
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (updateDialogPopupWindow != null) {
                        updateDialogPopupWindow.dismiss();
                    }
                }
            });
            bt_dialog_right.setTag(data);
            updateDialogPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            updateDialogPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            updateDialogPopupWindow.setFocusable(true);
            updateDialogPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        }
        updateDialogPopupWindow.showAtLocation(llAboutMe, Gravity.CENTER, 0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
