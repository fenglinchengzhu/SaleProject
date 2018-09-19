package com.goldwind.app.help.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.DocumentContentActivity;
import com.goldwind.app.help.activity.KnowledgeBaikeActivity;
import com.goldwind.app.help.activity.NewsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * find
 */
public class FindFragment extends BaseFragment {
    private static final int ZAIXIAN_INDEX = 2;
    private static final int WHAT_CLOSE_POP = 1100;
    @Bind(R.id.ll_reports)
    LinearLayout llReports;
    @Bind(R.id.ll_document)
    RelativeLayout llDocument;
    @Bind(R.id.ll_baike)
    RelativeLayout llBaike;
    @Bind(R.id.ll_news)
    RelativeLayout llNews;
    @Bind(R.id.ll_question_upload)
    LinearLayout llQuestionUpload;
    @Bind(R.id.ll_question_neighbor)
    LinearLayout llQuestionNeighbor;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    private boolean isSnackBar = false;
    private BroadcastReceiver receiver;
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        ButterKnife.bind(this, view);
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


    }

    @Override
    protected void initListener() {
        llReports.setOnClickListener(this);
        llDocument.setOnClickListener(this);
        llBaike.setOnClickListener(this);
        llNews.setOnClickListener(this);
        llQuestionUpload.setOnClickListener(this);
        llQuestionNeighbor.setOnClickListener(this);


    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_reports: {
                break;
            }
            case R.id.ll_document: {
                Intent intent = new Intent(getActivity(), DocumentContentActivity.class);
                intent.putExtra("tagIndex", ZAIXIAN_INDEX);
                startActivity(intent);
                //ToastUtil.showToastLong(getActivity(),"建设中...");
                break;
            }
            case R.id.ll_news: {
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                startActivity(intent);

                break;
            }
            case R.id.ll_baike: {
                Intent intent = new Intent(getActivity(), KnowledgeBaikeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ll_question_upload: {
//				Intent intent = new Intent(getActivity(), AdviceActivity.class);
//				getActivity().startActivity(intent);
                break;
            }
            case R.id.ll_question_neighbor: {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
