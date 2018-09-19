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
import com.goldwind.app.help.model.SearchResult;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.SildingFinishLayout;
import com.goldwind.app.help.view.SildingFinishLayout.OnSildingFinishListener;
import com.goldwind.app.help.view.XListView;
import com.goldwind.app.help.view.XListView.IXListViewListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xiaopan.sketch.SketchImageView;

public class FenLeiNullActivity extends BaseActivity implements IXListViewListener {
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.lv_tui_jian)
    XListView lvTuiJian;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    @Bind(R.id.view_white)
    View view_white;
    @Bind(R.id.iv_pic_anim)
    ImageView iv_pic_anim;

    private boolean isSnackBar = false;

    private ReceiveBroadCast mReceiveBroadCast;

    private ArrayList<GetResourcesResult.ResourceItem> resourceItemList;

    private Animation alphaAnimation;
    private int x;
    private int y;
    private int h;
    private Handler mHandler;

    private int lastStart = 0;
    private int page = 0;

    private MyAdapter myAdapter;

    private String pid;

    private SildingFinishLayout mSildingFinishLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fen_lei_null);
        ButterKnife.bind(this);

        mSildingFinishLayout = (SildingFinishLayout) findViewById(R.id.sildingFinishLayout);
        mSildingFinishLayout.setOnSildingFinishListener(new OnSildingFinishListener() {
            @Override
            public void onSildingFinish() {
                finish();
            }
        });


        initAll();


        mReceiveBroadCast = new ReceiveBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("FenLeiNullActivityBroadCast");
        registerReceiver(mReceiveBroadCast, filter);
    }

    private void setTouchView() {
        if (resourceItemList != null && resourceItemList.size() > 0) {
            mSildingFinishLayout.setTouchView(lvTuiJian);
        } else {
            mSildingFinishLayout.setTouchView(mSildingFinishLayout);
        }
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

        pid = getIntent().getStringExtra("pid");

        resourceItemList = new ArrayList<ResourceItem>();
        myAdapter = new MyAdapter();

    }

    @Override
    protected void initViews() {
        tv_title.setText(getIntent().getStringExtra("name"));
        TextView emptyView = new TextView(FenLeiNullActivity.this);
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) lvTuiJian.getParent()).addView(emptyView);
        lvTuiJian.setEmptyView(emptyView);
    }

    private void openDetailPage(ResourceItem resourceItem) {
    }

    @Override
    protected void initListener() {
        ivBack.setOnClickListener(this);

        lvTuiJian.setXListViewListener(this);

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

        lvTuiJian.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);

                final ResourceItem item = MyDB.getInstance(getApplicationContext()).getResourceById(Constant.getCurrentUser(getApplicationContext()).staffid,
                        resourceItem.resourcesid + "");

                if (item == null) {
                    MyDB.getInstance(getApplicationContext()).insert(getApplicationContext(), resourceItem, Constant.getCurrentUser(getApplicationContext()).staffid);
                    ResourceItem item1 = MyDB.getInstance(getApplicationContext()).getResourceById(
                            Constant.getCurrentUser(getApplicationContext()).staffid, resourceItem.resourcesid + "");
                    if (item1 == null) {
                        if (getApplicationContext()
                                == null) {
                            return;
                        }
                    } else {
                        openDetailPage(item1);
                    }
                    return;
                }


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
                            openDetailPage(item);
                        }
                    }, 450);
                } else {
                    openDetailPage(item);
                }
            }
        });
    }

    @Override
    protected void initData() {
        setTouchView();

        lvTuiJian.setAdapter(myAdapter);

        onRefresh();
    }


    private void requestGetCategoryResources() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid", Constant.getCurrentUser(getApplicationContext()).staffid);
        map.put("categoryid", pid);
        map.put("limit", Constant.getPageSize() + "");
        map.put("start", lastStart + "");
        ApiUtil.request(new ApiUtil.MyHttpRequest<SearchResult>(getApplicationContext(),
                Constant.Api.GET_CATEGORY_RESOURCES, ApiUtil.genRequestMap(map)) {

            @Override
            protected void onRequestStart() {
                MyProgressDialog.showDialog(FenLeiNullActivity.this, "加载中");
            }

            @Override
            protected void onNetOrServerFailure() {
                onLoad();
                MyProgressDialog.closeDialog();
            }

            @Override
            protected void onNetAndServerSuccess() {
                onLoad();
                MyProgressDialog.closeDialog();
            }

            @Override
            public void handleResult(SearchResult result) {
                switch (result.result) {
                    case 200: {
                        if (result.data != null && result.data.resources != null && result.data.resources.size() > 0) {
                            lastStart = result.data.start;
                            ArrayList<GetResourcesResult.ResourceItem> list = new ArrayList<GetResourcesResult.ResourceItem>();
                            for (SearchResult.ResourceItem item : result.data.resources) {
                                GetResourcesResult.ResourceItem item2 = new GetResourcesResult.ResourceItem();
                                item2.classid = item.classid;
                                item2.cover = item.cover;
                                item2.isrecommend = item.isrecommend;
                                item2.labelid = item.labelid;
                                item2.status = item.status;
                                item2.description = item.description;
                                item2.isdelete = item.isdelete;
                                item2.fileaddress = item.fileaddress;
                                item2.type = item.type;
                                item2.resourcesid = item.resourcesid;
                                item2.createTime = item.createTime;
                                item2.name = item.name;
                                item2.version = item.version;
                                item2.downloadState = item.downloadState;
                                item2.staffid = item.staffid;
                                item2.groupid = item.groupid;
                                item2.readTime = item.readTime;
                                item2.size = item.size;
                                list.add(item2);
                            }
                            if (page == 0) {
                                resourceItemList.clear();
                            }
                            resourceItemList.addAll(list);
                            if (resourceItemList.size() > 0) {
                                lvTuiJian.showFooterView();
                            } else {
                                lvTuiJian.hideFooterView();
                            }
                            setTouchView();
                            myAdapter.notifyDataSetChanged();
                        } else {
                            if (page != 0) {
                                if (getApplicationContext()
                                        == null) {
                                    return;
                                }
                            } else {
                                resourceItemList.clear();
                                if (getApplicationContext()
                                        == null) {
                                    return;
                                }
                            }
                            setTouchView();
                            myAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    default: {
                        if (getApplicationContext()
                                == null) {
                            return;
                        }
                        break;
                    }
                }
            }
        });
    }

    private void onLoad() {
        lvTuiJian.stopRefresh();
        lvTuiJian.stopLoadMore();
        lvTuiJian.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh() {
        page = 0;
        lastStart = 0;
        requestGetCategoryResources();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestGetCategoryResources();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (resourceItemList == null || resourceItemList.size() == 0) {
                lvTuiJian.hideFooterView();
                return 0;
            }
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
                convertView = View.inflate(FenLeiNullActivity.this, R.layout.layout_item_resource_list, null);
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
