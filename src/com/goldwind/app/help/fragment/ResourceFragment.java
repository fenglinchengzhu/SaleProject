package com.goldwind.app.help.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldwind.app.help.BaseFragment;
import com.goldwind.app.help.Constant;
import com.goldwind.app.help.R;
import com.goldwind.app.help.activity.FenLeiActivity;
import com.goldwind.app.help.activity.FenLeiNullActivity;
import com.goldwind.app.help.db.MyDB;
import com.goldwind.app.help.model.GetResourcesResult;
import com.goldwind.app.help.model.GetResourcesResult.CategoryItem;
import com.goldwind.app.help.model.GetResourcesResult.ResourceItem;
import com.goldwind.app.help.model.SearchResult;
import com.goldwind.app.help.util.ApiUtil;
import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.view.MyProgressDialog;
import com.goldwind.app.help.view.XListView;
import com.goldwind.app.help.view.XListView.IXListViewListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.xiaopan.sketch.SketchImageView;

/**
 * 资源
 */
public class ResourceFragment extends BaseFragment implements IXListViewListener {
    @Bind(R.id.tv_menu_tj)
    TextView tvMenuTj;
    @Bind(R.id.tv_menu_fl)
    TextView tvMenuFl;
    @Bind(R.id.iv_menu_tj)
    ImageView ivMenuTj;
    @Bind(R.id.iv_menu_fl)
    ImageView ivMenuFl;
    @Bind(R.id.vp_resource)
    ViewPager vpResource;
    @Bind(R.id.iv_back_img)
    ImageView backImg;
    View view;
    XListView lv_tui_jian;
    View lv_fen_lei;
    View view_white;
    ImageView iv_pic_anim;
    @Bind(R.id.rl_top_menu)
    LinearLayout rlTopMenu;
    private boolean isSnackBar = false;
    private AdapterView.OnItemClickListener onItemClickListener;

    private List<View> mViews = new ArrayList<View>();

    private List<GetResourcesResult.CategoryItem> fenLeiList;

    // 推荐
    private ArrayList<GetResourcesResult.ResourceItem> resourceItemList;

    private Animation alphaAnimation;
    private int x;
    private int y;
    private int h;
    private Handler mHandler;


    private int lastStart = 0;
    private int page = 0;
    private MyAdapter myAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);
        initAll();

        return view;
    }

    @Override
    public void onResume() {
        System.gc();
        System.gc();
        view_white.setVisibility(View.GONE);
        iv_pic_anim.setVisibility(View.GONE);
        super.onResume();
    }

    @Override
    protected void initParam() {
        x = CommonUtil.sp2px(getActivity().getApplicationContext(), 8);
        y = CommonUtil.sp2px(getActivity().getApplicationContext(), 8 + 50 + 2);
        h = Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        alphaAnimation = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.alpha);
        mHandler = new Handler();

        mViews.clear();
//		resourceItemList = MyDB.getInstance(getActivity().getApplicationContext()).getTuiJianResourceList(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid);

        resourceItemList = new ArrayList<GetResourcesResult.ResourceItem>();

        myAdapter = new MyAdapter();

        fenLeiList = MyDB.getInstance(getActivity().getApplicationContext()).getPCategoryItemList("1");
        for (CategoryItem categoryItem : fenLeiList) {
            categoryItem.subList = MyDB.getInstance(getActivity().getApplicationContext()).getCategoryItemList(categoryItem.id + "");
        }
    }

    @Override
    protected void initViews() {
        ivMenuFl.setVisibility(View.INVISIBLE);
        ivMenuTj.setVisibility(View.VISIBLE);

        view = View.inflate(getActivity(), R.layout.layout_tui_jian, null);
        view_white = view.findViewById(R.id.view_white);
        iv_pic_anim = (ImageView) view.findViewById(R.id.iv_pic_anim);
        lv_tui_jian = (XListView) view.findViewById(R.id.lv_tui_jian);
//		lv_tui_jian.addFooterView(View.inflate(getActivity(), R.layout.layout_bottom, null));
        lv_fen_lei = View.inflate(getActivity(), R.layout.layout_fen_lei, null);

        TextView emptyView = new TextView(getActivity());
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无资源");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) lv_tui_jian.getParent()).addView(emptyView);
        lv_tui_jian.setEmptyView(emptyView);
    }

