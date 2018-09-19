/* Copyright (C) 2011 The Android Open Source Project Copyright (C) 2011 Jake Wharton Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package com.goldwind.app.help.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldwind.app.help.R;
import com.goldwind.app.help.util.CommonUtil;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements PageIndicator {

    private static final int[] LL = new int[]{
        /* 0 */ R.attr.firstItemBackground,
        /* 1 */ R.attr.lastItemBackground,
    };

    private static final int LL_FIRST_ITEM_BACKGROUND = 0;
    private static final int LL_LAST_ITEM_BACKGROUND = 1;

    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";
    private final IcsLinearLayout mTabLayout;
    private Runnable mTabSelector;
    private ViewPager mViewPager;
    private OnPageChangeListener mListener;
    private int mMaxTabWidth;
    private int mSelectedTabIndex;
    private OnTabClickListener mTabClickListener;
    private final OnClickListener clickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int newSelected = tabView.getIndex();
            if (mTabClickListener != null) mTabClickListener.onTabClick(newSelected);
            mViewPager.setCurrentItem(newSelected);
        }
    };
    private boolean isFirst = true;

    public TabPageIndicator(Context context) {
        this(context, null);
    }

    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
        mTabLayout = new IcsLinearLayout(context, R.attr.vpiTabPageIndicatorStyle);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mTabClickListener = listener;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);
        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST))
            if (childCount > 2) mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            else mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
        else mMaxTabWidth = -1;
        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();
        if (lockedExpanded && oldWidth != newWidth) { /* Recenter the tab display if we're at a new (scrollable) size.*/
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) removeCallbacks(mTabSelector);
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) { /* Re-post the selector we saved*/
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) removeCallbacks(mTabSelector);
    }

    private void addTab(int index, CharSequence text, int iconResId, boolean isFirst, boolean isLast) {
        final TabView tabView = new TabView(getContext(), isFirst, isLast);
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(clickListener);
        tabView.setText(text);
        if (iconResId != 0) tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
        if (isFirst) {
            layoutParams.leftMargin = CommonUtil.dip2px(getContext(), 16f);
        }
        if (isLast) {
            layoutParams.rightMargin = CommonUtil.dip2px(getContext(), 16f);
        }
        layoutParams.topMargin = CommonUtil.dip2px(getContext(), 7f);
        layoutParams.bottomMargin = CommonUtil.dip2px(getContext(), 7f);
        mTabLayout.addView(tabView, layoutParams);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) mListener.onPageScrollStateChanged(arg0);
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) mListener.onPageScrolled(arg0, arg1, arg2);
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) mListener.onPageSelected(arg0);
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) return;
        if (mViewPager != null) mViewPager.setOnPageChangeListener(null);
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null)
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        boolean isFirst = false;
        boolean isLast = false;

        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) title = EMPTY_TITLE;
            int iconResId = 0;
            isFirst = (i == 0);
            isLast = (i == count - 1);

            addTab(i, title, iconResId, isFirst, isLast);
        }
        if (mSelectedTabIndex > count) mSelectedTabIndex = count - 1;
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    @Override
    public void setCurrentItem(int item) {

        if (mViewPager == null) throw new IllegalStateException("ViewPager has not been bound.");


        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
                ((TabView) child).setTextColor(0xffffffff);
                if (isFirst && item == 0) {
                    ((TabView) child).setBackgroundDrawable(null);
                } else {
                    ((TabView) child).setBackgroundResource(R.drawable.border_fen_lei);
                }


            } else {
                ((TabView) child).setTextColor(0xffffffff);
                ((TabView) child).setBackgroundDrawable(null);
            }
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }


    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabClickListener {
        /**
         * Callback when the selected tab has been reselected. @param position Position of the current center item.
         */
        void onTabClick(int position);
    }

    private class TabView extends TextView {
        private int mIndex;

        public TabView(Context context, boolean isFirst, boolean isLast) {
            super(context, null, R.attr.vpiTabPageIndicatorStyle);

            final Resources.Theme theme = context.getTheme();

            TypedArray a = theme.obtainStyledAttributes(
                    null, LL, R.attr.vpiTabPageIndicatorStyle, 0);
            if (isFirst) {
                if (a.getDrawable(TabPageIndicator.LL_FIRST_ITEM_BACKGROUND) != null) {
                    setBackgroundDrawable(a.getDrawable(TabPageIndicator.LL_FIRST_ITEM_BACKGROUND));
                }
            }

            if (isLast) {
                if (a.getDrawable(TabPageIndicator.LL_LAST_ITEM_BACKGROUND) != null) {
                    setBackgroundDrawable(a.getDrawable(TabPageIndicator.LL_LAST_ITEM_BACKGROUND));
                }
            }

            a.recycle();
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec); /* Re-measure if we went beyond our maximum size.*/
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth)
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
        }

        public int getIndex() {
            return mIndex;
        }
    }
}
