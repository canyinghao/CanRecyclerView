package com.canyinghao.canrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

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
public class CanItemDecoration extends RecyclerView.ItemDecoration {

    private int height;


    private boolean isHeader = true;



    public CanItemDecoration setHeight(int height) {
        this.height = height;

        return this;
    }


    public CanItemDecoration setIsHeader(boolean isHeader) {

        this.isHeader = isHeader;
        return this;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);


        try {
            setOutRect(outRect, view, parent);
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }

    private void setOutRect(Rect outRect, View view, RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {

            int position = parent.getChildAdapterPosition(view);

            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup lookup = gridLayoutManager.getSpanSizeLookup();

            int spanCount = gridLayoutManager.getSpanCount();
            int spanGroup = lookup.getSpanGroupIndex(position, spanCount);

            int count = gridLayoutManager.getItemCount();
            int lastGroup = lookup.getSpanGroupIndex(count - 1, spanCount);

            if (isHeader && spanGroup == 0) {
                outRect.set(0, height, 0, 0);
            }

            if (!isHeader && spanGroup == lastGroup) {

                outRect.set(0, 0, 0, height);

            }

        } else {

            int rowSpan = 1;

            if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                rowSpan = gridLayoutManager.getSpanCount();
            }


            boolean relatedPosition = false;


            if (isHeader) {

                relatedPosition = parent.getChildLayoutPosition(view) < rowSpan;

            } else {

                int lastSum = 1;


                int itemCount = layoutManager.getItemCount();

                if (itemCount > 0 && rowSpan > 1) {

                    lastSum = itemCount % rowSpan;

                    if (lastSum == 0) {
                        lastSum = rowSpan;
                    }
                }

                int count = itemCount - lastSum;


                int lastPosition = parent.getChildLayoutPosition(view);


                relatedPosition = lastPosition >= count;

            }


            if (relatedPosition) {
                if (isHeader) {

                    outRect.set(0, height, 0, 0);

                } else {

                    outRect.set(0, 0, 0, height);

                }
            }


        }
    }


}