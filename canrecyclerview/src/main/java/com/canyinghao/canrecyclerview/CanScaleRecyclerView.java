package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

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
public class CanScaleRecyclerView extends RecyclerViewEmpty {


    private static final int FACTOR = 1;


    private float mCurrentScaleFactor;

    private float mLastTouchX;
    private float mLastTouchY;

    private float mOffsetX;
    private float mOffsetY;

    private float centerX;
    private float centerY;

    private float mMinScaleFactor = 0.8f;
    private float mMidScaleFactor = 2f;
    private float mMaxScaleFactor = 3f;


    private boolean isScale;

    private boolean isTwoStage;


//    是否回调单击事件
    private boolean isCanSingleTapListener = true;

//    是否回调双击事件
    private boolean isCanDoubleTapListener = true;

//    是否回调缩放事件
    private boolean isCanScaleListener = true;
    //    是否回调缩放事件
    private boolean isCanLongListener = true;

//    是否可双击缩放
    private boolean isCanDoubleScale = true;

//    是否可缩放
    private boolean isCanScale = true;




    private GestureDetector mGestureDetector;

    private ScaleGestureDetector mScaleGestureDetector;

    private OnGestureListener mOnGestureListener;


    private Runnable mAutoScaleRunnable;

    private long mZoomDuration = 200;

    private final Interpolator mZoomInterpolator = new AccelerateDecelerateInterpolator();

    public interface OnGestureListener {
        boolean onScale(ScaleGestureDetector detector);

        boolean onSingleTapConfirmed(MotionEvent e);

        boolean onDoubleTap(MotionEvent e);

        boolean onLongClick(MotionEvent e);
    }


    /**
     * 设置缩放时间
     *
     * @param duration long
     */
    public void setZoomTransitionDuration(long duration) {
        duration = duration < 0 ? 200 : duration;
        mZoomDuration = duration;
    }


    /**
     * 双击缩放时平滑缩放
     */
    private class AnimatedZoomRunnable implements Runnable {

        private final long mStartTime;
        private final float mZoomStart, mZoomEnd;

        public AnimatedZoomRunnable(final float targetZoom) {

            mStartTime = System.currentTimeMillis();
            mZoomStart = mCurrentScaleFactor;
            mZoomEnd = targetZoom;

        }

        @Override
        public void run() {


            float t = interpolate();

            mCurrentScaleFactor = mZoomStart + t * (mZoomEnd - mZoomStart);


            if (t < 1f) {
                postOnAnimation(CanScaleRecyclerView.this, this);
            }


            checkOffsetBorder();
            invalidate();


        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            t = mZoomInterpolator.getInterpolation(t);
            return t;
        }
    }


