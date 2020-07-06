package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

import java.util.Arrays;

/**
 * Created by canyinghao on 16/7/13.
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
public class CanRecyclerViewHeaderFooter extends FrameLayout {

    //    是否依附在RecyclerView上
    private boolean isAttached;
    //    是头部还是底部
    private boolean isHeader = true;

    //   RecyclerView是否颠倒
    private boolean isReversed;
    //    RecyclerView是否水平
    private boolean isVertical;

    //  设置是否可加载
    private boolean isLoadEnable = true;

    //  是否加载完成
    private boolean isLoadComplete = true;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //  给RecyclerView的头部底部加Decoration
    private CanItemDecoration decoration;
    //  loadmore的监听事件
    private OnLoadMoreListener loadMoreListener;

    // 滑动监听
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            onScrollChanged();
        }
    };

    //  依附监听
    private RecyclerView.OnChildAttachStateChangeListener onAttachListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            post(new Runnable() {
                @Override
                public void run() {

                    if (!recyclerView.isComputingLayout()) {
                        recyclerView.invalidateItemDecorations();
                    }
                    onScrollChanged();
                }
            });
        }
    };

    public CanRecyclerViewHeaderFooter(Context context) {
        super(context);
    }

    public CanRecyclerViewHeaderFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanRecyclerViewHeaderFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 是否可以加载，没有更多时可以调用
     *
     * @param loadEnable boolean
     */
    public void setLoadEnable(boolean loadEnable) {
        isLoadEnable = loadEnable;
    }


    /**
     * 获取 decoration
     *
     * @return CanItemDecoration
     */
    public CanItemDecoration getDecoration() {
        return decoration;
    }


    /**
     * 从recyclerView移出
     */
    public void remove() {
        isAttached = false;

        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView.removeOnChildAttachStateChangeListener(onAttachListener);
            if (decoration != null) {
                recyclerView.removeItemDecoration(decoration);
            }
        }


    }

    /**
     * 设置加载监听事件
     *
     * @param loadMoreListener OnLoadMoreListener
     */
    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * 加载完成时调用，避免重复加载
     */
    public void loadMoreComplete() {

        this.isLoadComplete = true;

    }


    /**
     * 依附的方法
     *
     * @param recycler RecyclerView
     * @param isHeader boolean
     */
    public void attachTo(@NonNull final RecyclerView recycler, boolean isHeader) {
        if (recycler.getLayoutManager() == null) {
            throw new IllegalStateException("no LayoutManager.");
        }

        this.isHeader = isHeader;

        recyclerView = recycler;
        layoutManager = recyclerView.getLayoutManager();

        initLayoutManager();

        isAttached = true;

        if (decoration != null) {
            recyclerView.removeItemDecoration(decoration);
        }
        decoration = new CanItemDecoration().setIsHeader(isHeader);
        recyclerView.addItemDecoration(decoration);

        recyclerView.removeOnScrollListener(onScrollListener);
        recyclerView.addOnScrollListener(onScrollListener);

        recyclerView.removeOnChildAttachStateChangeListener(onAttachListener);
        recyclerView.addOnChildAttachStateChangeListener(onAttachListener);


    }




    /**
     * 通过layoutManager获取各种属性值
     */
    private void initLayoutManager() {

        if (layoutManager instanceof GridLayoutManager) {


            GridLayoutManager grid = (GridLayoutManager) layoutManager;


            this.isReversed = grid.getReverseLayout();

            this.isVertical = grid.getOrientation() == LinearLayoutManager.VERTICAL;

        } else if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager linear = (LinearLayoutManager) layoutManager;


            this.isReversed = linear.getReverseLayout();

            this.isVertical = linear.getOrientation() == LinearLayoutManager.VERTICAL;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

            StaggeredGridLayoutManager staggeredGrid = (StaggeredGridLayoutManager) layoutManager;


            this.isReversed = staggeredGrid.getReverseLayout();

            this.isVertical = staggeredGrid.getOrientation() == LinearLayoutManager.VERTICAL;
        }


    }


    /**
     * 重写该方法，更新头部底部宽高
     *
     * @param changed boolean
     * @param l       int
     * @param t       int
     * @param r       int
     * @param b       int
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && isAttached) {


            if (decoration != null) {

                int vertical = 0;

                if (getLayoutParams() instanceof MarginLayoutParams) {
                    final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                    vertical = layoutParams.topMargin + layoutParams.bottomMargin;
                }
                decoration.setHeight(getHeight() + vertical);
                recyclerView.invalidateItemDecorations();

            }

            onScrollChanged();


        }


        super.onLayout(changed, l, t, r, b);
    }


    /**
     * 滚动时移动头部底部
     */
    public void onScrollChanged() {

        try{
            if (isHeader) {
                boolean isVisibility = hasItems() && isFirstRowVisible();

                translationXY(isVisibility);


            } else {

                boolean isVisibility = hasItems() && isLastRowVisible();

                translationXY(isVisibility);


            }
        }catch (Throwable e){
            e.printStackTrace();
        }


    }


    /**
     * 移动的方法
     *
     * @param isVisibility boolean
     */
    private void translationXY(boolean isVisibility) {
        try{
            setVisibility(isVisibility ? VISIBLE : INVISIBLE);

            if (isVisibility) {

                if (isLoadEnable && isLoadComplete && loadMoreListener != null) {

                    loadMoreListener.onLoadMore();

                    isLoadComplete = false;
                }


                int first = calculateTranslation();
                if (isVertical) {


                    ViewHelper.setTranslationY(this, first);

                } else {

                    ViewHelper.setTranslationX(this, first);
                }


            }
        }catch (Throwable e){
            e.printStackTrace();
        }

    }

    /**
     * 判断头部底部进行计算距离
     *
     * @return int
     */
    private int calculateTranslation() {


        if (isHeader) {

            return calculateTranslationXY(!isReversed);


        } else {

            return calculateTranslationXY(isReversed);


        }


    }

    /**
     * 计算距离的方法
     *
     * @param isTop boolean
     * @return int
     */
    private int calculateTranslationXY(boolean isTop) {
        if (!isTop) {
            int offset = getScrollOffset();
            int base = getScrollRange() - getSize();
            return base - offset;


        } else {


            return -getScrollOffset();

        }
    }


    private boolean hasItems() {
        return recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() != 0;
    }


    private int getScrollOffset() {
        return isVertical ? recyclerView.computeVerticalScrollOffset() : recyclerView.computeHorizontalScrollOffset();
    }

    private int getSize() {
        return isVertical ? getHeight() : getWidth();
    }


    private int getScrollRange() {
        return isVertical ?
                recyclerView.computeVerticalScrollRange() :
                recyclerView.computeHorizontalScrollRange();
    }


    /**
     * 第一项是否显示
     *
     * @return boolean
     */
    private boolean isFirstRowVisible() {
        if (layoutManager instanceof GridLayoutManager) {


            GridLayoutManager grid = (GridLayoutManager) layoutManager;

            return grid.findFirstVisibleItemPosition() == 0;

        } else if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager linear = (LinearLayoutManager) layoutManager;

            return linear.findFirstVisibleItemPosition() == 0;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

            StaggeredGridLayoutManager staggeredGrid = (StaggeredGridLayoutManager) layoutManager;

            int[] positions = staggeredGrid.findFirstVisibleItemPositions(null);

            Arrays.sort(positions);


            return positions[0] == 0;
        }

        return false;
    }


    /**
     * 最后一项是否显示
     *
     * @return boolean
     */
    private boolean isLastRowVisible() {
        if (layoutManager instanceof GridLayoutManager) {


            GridLayoutManager grid = (GridLayoutManager) layoutManager;

            return grid.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1;

        } else if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager linear = (LinearLayoutManager) layoutManager;

            return linear.findLastVisibleItemPosition() == layoutManager.getItemCount() - 1;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

            StaggeredGridLayoutManager staggeredGrid = (StaggeredGridLayoutManager) layoutManager;

            int[] positions = staggeredGrid.findLastVisibleItemPositions(null);

            Arrays.sort(positions);


            return positions[staggeredGrid.getSpanCount() - 1] >= layoutManager.getItemCount() - 1;
        }

        return false;
    }


    public interface OnLoadMoreListener {

        void onLoadMore();
    }
}
