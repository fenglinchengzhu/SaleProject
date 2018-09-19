package com.goldwind.app.help.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.BaikeContentDetailActivity;
import com.goldwind.app.help.activity.KnowledgeBaikeActivity;
import com.goldwind.app.help.activity.SearchResultActivity;
import com.goldwind.app.help.adapter.ContentListAdapter;
import com.goldwind.app.help.adapter.SearchAdapter;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LoadDataFromServer;
import com.goldwind.app.help.view.XListView;
import com.goldwind.app.help.view.XListView.IXListViewListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 本地
 */
public class BaikeFragment extends BaseFragment implements IXListViewListener {
    private static final String LOG_TAG = "BaseFragment";
    private static final int PAGE_SIZE = 10;
    private static final String TAG = "BaikeFragment";
    private static BaikeFragment sInstance = new BaikeFragment();
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.btn_search)
    ImageButton btnSearch;
    @Bind(R.id.vp_recommend)
    ViewPager vpRecommend;
    @Bind(R.id.lv_main_recommend)
    XListView lvRecommend;

    //ImageLoader mImageLoader = ImageLoader.getInstance();
    @Bind(R.id.rl_top_menu)
    RelativeLayout rlTopMenu;
    @Bind(R.id.rl_recommend_title)
    TextView recommend_title;
    @Bind(R.id.root_view)
    RelativeLayout rootView;
    @Bind(R.id.ll_dot_layout)
    LinearLayout dotlayout;
    // 分类名
    private String className = "全部";
    private Animation alphaAnimation;
    private int x;
    private int y;
    private int h;
    private Handler mHandler;
    private Handler mImageHandler;
    private Context mContext;
    private List<View> mViews = new ArrayList<View>();
    private int lastStart = 0;
    private int page = 1;
    private ContentListAdapter myAdapter;
    private SearchAdapter searchAdapter;
    // 推荐
    private ArrayList<GetResourcesResult.BaikeItem> resourceItemList;
    private ArrayList<GetResourcesResult.BaikeItem> topItemList;
    private ArrayList<String> searchKeyWords = new ArrayList<String>();
    private boolean isSnackBar = false;
    private MyDB myDB;
    private ImageHandler handler = new ImageHandler(new WeakReference<BaikeFragment>(this));

    public static BaikeFragment newInstance() {
        if (sInstance == null) {
            sInstance = new BaikeFragment();
        }
        return sInstance;
    }

    /*@Override
    public void onResume() {
        super.onResume();
        mHandler.post(mCycleTask);
    }

    CycleTask mCycleTask = new CycleTask();

    class CycleTask implements Runnable{
        @Override
        public void run() {
            int pos = vpRecommend.getCurrentItem();
            vpRecommend.setCurrentItem((pos+1)%3);
            mHandler.postDelayed(this,1000*2);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mCycleTask);
    }*/

    /*public static BaikeFragment newInstance(){
        if(sInstance == null){
            sInstance = new BaikeFragment();
        }
        return sInstance;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_baike, container, false);
        ButterKnife.bind(this, view);
        mContext = this.getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rlTopMenu.getLayoutParams();
            int h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
            layoutParams.topMargin = h;
            rlTopMenu.setLayoutParams(layoutParams);
        }
        isSnackBar = true;

        initAll();


        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    protected void initParam() {
        x = CommonUtil.sp2px(getActivity().getApplicationContext(), 8);
        y = CommonUtil.sp2px(getActivity().getApplicationContext(), 8 + 50 + 2);
        h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        alphaAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.alpha);
        mHandler = new Handler();

        topItemList = new ArrayList<GetResourcesResult.BaikeItem>();

        resourceItemList = new ArrayList<GetResourcesResult.BaikeItem>();
        myDB = MyDB.getInstance(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    resourceItemList = myDB.getCacheBaike(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    topItemList = myDB.getCacheBaike(5);
                    Log.i(TAG, "topItemList.size() is:" + topItemList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.post(new DataTask());
            }
        }).start();


    }

    @Override
    protected void initViews() {
        TextView emptyView = new TextView(getActivity());
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) lvRecommend.getParent()).addView(emptyView);
        lvRecommend.setEmptyView(emptyView);
        vpRecommend.requestFocus();
    }

    @Override
    protected void initListener() {
        btnSearch.setOnClickListener(this);
        lvRecommend.setXListViewListener(this);

        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 400);
            }
        });

        lvRecommend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GetResourcesResult.BaikeItem resourceItem = (GetResourcesResult.BaikeItem) parent.getItemAtPosition(position);
                Intent detailIntent = new Intent();
                detailIntent.setClass(mContext, BaikeContentDetailActivity.class);
                detailIntent.putExtra("item", resourceItem);
                startActivity(detailIntent);

            }
        });


        vpRecommend.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, arg0, 0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE://不需要viewpager代替来操作，界面恢复后自行循环操作
                        //handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });

        etSearch.setOnClickListener(this);
    }

    @Override
    protected void initData() {


        requestRecommendBaike();
        requestRecommend();
    }

    private void refreshTopView() {
        ArrayList<ImageView> views = new ArrayList<ImageView>();
        for (int i = 0; i < topItemList.size(); i++) {
            ImageView imageview = new ImageView(mContext);
            imageview.setScaleType(ImageView.ScaleType.FIT_XY);
            //showPic(imageview, Constant.Api.GOLD_DOMAIN + topItemList.get(i).pic);
            ImageLoader.getInstance().displayImage(Constant.Api.GOLD_DOMAIN + topItemList.get(i).pic, imageview);
            views.add(imageview);
        }
        if (topItemList.size() > 0) {
            recommend_title.setText(topItemList.get(0).entry);
        }
        vpRecommend.setAdapter(new MyPagerAdapter(views));

        vpRecommend.setCurrentItem(0);//默认在中间，使用户看不到边界
        /*if(topItemList != null && topItemList.get(2)!=null) {
            recommend_title.setText(topItemList.get(2).entry);
        }*/
        //开始轮播效果
        handler.removeMessages(ImageHandler.MSG_UPDATE_IMAGE);
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
    }


    private void selectDot(int index) {
        if (topItemList != null && topItemList.get(index) != null) {
            recommend_title.setText(topItemList.get(index).entry);
        }
        for (int i = 0; i < dotlayout.getChildCount(); i++) {
            if (i == index) {
                ((ImageView) dotlayout.getChildAt(i)).setImageResource(R.drawable.index_slidercurrent_bg);
            } else {
                ((ImageView) dotlayout.getChildAt(i)).setImageResource(R.drawable.index_slidercircle_bg);
            }
        }
    }

    private void requestRecommendBaike() {
//        MyProgressDialog.showDialog(mContext, "加载中");
        String recommendUrl = Constant.Api.GET_RECOMMEND + "?number=" + page;
        LoadDataFromServer task = new LoadDataFromServer(mContext, recommendUrl);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(com.alibaba.fastjson.JSONObject data) {
                onLoad();
//                MyProgressDialog.closeDialog();
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    //Log.d(LOG_TAG, "result " + data.toString());
                    number = data.getInteger("num");


                    jsonArray = data.getJSONArray("data");

                    if (jsonArray != null && number > 0) {
                        lastStart = 0;
                        ArrayList<GetResourcesResult.BaikeItem> list = new ArrayList<GetResourcesResult.BaikeItem>();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            GetResourcesResult.BaikeItem item2 = new GetResourcesResult.BaikeItem();
                            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            item2.id = jsonObject.getInteger("id");
                            item2.entry = jsonObject.getString("entry");
                            item2.pic = jsonObject.getString("pic");
                            item2.summary = jsonObject.getString("summary");
                            item2.created_time = jsonObject.getString("created_time");
                            item2.catalog_id = jsonObject.getString("catalog_id");
                            list.add(item2);
                        }
                        if (page == 1) {
                            myDB.saveCacheBaike(list);
                            resourceItemList.clear();
                            //new SnackBar.Builder(getActivity(), isSnackBar).withMessage("刷新成功").withDuration(SnackBar.SHORT_SNACK).show();
//                            Toast.makeText(mContext, "刷新成功", Toast.LENGTH_LONG).show();
                        }

                        resourceItemList.addAll(list);
                        if (resourceItemList.size() > 0) {
                            lvRecommend.showFooterView();
                        } else {
                            lvRecommend.hideFooterView();
                        }
                        myAdapter.notifyDataSetChanged();
                    } else {
                        if (page != 1) {
                            if (getActivity() == null || getActivity().getApplicationContext() == null) {
                                return;
                            }
//                            new SnackBar.Builder(getActivity(), isSnackBar).withMessage("没有更多数据").withDuration(SnackBar.SHORT_SNACK).show();
                            lvRecommend.hideFooterView();
                        } else {
//                            resourceItemList.clear();
                            if (getActivity() == null || getActivity().getApplicationContext() == null) {
                                return;
                            }
//                            new SnackBar.Builder(getActivity(), isSnackBar).withMessage("暂无数据").withDuration(SnackBar.SHORT_SNACK).show();
                        }
                        myAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void requestRecommend() {
//        MyProgressDialog.showDialog(mContext, "加载中");
        LoadDataFromServer task = new LoadDataFromServer(mContext, Constant.Api.GET_TOP_IMAGE);
        task.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(com.alibaba.fastjson.JSONObject data) {
                onLoad();
//                MyProgressDialog.closeDialog();
                com.alibaba.fastjson.JSONArray jsonArray = null;
                int number = 0;
                try {
                    Log.d(LOG_TAG, "result " + data.toString());
                    number = data.getInteger("num");
                    jsonArray = data.getJSONArray("data");

                    if (jsonArray != null && number > 0) {
                        topItemList.clear();
                        for (int i = 0; i < jsonArray.size(); i++) {
                            GetResourcesResult.BaikeItem item2 = new GetResourcesResult.BaikeItem();
                            com.alibaba.fastjson.JSONObject jsonObject = jsonArray.getJSONObject(i);
                            item2.id = jsonObject.getInteger("id");
                            item2.entry = jsonObject.getString("entry");
                            item2.pic = jsonObject.getString("pic");
                            item2.summary = jsonObject.getString("summary");
                            if (jsonObject.containsKey("type")) {
                                item2.type = jsonObject.getString("type");
                            } else {
                                item2.type = "outlink";
                            }
                            item2.url = jsonObject.getString("url");

                            item2.created_time = jsonObject.getString("created_time");
                            //item2.catalog_id = jsonObject.getString("catalog_id");
                            if (topItemList.size() < 5) {//推荐临时显示5个
                                topItemList.add(item2);
                            }
                        }
                        refreshTopView();
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void onLoad() {
        lvRecommend.stopRefresh();
        lvRecommend.stopLoadMore();
        lvRecommend.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.removeMessages(ImageHandler.MSG_UPDATE_IMAGE);
        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(ImageHandler.MSG_UPDATE_IMAGE);
    }

    @Override
    public void onLoadMore() {
        page++;
        requestRecommendBaike();
    }

    @Override
    public void onRefresh() {
        page = 1;
        lastStart = 0;
        requestRecommendBaike();

    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_search:
                intent = new Intent(mContext, SearchResultActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_index:
                intent = new Intent(mContext, KnowledgeBaikeActivity.class);
                startActivity(intent);
                break;
            case R.id.et_search:
                intent = new Intent(mContext, SearchResultActivity.class);
                startActivity(intent);
                break;
        }

    }

    private static class ImageHandler extends Handler {

        /**
         * 请求更新显示的View。
         */
        protected static final int MSG_UPDATE_IMAGE = 1;
        /**
         * 请求暂停轮播。
         */
        protected static final int MSG_KEEP_SILENT = 2;
        /**
         * 请求恢复轮播。
         */
        protected static final int MSG_BREAK_SILENT = 3;
        /**
         * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
         * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
         * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
         */
        protected static final int MSG_PAGE_CHANGED = 4;

        //轮播间隔时间
        protected static final long MSG_DELAY = 3000;

        //使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
        private WeakReference<BaikeFragment> weakReference;

        protected ImageHandler(WeakReference<BaikeFragment> wk) {
            weakReference = wk;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(LOG_TAG, "receive message " + msg.what);
            BaikeFragment activity = weakReference.get();
            if (activity == null) {
                //Activity已经回收，无需再处理UI了
                return;
            }
            //检查消息队列并移除未发送的消息，这主要是避免在复杂环境下消息出现重复等问题。
            /*if (activity.handler.hasMessages(MSG_UPDATE_IMAGE)) {
                activity.handler.removeMessages(MSG_UPDATE_IMAGE);
            }*/
            try {
                switch (msg.what) {
                    case MSG_UPDATE_IMAGE:
                        int currentItem = activity.vpRecommend.getCurrentItem();
                        activity.vpRecommend.setCurrentItem((currentItem + 1) % 5);
                        activity.selectDot(currentItem);
                        activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                        break;
                    case MSG_KEEP_SILENT:
                        //只要不发送消息就暂停了
                        break;
                    case MSG_BREAK_SILENT:
                        //activity.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                        break;
                    case MSG_PAGE_CHANGED:

                        //记录当前的页号，避免播放的时候页面显示不正确。
                        currentItem = activity.vpRecommend.getCurrentItem();
                        activity.selectDot(currentItem);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DataTask implements Runnable {
        @Override
        public void run() {
            refreshTopView();
            Log.d(TAG, "cache resources " + resourceItemList.toString());
            myAdapter = new ContentListAdapter(mContext, resourceItemList);
            lvRecommend.setAdapter(myAdapter);
            searchAdapter = new SearchAdapter(mContext, searchKeyWords);
        }
    }

    private class MyPagerAdapter extends PagerAdapter {

        private ArrayList<ImageView> viewlist;

        public MyPagerAdapter(ArrayList<ImageView> viewlist) {
            this.viewlist = viewlist;
        }

        @Override
        public int getCount() {
            //设置成最大，使用户看不到边界
            return this.viewlist.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            //Warning：不要在这里调用removeView
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            position %= viewlist.size();
            if (position < 0) {
                position = viewlist.size() + position;
            }
            ImageView view = viewlist.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp = view.getParent();
            if (vp != null) {
                ViewGroup parent = (ViewGroup) vp;
                parent.removeView(view);
            }
            container.addView(view);
            //add listeners here if necessary
            final GetResourcesResult.BaikeItem resourceItem = (GetResourcesResult.BaikeItem) topItemList.get(position);
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    Intent detailIntent = new Intent();
                    detailIntent.putExtra("isTop", true);
                    detailIntent.setClass(mContext, BaikeContentDetailActivity.class);
                    detailIntent.putExtra("item", resourceItem);
                    startActivity(detailIntent);
                }
            });
            return view;
        }
    }


}
