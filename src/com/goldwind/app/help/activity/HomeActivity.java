package com.goldwind.app.help.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragmentActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.download.StorageUtils;
import com.goldwind.app.help.fragment.BaikeFragment;
import com.goldwind.app.help.fragment.ContactsFragment;
import com.goldwind.app.help.fragment.FindFragment;
import com.goldwind.app.help.fragment.NewCalculatorFragment;
import com.goldwind.app.help.fragment.PersonFragment;
import com.goldwind.app.help.model.GetApkVersionResult;
import com.goldwind.app.help.model.GetApkVersionResult.Data;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.SystemBarTintManager;
import com.goldwind.app.help.util.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 首页
 */
public class HomeActivity extends BaseFragmentActivity {

    public static final String tag1 = "1";
    public static final String tag2 = "2";
    public static final String tag3 = "3";
    public static final String tag4 = "4";
    public static final String tag5 = "5";
    private static final int OPEN_LOGIN = 1001;
    public String curFragmentTag = "";
    @Bind(R.id.fragment_container)
    RelativeLayout fragment_container;
    @Bind(R.id.ll_menu_baike)
    LinearLayout llMenuBaiKe;
    @Bind(R.id.iv_menu_baike)
    ImageView ivMenuBaiKe;
    @Bind(R.id.tv_menu_baike)
    TextView tvMenuBaiKe;
    @Bind(R.id.ll_menu_calculator)
    LinearLayout llMenuCalculator;
    @Bind(R.id.iv_menu_calculator)
    ImageView ivMenuCalculator;
    @Bind(R.id.tv_menu_calculator)
    TextView tvMenuCalculator;
    @Bind(R.id.ll_menu_contacts)
    LinearLayout llMenuContacts;
    @Bind(R.id.iv_menu_contacts)
    ImageView ivMenuContacts;
    @Bind(R.id.tv_menu_contacts)
    TextView tvMenuContacts;
    @Bind(R.id.ll_menu_find)
    LinearLayout llMenuFind;
    @Bind(R.id.iv_menu_find)
    ImageView ivMenuFind;
    @Bind(R.id.tv_menu_find)
    TextView tvMenuFind;
    @Bind(R.id.ll_menu_person)
    LinearLayout llMenuPerson;
    @Bind(R.id.iv_menu_person)
    ImageView ivMenuPerson;
    @Bind(R.id.tv_menu_person)
    TextView tvMenuPerson;
    String TAG = "HomeActivity";
    //	private LocalFragment localFragment;
    private PersonFragment personFragment;
    //	private ResourceFragment resourceFragment;
    private BaikeFragment baikeFragment;
    //	private CalculatorFragment calculatorFragment;
    private NewCalculatorFragment newCalculatorFragment;
    private ContactsFragment contactsFragment;
    private FindFragment findFragment;
    private boolean isSnackBar = false;
    private BroadcastReceiver receiver;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == OPEN_LOGIN) {
                Intent intent = new Intent(HomeActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        ;
    };
    private PopupWindow updateDialogPopupWindow;
    private long mExitTime;

    public static String getRoleName(int i) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super.setTranslucentStatus(true);
            isSnackBar = true;
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.c1);
        initAll();


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        personFragment = null;
        baikeFragment = null;
        newCalculatorFragment = null;
        contactsFragment = null;
        findFragment = null;
        StorageUtils.delete(new File(Constant.BASE_DECODE_PATH));
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initParam() {
//		Constant.newUpdate = CommonUtil.spGetInt(getApplicationContext(),
//				Constant.getCurrentUser(getApplicationContext()).staffname
//						+ "_newUpdate");

        curFragmentTag = "";
        baikeFragment = BaikeFragment.newInstance();
//		calculatorFragment = new CalculatorFragment();
        newCalculatorFragment = new NewCalculatorFragment();

        contactsFragment = new ContactsFragment();
        findFragment = new FindFragment();
        personFragment = new PersonFragment();
    }

    @Override
    protected void initViews() {
        ivMenuBaiKe.setSelected(true);
        tvMenuBaiKe.setTextColor(getResources().getColor(R.color.c7));
    }

    @Override
    protected void initListener() {
        llMenuBaiKe.setOnClickListener(this);
        llMenuCalculator.setOnClickListener(this);
        llMenuContacts.setOnClickListener(this);
        llMenuFind.setOnClickListener(this);
        llMenuPerson.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, baikeFragment)
                /*.add(R.id.fragment_container, newCalculatorFragment)
				.add(R.id.fragment_container, findFragment)
				.add(R.id.fragment_container, personFragment)
				.hide(newCalculatorFragment).hide(findFragment)
				.hide(personFragment)*/.show(baikeFragment).commit();
        curFragmentTag = tag1;
