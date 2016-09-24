package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by canyinghao on 15/12/17..
 * Copyright 2016 canyinghao
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class CanRecyclerViewPager extends RecyclerViewEmpty {


    public static final String TAG = "CanRecyclerViewPager";

    protected Adapter mAdapter;
    protected float mFriction = 0.1f;

    protected boolean mOnePage;
    protected List<OnPageChangedListener> mOnPageChangedListeners;

    protected View mCurrentView;
    protected boolean isSmooth;


    public CanRecyclerViewPager(Context context) {
        this(context, null);
    }

    public CanRecyclerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanRecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setNestedScrollingEnabled(false);


    }

    public boolean isOnePage() {
        return mOnePage;
    }

    public void setOnePage(boolean mOnePage) {
        this.mOnePage = mOnePage;
    }

    public float getFriction() {
        return mFriction;
    }

    public void setFriction(float mFriction) {
        this.mFriction = mFriction;


    }


    private void setScale(final float scale) {

        int childCount = getChildCount();

        int width = getChildAt(0).getWidth();
        if (!isHorizontally()) {
            width = getChildAt(0).getHeight();
        }

        int padding = (getWidth() - width) / 2;

        if (!isHorizontally()) {
            padding = (getHeight() - width) / 2;
        }


        for (int j = 0; j < childCount; j++) {
            View v = getChildAt(j);
            //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
            float rate = 0;

            if (isHorizontally()) {

                if (v.getLeft() <= padding) {
                    if (v.getLeft() >= padding - v.getWidth()) {
                        rate = (padding - v.getLeft()) * 1f / v.getWidth();
                    } else {
                        rate = 1;
                    }


                    ViewCompat.setScaleX(v,1 - rate * (1.0f - scale));
                    ViewCompat.setScaleY(v,1 - rate * (1.0f - scale));

                } else {
                    //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                    if (v.getLeft() <= getWidth() - padding) {
                        rate = (getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                    }



                    ViewCompat.setScaleX(v,scale + rate * (1.0f - scale));
                    ViewCompat.setScaleY(v,scale + rate * (1.0f - scale));

                }
            } else {

                if (v.getTop() <= padding) {
                    if (v.getTop() >= padding - v.getHeight()) {
                        rate = (padding - v.getTop()) * 1f / v.getHeight();
                    } else {
                        rate = 1;
                    }




                    ViewCompat.setScaleX(v,1 - rate * (1.0f - scale));
                    ViewCompat.setScaleY(v,1 - rate * (1.0f - scale));

                } else {
                    //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                    if (v.getTop() <= getHeight() - padding) {
                        rate = (getHeight() - padding - v.getTop()) * 1f / v.getHeight();
                    }



                    ViewCompat.setScaleX(v,scale + rate * (1.0f - scale));
                    ViewCompat.setScaleY(v,scale + rate * (1.0f - scale));
                }

            }


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void addScaleListener(final float scale) {


        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {

                setScale(scale);
            }
        });



        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {


                int count = getChildCount();
                if (count >= 3) {
                    int midNum = getMidNum(count);


                    for (int i = 0; i < count; i++) {

                        if (i == midNum) {
                            continue;
                        }


                        View view = getChildAt(i);


                        ViewCompat.setScaleX(view,scale);
                        ViewCompat.setScaleY(view,scale);

                    }

                } else {
                    if (getChildAt(1) != null) {
                        if (getCurrentPosition() == 0) {
                            View v1 = getChildAt(1);

                            ViewCompat.setScaleX(v1,scale);
                            ViewCompat.setScaleY(v1,scale);
                        } else {
                            View v1 = getChildAt(0);

                            ViewCompat.setScaleX(v1,scale);
                            ViewCompat.setScaleY(v1,scale);
                        }
                    }

                }


            }
        });
    }


    @Override
    public void setAdapter(Adapter adapter) {

        this.mAdapter = adapter;
        super.setAdapter(adapter);
    }


    int mCurrentPosition;
    int mLoction;

    @Override
    public void smoothScrollToPosition(int position) {


        final int mPositionBeforeScroll = mCurrentPosition;
        mCurrentPosition = position;
        isSmooth = true;
        super.smoothScrollToPosition(position);

        if (mOnPageChangedListeners != null) {
            for (OnPageChangedListener onPageChangedListener : mOnPageChangedListeners) {
                if (onPageChangedListener != null) {
                    onPageChangedListener.OnPageChanged(mPositionBeforeScroll, getCurrentPosition());
                }
            }
        }
    }


    public int getCurrentPosition() {

        return mCurrentPosition;

    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        boolean flinging = super.fling((int) (velocityX * mFriction), (int) (velocityY * mFriction));
        if (flinging) {


            int flingCount = 0;

            flingCount = getFlingCountXY(velocityX, velocityY);


            if (mOnePage) {

                flingCount = Math.max(-1, Math.min(1, flingCount));


            }
            int targetPosition = mCurrentPosition + flingCount;


            smoothScrollToPosition(safePosition(targetPosition, mAdapter.getItemCount()));
        }


        return flinging;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {


            int count = getChildCount();

            if (count >= 3) {
                int midNum = getMidNum(count);


                mCurrentView = getChildAt(midNum);
            } else {
                mCurrentView = getChildAt(0);
            }


            mLoction = getLoaction();


        } else if (ev.getAction() == MotionEvent.ACTION_UP) {

            int count = getChildCount();

            View midView = null;
            if (count >= 3) {
                int midNum = getMidNum(count);


                midView = getChildAt(midNum);
            } else {
                midView = getChildAt(0);
            }

            newPostion = getChildAdapterPosition(midView);


        }
        return super.dispatchTouchEvent(ev);
    }


    int newPostion;

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        return super.onTouchEvent(e);
    }


    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        switch (state) {
            case SCROLL_STATE_IDLE:


                if (!isSmooth) {


                    int location = getLoaction();


                    int targetPosition = mCurrentPosition;


                    int minPostion = 0;
                    if (isHorizontally()) {


                        if (mCurrentView.getWidth() < getWidth() / 4) {

                            minPostion = 0;
                        } else {
                            minPostion = mCurrentView.getWidth() / 4;
                        }


                    } else {
                        if (mCurrentView.getHeight() < getHeight() / 4) {

                            minPostion = 0;
                        } else {
                            minPostion = mCurrentView.getHeight() / 4;
                        }


                    }

                    if (newPostion - targetPosition > 1) {

                        targetPosition = newPostion;
                    } else {
                        if (mLoction - location > minPostion) {

                            targetPosition++;


                        } else if (location - mLoction > minPostion) {


                            targetPosition--;
                        }
                    }


                    smoothScrollToPosition(safePosition(targetPosition, mAdapter.getItemCount()));
                } else {
                    isSmooth = false;

                }


                break;

            case SCROLL_STATE_DRAGGING:


                break;

            case SCROLL_STATE_SETTLING:


                break;

        }


    }


    private int getFlingCountXY(int velocitx, int velocity) {

        if (isHorizontally()) {


            return getFlingCount(velocitx, mCurrentView.getWidth());
        } else {

            return getFlingCount(velocity, mCurrentView.getHeight());
        }

    }

    private int getFlingCount(int velocity, int cellSize) {
        if (velocity == 0) {
            return 0;
        }
        int sign = velocity > 0 ? 1 : -1;
        return (int) (sign * Math.ceil((velocity * sign * mFriction / cellSize)
        ));
    }


    private int safePosition(int position, int count) {
        if (position < 0) {
            return 0;
        }
        if (position >= count) {
            return count - 1;
        }
        return position;
    }


    private boolean isHorizontally() {

        return getLayoutManager().canScrollHorizontally();
    }


    private int getLoaction() {


        int[] vLocationOnScreen = new int[2];
        mCurrentView.getLocationOnScreen(vLocationOnScreen);


        if (isHorizontally()) {
            return vLocationOnScreen[0];
        } else {

            return vLocationOnScreen[1];
        }
    }

    private int getMidNum(int count) {

        int midNum;
        if (count % 2 == 1) {
            midNum = (count + 1) / 2;
        } else {
            midNum = count / 2;
        }

        if (midNum > 0) {
            midNum--;
        }

        return midNum;
    }

    public void addOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners == null) {
            mOnPageChangedListeners = new ArrayList<>();
        }
        mOnPageChangedListeners.add(listener);
    }

    public void removeOnPageChangedListener(OnPageChangedListener listener) {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners.remove(listener);
        }
    }

    public void clearOnPageChangedListeners() {
        if (mOnPageChangedListeners != null) {
            mOnPageChangedListeners.clear();
        }
    }

    public interface OnPageChangedListener {
        void OnPageChanged(int oldPosition, int newPosition);
    }


    public  void setTabLayoutSupport(@NonNull final TabLayout tabLayout, @NonNull ViewPagerTabLayoutAdapter viewPagerTabLayoutAdapter) {
        tabLayout.removeAllTabs();

        int count = viewPagerTabLayoutAdapter.getItemCount();
        for (int i = 0; i < count; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(viewPagerTabLayoutAdapter.getPageTitle(i)));
        }


        addOnPageChangedListener(new CanRecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {

                if (tabLayout != null && tabLayout.getTabAt(newPosition) != null) {
                    tabLayout.getTabAt(newPosition).select();
                }
            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                smoothScrollToPosition(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public interface ViewPagerTabLayoutAdapter {
        String getPageTitle(int position);

        int getItemCount();
    }
}