//	public void refreshTuiJian() {
//		if (lv_tui_jian != null) {
//			if (getActivity() == null) {
//				return;
//			}
//			if (getActivity().getApplicationContext() == null) {
//				return;
//			}
//			if (Constant.getCurrentUser(getActivity().getApplicationContext()) == null) {
//				return;
//			}
//			resourceItemList = MyDB.getInstance(getActivity().getApplicationContext()).getTuiJianResourceList(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid);
//			lv_tui_jian.setAdapter(new MyAdapter());
//		}
//	}

    private void openDetailPage(ResourceItem resourceItem) {
//		// pdf
//		if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("pdf")) {
//			Intent intent = new Intent(getActivity(), PDFDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//			// 视频
//		} else if (resourceItem.type == 2) {
//			Intent intent = new Intent(getActivity(), VideoDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//			// 电子书
//		} else if (resourceItem.type == 1 && resourceItem.fileaddress.endsWith("epub")) {
//			Intent intent = new Intent(getActivity(), ResourceDetailActivity.class);
//			intent.putExtra("resourceItem", resourceItem);
//			intent.putExtra("anim", true);
//			startActivity(intent);
//		} else if (resourceItem.type == 3) {
//			Intent intent = null;
//			if (resourceItem.status > Integer.valueOf(Constant.getCurrentUser(getActivity().getApplicationContext()).role)) {
//				intent = new Intent(getActivity(), PicsDetailUnRoleActivity.class);
//			} else {
//				intent = new Intent(getActivity(), PicsDetailActivity.class);
//			}
//			intent.putExtra("resourceItem", resourceItem);
//			startActivity(intent);
//		}
    }

    @Override
    protected void initListener() {
        tvMenuFl.setOnClickListener(this);
        tvMenuTj.setOnClickListener(this);

        lv_tui_jian.setXListViewListener(this);
        backImg.setOnClickListener(this);
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

        lv_tui_jian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResourceItem resourceItem = (ResourceItem) parent.getItemAtPosition(position);

                final ResourceItem item = MyDB.getInstance(getActivity().getApplicationContext()).getResourceById(Constant.getCurrentUser(getActivity().getApplicationContext()).staffid,
                        resourceItem.resourcesid + "");

                if (item == null) {
                    MyDB.getInstance(getActivity().getApplicationContext()).insert(getActivity().getApplicationContext(), resourceItem, Constant.getCurrentUser(getActivity().getApplicationContext()).staffid);
                    ResourceItem item1 = MyDB.getInstance(getActivity().getApplicationContext()).getResourceById(
                            Constant.getCurrentUser(getActivity().getApplicationContext()).staffid, resourceItem.resourcesid + "");
                    if (item1 == null) {
                        if (getActivity() == null || getActivity().getApplicationContext() == null) {
                            return;
                        }
//						new SnackBar.Builder(getActivity(),isSnackBar).withMessage("您没有权限查看该资源").withDuration(SnackBar.SHORT_SNACK).show();
                    } else {
                        openDetailPage(item1);
                    }
                    return;
                }

                // pdf
                if (resourceItem.type != 3) {
                    ImageView iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
                    iv_pic.setDrawingCacheEnabled(true);
                    Bitmap obmp = Bitmap.createBitmap(iv_pic.getDrawingCache());
                    iv_pic.setDrawingCacheEnabled(false);
                    iv_pic_anim.setImageBitmap(obmp);

                    int[] location = new int[2];
                    iv_pic.getLocationOnScreen(location);
                    int aaa = CommonUtil.dip2px(getActivity().getApplicationContext(), 8);

                    TranslateAnimation translateAnimation = new TranslateAnimation(
                            TranslateAnimation.ABSOLUTE, location[0] - aaa,
                            TranslateAnimation.ABSOLUTE, x,
                            TranslateAnimation.ABSOLUTE, location[1] - y - h,
                            TranslateAnimation.ABSOLUTE, aaa);
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

        vpResource.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    ivMenuFl.setVisibility(View.INVISIBLE);
                    ivMenuTj.setVisibility(View.VISIBLE);
                } else {
                    ivMenuFl.setVisibility(View.VISIBLE);
                    ivMenuTj.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoryItem categoryItem = (CategoryItem) parent.getItemAtPosition(position);
                ArrayList<CategoryItem> subList = MyDB.getInstance(getActivity().getApplicationContext()).getCategoryItemList(categoryItem.id + "");

                if (subList != null && subList.size() != 0) {
                    Intent intent = new Intent(getActivity(), FenLeiActivity.class);
                    intent.putExtra("name", categoryItem.name);
                    intent.putExtra("pid", categoryItem.id + "");
                    getActivity().startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), FenLeiNullActivity.class);
                    intent.putExtra("name", categoryItem.name);
                    intent.putExtra("pid", categoryItem.id + "");
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void initData() {
        LinearLayout ll_fen_lei = (LinearLayout) lv_fen_lei.findViewById(R.id.ll_fen_lei);

        for (CategoryItem item : fenLeiList) {
            View view = View.inflate(getActivity(), R.layout.layout_fen_lei_level2, null);
            TextView tv_fei_lei = (TextView) view.findViewById(R.id.tv_fei_lei);

            ImageView iv_right_arrow = (ImageView) view.findViewById(R.id.iv_right_arrow);

            tv_fei_lei.setText(item.name);
//			tv_fei_lei.setTag(item);

            LinearLayout llll_fei_lei = (LinearLayout) view.findViewById(R.id.llll_fei_lei);
            llll_fei_lei.setTag(item);

            if (item.subList == null || item.subList.size() == 0) {
//				tv_fei_lei.setOnClickListener(this);
                llll_fei_lei.setOnClickListener(this);
                iv_right_arrow.setVisibility(View.VISIBLE);
            } else {
                llll_fei_lei.setOnClickListener(this);
                iv_right_arrow.setVisibility(View.VISIBLE);
//				iv_right_arrow.setVisibility(View.INVISIBLE);
            }

            GridView gv_fei_lei = (GridView) view.findViewById(R.id.gv_fei_lei);
            gv_fei_lei.setAdapter(new FenLeiAdapter(item.subList));
            gv_fei_lei.setOnItemClickListener(onItemClickListener);
            ll_fen_lei.addView(view);
        }

        mViews.add(view);
        mViews.add(lv_fen_lei);

        vpResource.setAdapter(new MyPagerAdapter());
        lv_tui_jian.setAdapter(myAdapter);

        onRefresh();
    }

    private void requestGetRecommend() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("staffid", Constant.getCurrentUser(getActivity().getApplicationContext()).staffid);
        map.put("limit", Constant.getPageSize() + "");
        map.put("start", lastStart + "");
        ApiUtil.request(new ApiUtil.MyHttpRequest<SearchResult>(getActivity().getApplicationContext(),
                Constant.Api.GET_DOC_RECOMMEND, ApiUtil.genRequestMap(map)) {

            @Override
            protected void onRequestStart() {
                MyProgressDialog.showDialog(getActivity(), "加载中");
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
                                lv_tui_jian.showFooterView();
                            } else {
                                lv_tui_jian.hideFooterView();
                            }
                            myAdapter.notifyDataSetChanged();
                        } else {
                            if (page != 0) {
                                if (getActivity() == null || getActivity().getApplicationContext() == null) {
                                    return;
                                }
//							new SnackBar.Builder(getActivity(),isSnackBar).withMessage("没有更多数据").withDuration(SnackBar.SHORT_SNACK).show();
                            } else {
                                resourceItemList.clear();
                                if (getActivity() == null || getActivity().getApplicationContext() == null) {
                                    return;
                                }
//							new SnackBar.Builder(getActivity(),isSnackBar).withMessage("暂无数据").withDuration(SnackBar.SHORT_SNACK).show();
                            }
                            myAdapter.notifyDataSetChanged();
                        }
                        break;
                    }
                    default: {
                        if (getActivity() == null || getActivity().getApplicationContext() == null) {
                            return;
                        }
//					new SnackBar.Builder(getActivity(),isSnackBar).withMessage(result.msg)
//					.withDuration(SnackBar.SHORT_SNACK).show();
                        break;
                    }
                }
            }
        });
    }

    private void onLoad() {
        lv_tui_jian.stopRefresh();
        lv_tui_jian.stopLoadMore();
        lv_tui_jian.setRefreshTime(CommonUtil.date2Str(new Date(), "HH:mm"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_menu_fl:
                ivMenuFl.setVisibility(View.VISIBLE);
                ivMenuTj.setVisibility(View.INVISIBLE);
                vpResource.setCurrentItem(1);
                break;
            case R.id.tv_menu_tj:
                ivMenuFl.setVisibility(View.INVISIBLE);
                ivMenuTj.setVisibility(View.VISIBLE);
                vpResource.setCurrentItem(0);
                break;
            case R.id.llll_fei_lei:
                CategoryItem categoryItem = (CategoryItem) v.getTag();
                if (categoryItem != null) {
                    Intent intent = new Intent(getActivity(), FenLeiNullActivity.class);
                    intent.putExtra("name", categoryItem.name);
                    intent.putExtra("pid", categoryItem.id + "");
                    getActivity().startActivity(intent);
                }
                break;
            case R.id.iv_back_img:
                this.getActivity().finish();
                ;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        page = 0;
        lastStart = 0;
        requestGetRecommend();
    }

    @Override
    public void onLoadMore() {
        page++;
        requestGetRecommend();
    }

    class FenLeiAdapter extends BaseAdapter {
        private List<CategoryItem> subList;

        public FenLeiAdapter(List<CategoryItem> subList) {
            super();
            this.subList = subList;
        }

        @Override
        public int getCount() {
            if (subList == null) {
                return 0;
            }
            return subList.size();
        }

        @Override
        public Object getItem(int position) {
            return subList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CategoryItem categoryItem = subList.get(position);
            View view = View.inflate(getActivity(), R.layout.layout_item_fen_lei, null);
            ((TextView) view.findViewById(R.id.tv_name)).setText(categoryItem.name);
            try {
                ((ImageView) view.findViewById(R.id.iv_pic)).setImageBitmap(BitmapFactory.decodeStream(getResources().getAssets().open(
                        "a" + (position % 4) + ".png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return view;
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (resourceItemList == null) {
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
                convertView = View.inflate(getActivity(), R.layout.layout_item_resource_list, null);
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

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mViews != null) {
                return mViews.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
}
