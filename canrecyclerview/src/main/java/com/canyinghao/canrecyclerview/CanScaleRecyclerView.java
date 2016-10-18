package com.canyinghao.canrecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

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

    private int mAutoTime = 10;
    private float mAutoBigger = 1.04f;
    private float mAutoSmall = 0.96f;

    private boolean isScale;

    private boolean isTwoStage;

    private boolean isDoubleScale = true;

    private GestureDetector mGestureDetector;

    private ScaleGestureDetector mScaleGestureDetector;

    private OnGestureListener mOnGestureListener;

    public interface OnGestureListener {
        boolean onScale(ScaleGestureDetector detector);

        boolean onSingleTapConfirmed(MotionEvent e);

        boolean onDoubleTap(MotionEvent e);
    }


    /**
     * 双击缩放时平滑缩放
     */
    private class AutoScaleRunnable implements Runnable {

        private float mTargetScaleFactor;

        private float mGrad;


        private AutoScaleRunnable(float TargetScale, float grad) {
            mTargetScaleFactor = TargetScale;
            mGrad = grad;
        }

        @Override
        public void run() {
            if ((mGrad > 1.0f && mCurrentScaleFactor < mTargetScaleFactor)
                    || (mGrad < 1.0f && mCurrentScaleFactor > mTargetScaleFactor)) {

                mCurrentScaleFactor *= mGrad;

                if (mGrad > 1.0f) {

                    if (mCurrentScaleFactor >= mTargetScaleFactor) {
                        mCurrentScaleFactor = mTargetScaleFactor;
                    }

                } else {

                    if (mCurrentScaleFactor <= mTargetScaleFactor) {
                        mCurrentScaleFactor = mTargetScaleFactor;
                    }

                }
                postDelayed(this, mAutoTime);
            } else {
                mCurrentScaleFactor = mTargetScaleFactor;
            }

            checkOffsetBorder();
            invalidate();
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
            } else if (attr == R.styleable.CanScaleRecyclerView_autoScaleTime) {
                mAutoTime = ta.getInt(attr, 10);
            } else if (attr == R.styleable.CanScaleRecyclerView_isTwoStage) {
                isTwoStage = ta.getBoolean(attr, false);
            } else if (attr == R.styleable.CanScaleRecyclerView_isDoubleScale) {
                isDoubleScale = ta.getBoolean(attr, true);
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


                CanScaleRecyclerView.this.

                        invalidate();


                if (mOnGestureListener != null)

                {
                    mOnGestureListener.onScale(detector);
                }

                return true;
            }


            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

                isScale = false;

                if (mCurrentScaleFactor < FACTOR) {

                    postDelayed(new AutoScaleRunnable(FACTOR, mAutoBigger), mAutoTime);

                } else {

                    if (!isTwoStage && mCurrentScaleFactor > mMidScaleFactor) {

                        postDelayed(new AutoScaleRunnable(mMidScaleFactor, mAutoSmall), mAutoTime);
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

                //点击
                return mOnGestureListener != null && mOnGestureListener.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                //双击缩放
                centerX = e.getRawX();
                centerY = e.getRawY();

                if (isDoubleScale) {

                    if (isTwoStage) {

                        if (mCurrentScaleFactor < mMidScaleFactor) {
                            postDelayed(new AutoScaleRunnable(mMidScaleFactor, mAutoBigger), mAutoTime);
                        } else if (mCurrentScaleFactor < mMaxScaleFactor) {
                            postDelayed(new AutoScaleRunnable(mMaxScaleFactor, mAutoBigger), mAutoTime);
                        } else {
                            postDelayed(new AutoScaleRunnable(FACTOR, mAutoSmall), mAutoTime);
                        }

                    } else {


                        if (mCurrentScaleFactor < mMidScaleFactor) {
                            postDelayed(new AutoScaleRunnable(mMidScaleFactor, mAutoBigger), mAutoTime);
                        } else {
                            postDelayed(new AutoScaleRunnable(FACTOR, mAutoSmall), mAutoTime);
                        }


                    }
                }


                if (mOnGestureListener != null) {
                    mOnGestureListener.onDoubleTap(e);
                }
                return true;
            }
        }

        );
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save(Canvas.MATRIX_SAVE_FLAG);
        if (mCurrentScaleFactor <= 1.0f) {
            mOffsetX = 0.0f;
            mOffsetY = 0.0f;
        }


        canvas.translate(mOffsetX, mOffsetY);//偏移

        canvas.scale(mCurrentScaleFactor, mCurrentScaleFactor, centerX, centerY);//缩放
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (mGestureDetector.onTouchEvent(event)) {

            return true;

        }


        mScaleGestureDetector.onTouchEvent(event);


        if (isScale) {

            return true;
        }


        if(mCurrentScaleFactor==1){

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


    public int getAutoTime() {
        return mAutoTime;
    }

    public void setAutoTime(int autoTime) {
        this.mAutoTime = autoTime;
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

    public boolean isDoubleScale() {
        return isDoubleScale;
    }

    public void setDoubleScale(boolean doubleScale) {
        isDoubleScale = doubleScale;
    }

    public OnGestureListener getOnGestureListener() {
        return mOnGestureListener;
    }

    public void setOnGestureListener(OnGestureListener onGestureListener) {
        this.mOnGestureListener = onGestureListener;
    }


}