    private void postOnAnimation(View view, Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postOnAnimation(runnable);
        } else {
            view.postDelayed(runnable, 16L);
        }
    }


    private class CanLinearLayoutManager extends LinearLayoutManager {
        public CanLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public int scrollVerticallyBy(int dy, Recycler recycler, State state) {


            return super.scrollVerticallyBy((int) Math.ceil(dy / mCurrentScaleFactor), recycler, state);

        }
    }


    public CanScaleRecyclerView(Context context) {
        this(context, null);
    }

    public CanScaleRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanScaleRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setLayoutManager(new CanLinearLayoutManager(context, VERTICAL, false));

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CanScaleRecyclerView);
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.CanScaleRecyclerView_minScaleFactor) {
                mMinScaleFactor = ta.getFloat(attr, 0.8f);
            } else if (attr == R.styleable.CanScaleRecyclerView_maxScaleFactor) {
                mMaxScaleFactor = ta.getFloat(attr, 3);
            } else if (attr == R.styleable.CanScaleRecyclerView_isTwoStage) {
                isTwoStage = ta.getBoolean(attr, false);
            } else if (attr == R.styleable.CanScaleRecyclerView_isDoubleScale) {
                isCanDoubleScale = ta.getBoolean(attr, true);
            }
        }
        ta.recycle();

        mMidScaleFactor = (FACTOR + mMaxScaleFactor) / 2;
        mCurrentScaleFactor = FACTOR;


        initDetector();
    }


    /**
     * 设置手势监听
     */
    private void initDetector() {
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {


            boolean fromBig = false;


            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {


                // 获取缩放的中心点
                centerX = detector.getFocusX();
                centerY = detector.getFocusY();
                isScale = true;

                fromBig = mCurrentScaleFactor > FACTOR;

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                if(isCanScale){

                    float tempFactor = mCurrentScaleFactor;
                    mCurrentScaleFactor *= detector.getScaleFactor();
                    mCurrentScaleFactor = Math.max(mMinScaleFactor, Math.min(mCurrentScaleFactor, mMaxScaleFactor));

                    if (fromBig) {
                        if (tempFactor >= mCurrentScaleFactor) {

                            if (mCurrentScaleFactor <= FACTOR) {
                                mCurrentScaleFactor = FACTOR;
                            }
                        }
                    }


                    CanScaleRecyclerView.this.invalidate();


                    if (isCanScaleListener&&mOnGestureListener != null)
                    {
                        mOnGestureListener.onScale(detector);
                    }
                    return true;

                }else{

                   return super.onScale(detector);
                }




            }


            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

                isScale = false;

                if (mCurrentScaleFactor < FACTOR) {


                    postOnAnimation(CanScaleRecyclerView.this, new AnimatedZoomRunnable(FACTOR));

                } else {

                    if (!isTwoStage && mCurrentScaleFactor > mMidScaleFactor) {


                        postOnAnimation(CanScaleRecyclerView.this, new AnimatedZoomRunnable(mMidScaleFactor));
                    }


                }
                checkOffsetBorder();

                super.onScaleEnd(detector);
            }
        }

        );

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener()

        {


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if(isCanSingleTapListener){
                    //点击
                    return mOnGestureListener != null && mOnGestureListener.onSingleTapConfirmed(e);

                }else{
                    return super.onSingleTapConfirmed(e);
                }

            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                if(isCanLongListener){
                    if (mOnGestureListener != null) {
                        mOnGestureListener.onLongClick(e);
                    }
                }


            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {


                //双击缩放
                centerX = e.getRawX();
                centerY = e.getRawY();

                if (mAutoScaleRunnable != null) {
                    removeCallbacks(mAutoScaleRunnable);
                }

                if (isCanDoubleScale) {

                    if (isTwoStage) {

                        if (mCurrentScaleFactor < mMidScaleFactor) {

                            mAutoScaleRunnable = new AnimatedZoomRunnable(mMidScaleFactor);

                        } else if (mCurrentScaleFactor < mMaxScaleFactor) {


                            mAutoScaleRunnable = new AnimatedZoomRunnable(mMaxScaleFactor);
                        } else {

                            mAutoScaleRunnable = new AnimatedZoomRunnable(FACTOR);
                        }

                    } else {


                        if (mCurrentScaleFactor < mMidScaleFactor) {

                            mAutoScaleRunnable = new AnimatedZoomRunnable(mMidScaleFactor);

                        } else {
                            mAutoScaleRunnable = new AnimatedZoomRunnable(FACTOR);
                        }


                    }


                    postOnAnimation(CanScaleRecyclerView.this, mAutoScaleRunnable);

                }


                if(isCanDoubleTapListener){
                    if (mOnGestureListener != null) {
                        mOnGestureListener.onDoubleTap(e);
                    }
                    return true;
                }else{
                    return super.onDoubleTap(e);
                }

            }
        }

        );
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        if(isCanScale){
            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            if (mCurrentScaleFactor <= 1.0f) {
                mOffsetX = 0.0f;
                mOffsetY = 0.0f;
            }


            canvas.translate(mOffsetX, mOffsetY);//偏移

            canvas.scale(mCurrentScaleFactor, mCurrentScaleFactor, centerX, centerY);//缩放
            super.dispatchDraw(canvas);
            canvas.restore();
        }else{
            super.dispatchDraw(canvas);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(isCanScale){

            super.onTouchEvent(event);

            try {
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                mScaleGestureDetector.onTouchEvent(event);

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (isScale) {

                return true;
            }


            if (mCurrentScaleFactor == 1) {

                return true;
            }


            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();

                    break;
                case MotionEvent.ACTION_MOVE:

                    float mainPointX = event.getX();
                    float mainPointY = event.getY();


                    //滑动时偏移
                    mOffsetX += (mainPointX - mLastTouchX);


                    mOffsetY += (mainPointY - mLastTouchY);


                    mLastTouchX = mainPointX;
                    mLastTouchY = mainPointY;


                    checkOffsetBorder();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mLastTouchX = event.getX();
                    mLastTouchY = event.getY();
                    break;
            }

            return true;

        }else{
          return   super.onTouchEvent(event);

        }


    }


    /**
     * 检测偏移边界,通过缩放中心距左右、上下边界的比例确定
     */
    private void checkOffsetBorder() {

        if (mCurrentScaleFactor < FACTOR) {
            return;
        }

        float sumOffsetX = getWidth() * (mCurrentScaleFactor - FACTOR);
        float sumOffsetY = getHeight() * (mCurrentScaleFactor - FACTOR);


        float numX = (getWidth() - centerX) / centerX + 1;


        float offsetLeftX = sumOffsetX / numX;


        float offsetRightX = ((getWidth() - centerX) / centerX) * offsetLeftX;


        float numY = (getHeight() - centerY) / centerY + 1;


        float offsetTopY = sumOffsetY / numY;


        float offsetBottomY = ((getHeight() - centerY) / centerY) * offsetTopY;


        if (mOffsetX > offsetLeftX) {
            mOffsetX = offsetLeftX;

        }

        if (mOffsetX < -offsetRightX) {

            mOffsetX = -offsetRightX;
        }


        if (mOffsetY > offsetTopY) {
            mOffsetY = offsetTopY;

        }

        if (mOffsetY < -offsetBottomY) {

            mOffsetY = -offsetBottomY;
        }


    }



    public void resetSize(){

        if(mCurrentScaleFactor!=FACTOR){
            postOnAnimation(CanScaleRecyclerView.this, new AnimatedZoomRunnable(FACTOR));
        }
    }

    public void setCanSingleTapListener(boolean canSingleTapListener) {
        isCanSingleTapListener = canSingleTapListener;
    }

    public void setCanDoubleTapListener(boolean canDoubleTapListener) {
        isCanDoubleTapListener = canDoubleTapListener;
    }

    public void setCanScaleListener(boolean canScaleListener) {
        isCanScaleListener = canScaleListener;
    }

    public void setCanLongListener(boolean canLongListener) {
        isCanLongListener = canLongListener;
    }

    public void setCanDoubleScale(boolean canDoubleScale) {
        isCanDoubleScale = canDoubleScale;
    }

    public void setCanScale(boolean canScale) {
        isCanScale = canScale;
    }

    public float getMinScaleFactor() {
        return mMinScaleFactor;
    }

    public void setMinScaleFactor(float minScaleFactor) {
        this.mMinScaleFactor = minScaleFactor;
    }

    public float getMaxScaleFactor() {
        return mMaxScaleFactor;
    }

    public void setMaxScaleFactor(float maxScaleFactor) {
        this.mMaxScaleFactor = maxScaleFactor;
    }

    public boolean isTwoStage() {
        return isTwoStage;
    }

    public void setTwoStage(boolean twoStage) {
        isTwoStage = twoStage;
    }


    public boolean isCanSingleTapListener() {
        return isCanSingleTapListener;
    }

    public boolean isCanDoubleTapListener() {
        return isCanDoubleTapListener;
    }

    public boolean isCanScaleListener() {
        return isCanScaleListener;
    }

    public boolean isCanLongListener() {
        return isCanLongListener;
    }

    public boolean isCanDoubleScale() {
        return isCanDoubleScale;
    }

    public boolean isCanScale() {
        return isCanScale;
    }

    public OnGestureListener getOnGestureListener() {
        return mOnGestureListener;
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.mOnGestureListener = onGestureListener;
    }


}
