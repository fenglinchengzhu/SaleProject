package com.goldwind.app.help.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.goldwind.app.help.R;
import com.goldwind.app.help.util.CommonUtil;

public class MyGridViewDown extends GridView implements AbsListView.OnScrollListener {
    public static final int UP = 0x999;
    public static final int DOWN = 0x998;
    private static final float MIN_DISTANCE = 50f;

    private LinearLayout llBottom_1;

    private int llBottomTopMargin;
    private float xDistance, yDistance, xLast, yLast;

    private float mLastY = -1; // save event y
    private float mDistance;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private boolean isClick = true;
    private MyGridViewUP myGridViewUP;

    public MyGridViewDown(Context context) {
        super(context);
        initWithContext(context);
    }

//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }

    public MyGridViewDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public MyGridViewDown(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        setOnScrollListener(this);
        llBottomTopMargin = CommonUtil.dip2px(context, 215f);
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean isClick) {
        this.isClick = isClick;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (xDistance > yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                mDistance = 0f;
                if (isClick) {
                    super.setOnItemClickListener(onItemClickListener);
                    super.setOnItemLongClickListener(onItemLongClickListener);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                mDistance += deltaY;

                if (Math.abs(mDistance) > MIN_DISTANCE / 10) {

//                	isClick = false;

                    clearListener();
                } else {
//                	isClick = true;
                }

                if (mDistance < 0 && Math.abs(mDistance) > MIN_DISTANCE) {
                    isClick = false;

                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                            0f, Animation.RELATIVE_TO_PARENT, 0f
                            , Animation.ABSOLUTE, llBottomTopMargin, Animation.RELATIVE_TO_SELF, 0f);
                    translateAnimation.setDuration(600);
                    translateAnimation.setFillAfter(true);
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            isClick = false;
                            llBottom_1.setVisibility(VISIBLE);
                            myGridViewUP.clearListener();
                            clearListener();
                            myGridViewUP.setScrollEnable(false);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            isClick = true;
                            myGridViewUP.setScrollEnable(true);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    llBottom_1.startAnimation(translateAnimation);

                }
                return true;
            case MotionEvent.ACTION_UP:
                mLastY = -1;
                mDistance = 0f;
                break;
            default:
                mLastY = -1;
                mDistance = 0f;
                break;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void setLlBottom(LinearLayout llBottom) {
        this.llBottom_1 = llBottom;
        this.myGridViewUP = (MyGridViewUP) llBottom.findViewById(R.id.gv_all_1);
    }

    public void clearListener() {
        super.setOnItemClickListener(null);
        super.setOnItemLongClickListener(null);
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        super.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void setOnItemLongClickListener(android.widget.AdapterView.OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        super.setOnItemLongClickListener(listener);
    }

}
