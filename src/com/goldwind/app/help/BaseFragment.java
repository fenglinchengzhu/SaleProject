package com.goldwind.app.help;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Fragment的基类
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

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
}
