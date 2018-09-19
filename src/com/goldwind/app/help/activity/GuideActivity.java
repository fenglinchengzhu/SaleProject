package com.goldwind.app.help.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.goldwind.app.help.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 引导页
 */
public class GuideActivity extends Activity implements OnPageChangeListener {
    Context mContext;
    int[] viewIds;
    MyPagerAdapter myPagerAdapter;
    private ViewPager vp_guide;
    //private List<View> viewList;
    private LinearLayout ll_dots;
    private int position = -1;
    private int state = -1;
    private int lastState = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);
        mContext = this;
        initUI();
        initListener();
        initData();
    }

    private void initUI() {
        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        ll_dots = (LinearLayout) findViewById(R.id.ll_dots);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 7;
        layoutParams.rightMargin = 7;

        for (int i = 0; i < 3; i++) {
            ImageView dotView = (ImageView) View.inflate(this, R.layout.layout_dot_1, null);
            if (i == 0) {
                dotView.setEnabled(true);
            } else {
                dotView.setEnabled(false);
            }
            ll_dots.addView(dotView, i, layoutParams);
        }
    }

    private void initListener() {
        vp_guide.setOnPageChangeListener(this);
    }

    private void selectDots(int n) {
        for (int i = 0; i < ll_dots.getChildCount(); i++) {
            ImageView dotView = (ImageView) ll_dots.getChildAt(i);
            if (i == n) {
                dotView.setEnabled(true);
            } else {
                dotView.setEnabled(false);
            }
        }
    }

    private void initData() {
        viewIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};
        if (myPagerAdapter == null) {
            myPagerAdapter = new MyPagerAdapter();
        }
        vp_guide.setAdapter(myPagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        this.position = position;
        selectDots(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.lastState = this.state;
        this.state = state;

		/*if (state == 0 && this.lastState == 1 && position == viewList.size() - 1) {
                Intent intent = new Intent(this, SplashActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(0, R.anim.base_slide_right_out);
			}
		}*/
        if (state == 0 && this.lastState == 1 && position == viewIds.length - 1) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();
//			overridePendingTransition(0, R.anim.base_slide_right_out);
        }
    }

	/*private ArrayList<View>views;
	private GuideAdapter mGuideAdapter;

	class GuideAdapter extends PagerAdapter{
		private List<View> views;
		private final LinkedList<View> recyleBin=new LinkedList<>();
		public GuideAdapter(List<View>views){
			this.views=views;
		}
		@Override
		public int getCount() {
			return views.size();
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==object;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}
		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			//在此设置背景图片，提高加载速度，解决OOM问题
			View view;
			int count=getCount();
			if(!recyleBin.isEmpty()) {
				view=recyleBin.pop();
			}else {
				view=views.get(position);
				ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				view.setBackgroundResource(viewIds[position % count]);
				view.setLayoutParams(params);
			}
			container.addView(view,0);
			return views.get(position);
		}
	}*/

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myPagerAdapter = null;
		/*for(Bitmap bitmap:bitmapList){
			bitmap.recycle();
		}*/
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //container.removeView(viewList.get(position));// 删除页卡
            ImageView view = (ImageView) object;
            ((ViewPager) container).removeView(view);
            view.destroyDrawingCache();
            view = null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoader.getInstance().displayImage("drawable://" + viewIds[position], imageView);
            ((ViewPager) container).addView(imageView);
            return imageView;
        }

        @Override
        public int getCount() {
            return viewIds.length;//viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }
    }
}
