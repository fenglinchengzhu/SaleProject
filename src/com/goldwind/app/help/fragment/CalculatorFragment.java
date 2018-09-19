package com.goldwind.app.help.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.CalculatorInputActivity;
import com.goldwind.app.help.util.LogUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 本地
 */
public class CalculatorFragment extends BaseFragment {
    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.gv_all)
    GridView gvAll;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    @Bind(R.id.iv_pic_anim)
    ImageView iv_pic_anim;
    @Bind(R.id.view_white)
    View view_white;

    // 分类名
    private String className = "全部";
    private ArrayList<String> itemList;
    private ItemAdapter allItemAdapter;

    private PopupWindow deleteDialogPopupWindow;

    private Animation alphaAnimation;

    private Handler mHandler;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculator, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rlTopMenu.getLayoutParams();
            int h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            layoutParams.topMargin = h;
//			rlTopMenu.setBackgroundResource(R.color.c6);
            rlTopMenu.setLayoutParams(layoutParams);
        }
        initAll();

        System.gc();
        System.gc();

        return view;
    }

    @Override
    public void onResume() {
        refreshData();
        view_white.setVisibility(View.GONE);
        iv_pic_anim.setVisibility(View.GONE);
        allItemAdapter.notifyDataSetChanged();
        System.gc();
        System.gc();
        super.onResume();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initParam() {
        itemList = new ArrayList<String>();
        mHandler = new Handler();
        alphaAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.alpha);
    }

    private void refreshData() {
        LogUtil.d("refreshData");
        itemList = new ArrayList<String>();
        itemList.add("1.5MW计算器");
        itemList.add("2.0MW计算器");
        itemList.add("2.5MW计算器");
        itemList.add("3.0MW计算器");
        itemList.add("我的计算模版");
        itemList.add("计算历史保留");
    }


    @Override
    protected void initViews() {

        TextView emptyView = new TextView(getActivity());
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) gvAll.getParent()).addView(emptyView);
        gvAll.setEmptyView(emptyView);

    }

    @Override
    protected void initListener() {
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                iv_pic_anim.setVisibility(View.VISIBLE);
                view_white.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_pic_anim.setVisibility(View.GONE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view_white.setVisibility(View.GONE);
                    }
                }, 400);
            }
        });


        gvAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                final View v = view.findViewById(R.id.rl_item);
                PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f, 1f);
                PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f, 1f);
                ObjectAnimator.ofPropertyValuesHolder(v, pvhX, pvhY).setDuration(1000).start();
                String item = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra(Constant.EXTRA.CALCULATOR_NAME, item);
                intent.setClass(mContext, CalculatorInputActivity.class);
                startActivity(intent);
            }
        });

    }

    private void openDetailPage(String resourceItem) {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void initData() {
        refreshData();


        allItemAdapter = new ItemAdapter();


        gvAll.setAdapter(allItemAdapter);

    }

    private void showDeleteDialogPop() {
        if (deleteDialogPopupWindow == null) {
            View view = View.inflate(getActivity(), R.layout.pop_delete_dialog, null);
            Button bt_dialog_left = (Button) view.findViewById(R.id.bt_delete_dialog_left);
            Button bt_dialog_right = (Button) view.findViewById(R.id.bt_delete_dialog_right);
            bt_dialog_left.setOnClickListener(this);
            bt_dialog_right.setOnClickListener(this);
            deleteDialogPopupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            deleteDialogPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            deleteDialogPopupWindow.setFocusable(true);
            deleteDialogPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
        }
//		deleteDialogPopupWindow.showAtLocation(llBottom, Gravity.BOTTOM, 0, 0);
    }


    private class ItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.layout_item_text_image, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
                convertView.setMinimumWidth(300);
                convertView.setMinimumHeight(250);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv_name.setText(itemList.get(position));
            holder.tv_name.setTextColor(0xff4f4f4f);
            return convertView;
        }

        private class ViewHolder {
            private ImageView iv_icon;
            private TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
            }
        }
    }
}
