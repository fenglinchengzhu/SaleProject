package com.goldwind.app.help.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.goldwind.app.help.util.CommonUtil;
import com.goldwind.app.help.util.LogUtil;

public class MyGridViewUP extends GridView implements AbsListView.OnScrollListener, Runnable {
    public static final int UP = 0x999;
    public static final int DOWN = 0x998;
    private static final float MIN_DISTANCE = 50f;

    private LinearLayout llBottom;

    private int llBottomTopMargin;
    private float xDistance, yDistance, xLast, yLast;

    private float mLastY = -1; // save event y
    private float mDistance;

    private boolean downFlag;

    private float mLastDownY1 = 0f;
    private int mDistance1 = 0;
    private int mStep1 = 10;
    private boolean mPositive1 = false;

    private View lastView;

    private boolean isClick = true;
    private boolean isScrollEnable = true;
    private MyGridViewDown myGridViewDown;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public MyGridViewUP(Context context) {
        super(context);
        initWithContext(context);
    }

    public MyGridViewUP(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public MyGridViewUP(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean isClick) {
        this.isClick = isClick;
    }

    private void initWithContext(Context context) {
        setOnScrollListener(this);
        llBottomTopMargin = CommonUtil.dip2px(context, 215f);
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

    public void setScrollEnable(boolean isScrollEnable) {
        this.isScrollEnable = isScrollEnable;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // if (ev.getAction() == MotionEvent.ACTION_MOVE && !isScrollEnable) {
        // // 最关键的地方，忽略MOVE 事件
        // // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
        // return true;
        // }
        if (!isScrollEnable) {
            // 最关键的地方，忽略MOVE 事件
            // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
            return true;
        }
        return super.dispatchTouchEvent(ev);
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
                downFlag = true;

                if (isClick) {
                    super.setOnItemClickListener(onItemClickListener);
                    super.setOnItemLongClickListener(onItemLongClickListener);
                }


                if (mLastDownY1 == 0f && mDistance1 == 0) {
                    mLastDownY1 = ev.getY();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                mDistance += deltaY;

//			if (Math.abs(mDistance) > MIN_DISTANCE / 10) {
//
//            	isClick = false;
//
//            	clearListener();
//            }else {
//            	isClick = true;
//			}

                if (getFirstVisiblePosition() == 0 && getFirstChildTop() == 0 && mDistance > 0 && Math.abs(mDistance) > MIN_DISTANCE) {
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, llBottomTopMargin);
                    translateAnimation.setDuration(600);
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            clearListener();

                            if (myGridViewDown != null) {
                                myGridViewDown.clearListener();
                            }

                            isClick = false;

                            setSelection(0);
                            scrollTo(0, 0);
                            smoothScrollToPosition(0);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            isClick = true;
                            llBottom.setVisibility(INVISIBLE);

                            setSelection(0);
                            scrollTo(0, 0);
                            smoothScrollToPosition(0);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    llBottom.startAnimation(translateAnimation);
                }
                if (downFlag == false) {
                    return true;
                }

                if (mLastDownY1 != 0f) {
                    mDistance1 = (int) (mLastDownY1 - ev.getY());

                    if ((mDistance1 > 0 && getLastVisiblePosition() == getCount() - 1) && getLastChildBottom() != 0 && getLastChildBottom() <= getHeight()) {
                        clearListener();
                        LogUtil.d("clear listener");

                        mDistance1 /= 2.5f;
                        scrollTo(0, mDistance1);
                        return true;
                    }
                }
                mDistance1 = 0;

                break;
            case MotionEvent.ACTION_UP:

                if (mDistance1 != 0) {
                    mStep1 = 1;
                    mPositive1 = (mDistance1 >= 0);
                    this.post(this);

                    downFlag = true;
                    mLastY = -1;
                    mDistance = 0f;

                    return true;
                }
                mLastDownY1 = 0f;
                mDistance1 = 0;

                downFlag = true;
                mLastY = -1;
                mDistance = 0f;
                break;
            default:
                downFlag = true;
                mLastY = -1;
                mDistance = 0f;
                break;
        }

        return super.onTouchEvent(ev);
    }

    private int getFirstChildTop() {
        if (getChildAt(0) == null) {
            return 0;
        }
        return getChildAt(0).getTop();
    }

    private int getLastChildBottom() {
        if (lastView == null) {
            return 0;
        }
        return lastView.getBottom();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void setMyGridViewDown(MyGridViewDown myGridViewDown) {
        this.myGridViewDown = myGridViewDown;
    }

    public void setLlBottom(LinearLayout llBottom) {
        this.llBottom = llBottom;
    }

    @Override
    public void run() {
        mDistance1 += mDistance1 > 0 ? -mStep1 : mStep1;
        scrollTo(0, mDistance1);
        if ((mPositive1 && mDistance1 <= 0) || (!mPositive1 && mDistance1 >= 0)) {
            scrollTo(0, 0);


            LogUtil.d("reset listener");
//			super.setOnItemClickListener(onItemClickListener);
//	        super.setOnItemLongClickListener(onItemLongClickListener);


            mDistance1 = 0;
            mLastDownY1 = 0f;
            return;
        }
        mStep1 += 1;
        this.postDelayed(this, 10);
    }

    public void setLastView(View lastView) {
        this.lastView = lastView;
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