//		setTabSelection(tag1);
    }

    private void requestGetApkVersion() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid",
                Constant.getCurrentUser(getApplicationContext()).staffid);
        ApiUtil.request(new ApiUtil.MyHttpRequest<GetApkVersionResult>(
                getApplicationContext(), Constant.Api.GET_APK_VERSION, ApiUtil
                .genRequestMap(map)) {
            @Override
            protected void onRequestStart() {
            }

            @Override
            protected void onNetOrServerFailure() {
            }

            @Override
            protected void onNetAndServerSuccess() {
            }

            @Override
            public void handleResult(GetApkVersionResult result) {
                switch (result.result) {
                    case 200: {
                        if (result.data != null) {
                            int version = Integer.valueOf(ApiUtil._version);
                            if (result.data.id > version) {
                                showUpdateDialogPop(result.data);
                            }
                        }
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
    }

    private void showUpdateDialogPop(Data data) {
        if (updateDialogPopupWindow == null) {
            View view = View.inflate(this, R.layout.pop_update_dialog, null);
            Button bt_dialog_left = (Button) view
                    .findViewById(R.id.bt_update_dialog_left);
            Button bt_dialog_right = (Button) view
                    .findViewById(R.id.bt_update_dialog_right);
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
            updateDialogPopupWindow = new PopupWindow(view,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            updateDialogPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            updateDialogPopupWindow.setFocusable(true);
            updateDialogPopupWindow
                    .setAnimationStyle(R.style.popwin_anim_style);
        }
        updateDialogPopupWindow.showAtLocation(fragment_container, Gravity.CENTER, 0, 0);
    }

    // 切换
    private void setTabSelection(String tag) {
        if (!TextUtils.equals(tag, curFragmentTag)) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.hide(getFragment(curFragmentTag));
            if (!getFragment(tag).isAdded()) {
                trx.add(R.id.fragment_container, getFragment(tag));
            }
            trx.show(getFragment(tag)).commit();
            curFragmentTag = tag;
            switchMenu(tag);
        }
//		if (TextUtils.equals(tag, curFragmentTag)) {
//			return;
//		}
        curFragmentTag = tag;
        switchMenu(tag);
//		FragmentTransaction fragmentTransaction = getSupportFragmentManager()
//				.beginTransaction();
//		fragmentTransaction.replace(R.id.content, getFragment(tag), tag);
//		fragmentTransaction.commit();
    }

    private Fragment getFragment(String tag) {
        Log.i(TAG, "tag is:" + tag);
        if (TextUtils.equals(tag1, tag)) {
            return baikeFragment;
        } else if (TextUtils.equals(tag2, tag)) {
            return newCalculatorFragment;
        } else if (TextUtils.equals(tag3, tag)) {
            return contactsFragment;
        } else if (TextUtils.equals(tag4, tag)) {
            return findFragment;
        } else if (TextUtils.equals(tag5, tag)) {
            return personFragment;
        }
        return null;
    }

    private void switchMenu(String tag) {
        ivMenuBaiKe.setSelected(false);
        ivMenuCalculator.setSelected(false);
        ivMenuContacts.setSelected(false);
        ivMenuFind.setSelected(false);
        ivMenuPerson.setSelected(false);


        tvMenuBaiKe.setTextColor(Color.BLACK);
        tvMenuCalculator.setTextColor(Color.BLACK);
        tvMenuContacts.setTextColor(Color.BLACK);
        tvMenuFind.setTextColor(Color.BLACK);
        tvMenuPerson.setTextColor(Color.BLACK);

        if (TextUtils.equals(tag1, tag)) {
            ivMenuBaiKe.setSelected(true);
            //llMenuBaiKe.setBackgroundResource(R.drawable.img_menu_bg);
            tvMenuBaiKe.setTextColor(getResources().getColor(R.color.c7));
        } else if (TextUtils.equals(tag2, tag)) {
            //ivMenuCalculator.setImageResource(R.drawable.img_menu_person);
            ivMenuCalculator.setSelected(true);
            //llMenuCalculator.setBackgroundResource(R.drawable.img_menu_bg);
            tvMenuCalculator.setTextColor(getResources().getColor(R.color.c7));
        } else if (TextUtils.equals(tag3, tag)) {
            //ivMenuContacts.setImageResource(R.drawable.img_menu_person);
            ivMenuContacts.setSelected(true);
            //llMenuContacts.setBackgroundResource(R.drawable.img_menu_bg);
            tvMenuContacts.setTextColor(getResources().getColor(R.color.c7));
        } else if (TextUtils.equals(tag4, tag)) {
            //ivMenuFind.setImageResource(R.drawable.img_menu_person);
            ivMenuFind.setSelected(true);
            //llMenuFind.setBackgroundResource(R.drawable.img_menu_bg);
            tvMenuFind.setTextColor(getResources().getColor(R.color.c7));
        } else if (TextUtils.equals(tag5, tag)) {
            //ivMenuPerson.setImageResource(R.drawable.img_menu_person);
            ivMenuPerson.setSelected(true);
            //llMenuPerson.setBackgroundResource(R.drawable.img_menu_bg);
            tvMenuPerson.setTextColor(getResources().getColor(R.color.c7));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_update_dialog_left: {
                if (updateDialogPopupWindow != null) {
                    updateDialogPopupWindow.dismiss();
                }
                break;
            }
            case R.id.bt_update_dialog_right: {
                Data data = (Data) v.getTag();
                if (data != null) {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    String apkUrl = data.address;
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(apkUrl));
                    request.setDestinationInExternalPublicDir("/jinfeng/",
                            "JinFeng" + data.id + ".apk");
                    request.setTitle("金风营销系统");
                    request.setDescription("金风营销系统");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    final long myreference = downloadManager.enqueue(request);
                    ToastUtil.showToastShort(getApplicationContext(), "正在下载…");

                    IntentFilter filter = new IntentFilter(
                            DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                    receiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            long reference = intent.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                            if (reference == myreference) {
                                String serviceString = Context.DOWNLOAD_SERVICE;
                                DownloadManager dManager = (DownloadManager) context
                                        .getSystemService(serviceString);
                                Intent install = new Intent(Intent.ACTION_VIEW);
                                Uri downloadFileUri = dManager
                                        .getUriForDownloadedFile(myreference);
                                install.setDataAndType(downloadFileUri,
                                        "application/vnd.android.package-archive");
                                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(install);
                            }
                        }
                    };
                    registerReceiver(receiver, filter);
                }
                if (updateDialogPopupWindow != null) {
                    updateDialogPopupWindow.dismiss();
                }
                break;
            }
            case R.id.ll_menu_baike: {
                setTabSelection(tag1);
                break;
            }
            case R.id.ll_menu_calculator: {
                setTabSelection(tag2);
                break;
            }
            case R.id.ll_menu_contacts: {
//			setTabSelection(tag3);
                break;
            }
            case R.id.ll_menu_find: {
                setTabSelection(tag4);
                break;
            }
            case R.id.ll_menu_person: {
                setTabSelection(tag5);
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
