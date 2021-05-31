package com.canyinghao.canrecyclerview;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;


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
public class CanLinearLayoutManager extends LinearLayoutManagerFix{
    private static final int DEFAULT_EXTRA_LAYOUT_SPACE = 600;
    private int extraLayoutSpace = -1;


    public CanLinearLayoutManager(Context context) {
        super(context);

    }

    public CanLinearLayoutManager(Context context, int extraLayoutSpace) {
        super(context);
        this.extraLayoutSpace = extraLayoutSpace;
    }

    public CanLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);

    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.extraLayoutSpace = extraLayoutSpace;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (extraLayoutSpace > 0) {
            return extraLayoutSpace;
        }
        return DEFAULT_EXTRA_LAYOUT_SPACE;
    }
}
