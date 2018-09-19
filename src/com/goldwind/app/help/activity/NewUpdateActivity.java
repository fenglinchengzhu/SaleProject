package com.goldwind.app.help.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseActivity;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.view.SildingFinishLayout;
import com.goldwind.app.help.view.SildingFinishLayout.OnSildingFinishListener;
import com.goldwind.app.help.view.XListView;
import com.goldwind.app.help.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xiaopan.sketch.SketchImageView;

public class NewUpdateActivity extends BaseActivity implements IXListViewListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.lv_new_update)
    XListView lv_new_update;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    @Bind(R.id.iv_pic_anim)
    ImageView iv_pic_anim;
    @Bind(R.id.view_white)
    View view_white;

    private ArrayList<GetResourcesResult.ResourceItem> resourceItemList;

    private Animation alphaAnimation;

    private int x;
    private int y;
    private int h;

    private Handler mHandler;

    private ReceiveBroadCast mReceiveBroadCast;

    private ArrayList<GetResourcesResult.ResourceItem> allResourceItemList;

    private int page = 0;
    private boolean hasMore = true;

    private NewUpdateAdapter myAdapter;

    private boolean isSnackBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_update);
        ButterKnife.bind(this);


        initAll();

        SildingFinishLayout mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                finish();
            }
        });
        mSildingFinishLayout.setTouchView(lv_new_update);

        Constant.newUpdate = 0;
        CommonUtil.spPutInt(getApplicationContext(), Constant.getCurrentUser(getApplicationContext()).staffname + "_newUpdate", Constant.newUpdate);

        mReceiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("NewUpdateActivityBroadCast");
        registerReceiver(mReceiveBroadCast, filter);
    }

    @Override
    protected void onDestroy() {
        if (mReceiveBroadCast != null) {
            try {
                unregisterReceiver(mReceiveBroadCast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        view_white.setVisibility(View.GONE);
        iv_pic_anim.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    protected void initParam() {
        x = CommonUtil.sp2px(getApplicationContext(), 8);
        y = CommonUtil.sp2px(getApplicationContext(), 8 + 50 + 2);
        h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));

        alphaAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);

        mHandler = new Handler();

        resourceItemList = new ArrayList<GetResourcesResult.ResourceItem>();

        allResourceItemList = MyDB.getInstance(getApplicationContext()).getResourceList(Constant.getCurrentUser(getApplicationContext()).staffid);

        myAdapter = new NewUpdateAdapter();
    }

    @Override
    protected void initViews() {
        TextView emptyView = new TextView(NewUpdateActivity.this);
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) lv_new_update.getParent()).addView(emptyView);
        lv_new_update.setEmptyView(emptyView);
    }

    @Override
    protected void initListener() {
        ivBack.setOnClickListener(this);

        lv_new_update.setXListViewListener(this);

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
        lv_new_update.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);
                if (resourceItem.type != 3) {
                    ImageView iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
                    iv_pic.setDrawingCacheEnabled(true);
                    Bitmap obmp = Bitmap.createBitmap(iv_pic.getDrawingCache());
                    iv_pic.setDrawingCacheEnabled(false);
                    iv_pic_anim.setImageBitmap(obmp);

                    int[] location = new int[2];
                    iv_pic.getLocationOnScreen(location);
                    int aaa = CommonUtil.dip2px(getApplicationContext(), 8);
                    TranslateAnimation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, location[0] - aaa, TranslateAnimation.ABSOLUTE,
                            x, TranslateAnimation.ABSOLUTE, location[1] - aaa, TranslateAnimation.ABSOLUTE, y + h);
                    translateAnimation.setDuration(500);

                    iv_pic_anim.startAnimation(translateAnimation);
                    view_white.startAnimation(alphaAnimation);
                    iv_pic_anim.setVisibility(View.VISIBLE);
                    view_white.setVisibility(View.VISIBLE);

                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            openDetailPage(resourceItem);
                        }
                    }, 450);
                } else {
                    openDetailPage(resourceItem);
                }
            }
        });
    }

    private void openDetailPage(ResourceItem resourceItem) {
    }

    private void loadData() {
        for (int i = page * Constant.getPageSize(); i < (page + 1) * Constant.getPageSize(); i++) {
            if (i >= allResourceItemList.size()) {
                if (i == page * Constant.getPageSize()) {
                    hasMore = false;
                }
                break;
            }
            ResourceItem item = allResourceItemList.get(i);
            resourceItemList.add(item);
        }
    }

    @Override
    protected void initData() {

        lv_new_update.setAdapter(myAdapter);

//		onRefresh();
        resourceItemList.clear();
        page = 0;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                resourceItemList.clear();
                loadData();
                onLoad();
                if (resourceItemList.size() > 0) {
                    lv_new_update.showFooterView();
                } else {
                    lv_new_update.hideFooterView();
                }
                myAdapter.notifyDataSetChanged();
            }
        }, 50);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void onLoad() {
        lv_new_update.stopRefresh();
        lv_new_update.stopLoadMore();
        lv_new_update.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

    @Override
    public void onRefresh() {

        page = 0;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                resourceItemList.clear();
                loadData();
                onLoad();
                if (resourceItemList.size() > 0) {
                    lv_new_update.showFooterView();
                } else {
                    lv_new_update.hideFooterView();
                }
                myAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    @Override
    public void onLoadMore() {
        page++;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                loadData();
                onLoad();
                if (resourceItemList.size() > 0) {
                    lv_new_update.showFooterView();
                } else {
                    lv_new_update.hideFooterView();
                }
                if (page != 0 && !hasMore) {
                    if (getApplicationContext() == null) {
                        return;
                    }
                }
                myAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    class NewUpdateAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return resourceItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return resourceItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(NewUpdateActivity.this, R.layout.layout_item_resource_list, null);
                holder = new ViewHolder();
                holder.iv_pic = (SketchImageView) convertView.findViewById(R.id.iv_pic);
                holder.iv_video_tag = (ImageView) convertView.findViewById(R.id.iv_video_tag);
                holder.iv_type_tag = (ImageView) convertView.findViewById(R.id.iv_type_tag);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            GetResourcesResult.ResourceItem resourceItem = resourceItemList.get(position);
            holder.iv_pic.setImageBitmap(null);

            if (resourceItem.type == 3) {
                // 下载
                ApiUtil.downloadPic(resourceItem.fileaddress.split(",")[0], holder.iv_pic);
            } else {
                ApiUtil.downloadFengMian(resourceItem.cover, holder.iv_pic);
            }

            holder.tv_name.setText(resourceItem.name);
            holder.tv_description.setText(resourceItem.description);
            if (resourceItem.type == 2) {
                holder.iv_video_tag.setVisibility(View.VISIBLE);
                holder.iv_type_tag.setVisibility(View.VISIBLE);
                holder.iv_type_tag.setImageResource(R.drawable.img_video_tag_font);
            } else if (resourceItem.type == 1) {
                holder.iv_video_tag.setVisibility(View.GONE);
                holder.iv_type_tag.setVisibility(View.VISIBLE);
                holder.iv_type_tag.setImageResource(R.drawable.img_doc_tag);
            } else if (resourceItem.type == 3) {
                holder.iv_video_tag.setVisibility(View.GONE);
                holder.iv_type_tag.setVisibility(View.VISIBLE);
                holder.iv_type_tag.setImageResource(R.drawable.img_pic_tag);
            }
            return convertView;
        }

        private class ViewHolder {
            SketchImageView iv_pic;
            ImageView iv_video_tag;
            ImageView iv_type_tag;
            TextView tv_description;
            TextView tv_name;
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("data");
            if (TextUtils.equals(message, "RoleChange")) {
                if (getApplicationContext() == null) {
                    return;
                }
            }
        }
    }
}
