package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DimenRes;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;


public class VerticalDividerItemDecoration extends FlexibleDividerDecoration {

    private MarginProvider mMarginProvider;

    protected VerticalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.top = parent.getPaddingTop() +
                mMarginProvider.dividerTopMargin(position, parent) + transitionY;
        bounds.bottom = parent.getHeight() - parent.getPaddingBottom() -
                mMarginProvider.dividerBottomMargin(position, parent) + transitionY;

        int dividerSize = getDividerSize(position, parent);
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.left = child.getRight() + params.leftMargin + transitionX;
            bounds.right = bounds.left + dividerSize;
        } else {
            bounds.left = child.getRight() + params.leftMargin + dividerSize / 2 + transitionX;
            bounds.right = bounds.left;
        }

        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {

        if(mNewStyle){
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            if(layoutManager instanceof GridLayoutManager){
                GridLayoutManager gridLayoutManager =  (GridLayoutManager)layoutManager;
                int spanCount = gridLayoutManager.getSpanCount();
                GridLayoutManager.SpanSizeLookup lookup = gridLayoutManager.getSpanSizeLookup();
                int index  =0;
                int spanSizeCount =1;
                int spanSize  =  lookup.getSpanSize(position);
                if(mSpanIndexProvider!=null){
                     index= mSpanIndexProvider.getSpanIndex(position,parent);
                     spanSizeCount = mSpanIndexProvider.getSpanCount(position,parent);
                }else{
                    index= lookup.getSpanIndex(position,spanCount);
                    index =index/spanSize;
                    spanSizeCount = spanCount/spanSize;
                }

                int size = getDividerSize(position, parent);

                int column = index % spanSizeCount;

                if(mOnlyFirst){
                    if(index==0){
                        outRect.set(size, 0,0, 0);
                    }
                }else{

                    outRect.left = size - column * size / spanSizeCount; // spacing - column * ((1f / spanCount) * spacing)
                    outRect.right = (column + 1) * size / spanSizeCount; // (column + 1) * ((1f / spanCount) * spacing)

                }


            }else if(layoutManager instanceof StaggeredGridLayoutManager){
                StaggeredGridLayoutManager gridLayoutManager =  (StaggeredGridLayoutManager)layoutManager;
                int spanCount = gridLayoutManager.getSpanCount();
                int index = position%spanCount;
                setItemDividerSize(outRect, position, parent, index,spanCount);
            }else if(layoutManager instanceof LinearLayoutManager){
                LinearLayoutManager gridLayoutManager =  (LinearLayoutManager)layoutManager;
                int spanCount = 1;
                int index = 0;
                if(gridLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL){
                     spanCount = 1;
                     index = 0;
                }else{
                    spanCount = gridLayoutManager.getItemCount();
                    index = position%spanCount;
                }
                setItemDividerSize(outRect, position, parent, index,spanCount);
            }
        }else{
            outRect.set(0, 0, getDividerSize(position, parent), 0);
        }

    }

    private void setItemDividerSize(Rect outRect, int position, RecyclerView parent, int index,int spanCount) {
        int size = getDividerSize(position, parent);
        int column = position % spanCount; // item column

        if(mOnlyFirst){
            if(index==0){
                outRect.set(size, 0,0, 0);
            }
        }else{

            outRect.left = size - column * size / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * size / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

        }
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicWidth();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns top margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return top margin
         */
        int dividerTopMargin(int position, RecyclerView parent);

        /**
         * Returns bottom margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return bottom margin
         */
        int dividerBottomMargin(int position, RecyclerView parent);
    }

    public static class Builder extends FlexibleDividerDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerTopMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerBottomMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int topMargin, final int bottomMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerTopMargin(int position, RecyclerView parent) {
                    return topMargin;
                }

                @Override
                public int dividerBottomMargin(int position, RecyclerView parent) {
                    return bottomMargin;
                }
            });
        }

        public Builder margin(int verticalMargin) {
            return margin(verticalMargin, verticalMargin);
        }

        public Builder marginResId(@DimenRes int topMarginId, @DimenRes int bottomMarginId) {
            return margin(mResources.getDimensionPixelSize(topMarginId),
                    mResources.getDimensionPixelSize(bottomMarginId));
        }

        public Builder marginResId(@DimenRes int verticalMarginId) {
            return marginResId(verticalMarginId, verticalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public VerticalDividerItemDecoration build() {
            checkBuilderParams();
            return new VerticalDividerItemDecoration(this);
        }
    }
}