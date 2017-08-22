package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


public class HorizontalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mMarginProvider.dividerRightMargin(position, parent) + transitionX;

        int dividerSize = getDividerSize(position, parent);
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.top = child.getBottom() + params.topMargin + transitionY;
            bounds.bottom = bounds.top + dividerSize;
        } else {
            bounds.top = child.getBottom() + params.topMargin + dividerSize / 2 + transitionY;
            bounds.bottom = bounds.top;
        }

        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {

        if(mNewStyle){
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if(layoutManager instanceof GridLayoutManager){
                GridLayoutManager gridLayoutManager =  (GridLayoutManager)layoutManager;
                GridLayoutManager.SpanSizeLookup lookup =gridLayoutManager.getSpanSizeLookup();
                int spanCount = gridLayoutManager.getSpanCount();
                int spanGroup = lookup.getSpanGroupIndex(position,spanCount);
                int spanSizeCount =1;
                if(spanGroup==0){
                    if(mSpanIndexProvider!=null){
                        spanSizeCount = mSpanIndexProvider.getSpanCount(position,parent);
                    }else{
                        int spanSize  =  lookup.getSpanSize(position);
                        spanSizeCount = spanCount/spanSize;
                    }
                }else{
                    spanSizeCount = 0;
                }
                setItemDividerSize(outRect, position, parent, spanSizeCount);

            }else if(layoutManager instanceof StaggeredGridLayoutManager){
                StaggeredGridLayoutManager gridLayoutManager =  (StaggeredGridLayoutManager)layoutManager;
                int count = gridLayoutManager.getSpanCount();
                setItemDividerSize(outRect, position, parent, count);
            }else{
                int count = 1;
                setItemDividerSize(outRect, position, parent, count);
            }

        }else{
            outRect.set(0, 0, 0, getDividerSize(position, parent));
        }




    }

    private void setItemDividerSize(Rect outRect, int position, RecyclerView parent, int count) {
        int size = getDividerSize(position, parent);
        if (mOnlyFirst) {
            if (position < count) {
                outRect.set(0, size, 0, 0);
            }
        } else {
            if (position < count ) {
                outRect.set(0, size, 0, size);
            } else {
                outRect.set(0, 0, 0, size);
            }
        }
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return left margin
         */
        int dividerLeftMargin(int position, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return right margin
         */
        int dividerRightMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }
    }
}