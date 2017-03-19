package com.canyinghao.canrecyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
    private int width;
    private int rowSpan = 1;

    private boolean isReversed;
    private boolean isVertical;

    private boolean isHeader = true;
    private RecyclerView.LayoutManager layoutManager;

    public CanItemDecoration(RecyclerView.LayoutManager manager) {

        setLayoutManager(manager);

    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        this.layoutManager = manager;
        initLayoutManager();
    }


    public CanItemDecoration setHeight(int height) {
        this.height = height;

        return this;
    }

    public CanItemDecoration setWidth(int width) {
        this.width = width;

        return this;
    }

    public CanItemDecoration setIsHeader(boolean isHeader) {


        this.isHeader = isHeader;

        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);


        boolean relatedPosition = false;

        initLayoutManager();

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


        int heightOffset = relatedPosition && isVertical ? height : 0;
        int widthOffset = relatedPosition && !isVertical ? width : 0;

        if (isHeader) {

            if (isReversed) {
                outRect.bottom = heightOffset;
                outRect.right = widthOffset;
            } else {
                outRect.top = heightOffset;
                outRect.left = widthOffset;
            }
        } else {

            if (isReversed) {

                outRect.top = heightOffset;
                outRect.left = widthOffset;

            } else {


                outRect.bottom = heightOffset;
                outRect.right = widthOffset;
            }
        }


    }


    private void initLayoutManager() {

        if (layoutManager instanceof GridLayoutManager) {


            GridLayoutManager grid = (GridLayoutManager) layoutManager;


            this.rowSpan = grid.getSpanCount();

            this.isReversed = grid.getReverseLayout();

            this.isVertical = grid.getOrientation() == LinearLayoutManager.VERTICAL;

        } else if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager linear = (LinearLayoutManager) layoutManager;

            this.rowSpan = 1;
            this.isReversed = linear.getReverseLayout();

            this.isVertical = linear.getOrientation() == LinearLayoutManager.VERTICAL;

        } else if (layoutManager instanceof StaggeredGridLayoutManager) {

            StaggeredGridLayoutManager staggeredGrid = (StaggeredGridLayoutManager) layoutManager;

            this.rowSpan = staggeredGrid.getSpanCount();

            this.isReversed = staggeredGrid.getReverseLayout();

            this.isVertical = staggeredGrid.getOrientation() == LinearLayoutManager.VERTICAL;
        }


    }


}