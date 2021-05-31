package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/**
 * 可以设置EmptyView的RecyclerView，需要在布局中加入EmptyView
 * Created by canyinghao on 15/6/6.
 */
public class RecyclerViewEmpty extends RecyclerView {

    protected View emptyView;

    protected boolean hideOrShowRV;

    public static  int CACHE_SIZE = 0;

    protected AdapterDataObserver emptyObserver = new AdapterDataObserver() {


        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();


            if (adapter != null) {
                if (adapter.getItemCount() <= 0) {
                    if (emptyView != null) {
                        emptyView.setVisibility(View.VISIBLE);
                    }

                    if (hideOrShowRV) {
                        RecyclerViewEmpty.this.setVisibility(View.GONE);
                    }

                } else {
                    if (emptyView != null) {
                        emptyView.setVisibility(View.GONE);
                    }

                    if (hideOrShowRV) {
                        RecyclerViewEmpty.this.setVisibility(View.VISIBLE);
                    }


                }

            }

        }
    };

    public RecyclerViewEmpty(Context context) {
        super(context);
    }

    public RecyclerViewEmpty(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEmpty(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    public void setAdapter(Adapter adapter) {

        Adapter oldAdapter = getAdapter();
        if (oldAdapter != null && emptyObserver != null) {
            oldAdapter.unregisterAdapterDataObserver(emptyObserver);
        }
        if (adapter != null && emptyObserver != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
            setItemViewCacheSize(CACHE_SIZE);
        }
        super.setAdapter(adapter);


        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }


    public void setHideOrShowRV(boolean hideOrShowRV) {
        this.hideOrShowRV = hideOrShowRV;
    }

    public boolean isHideOrShowRV() {
        return hideOrShowRV;
    }

    public void onChanged(){
        if(emptyObserver!=null){
            emptyObserver.onChanged();
        }
    }
}