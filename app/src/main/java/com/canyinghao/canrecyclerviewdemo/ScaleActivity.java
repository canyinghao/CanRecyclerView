package com.canyinghao.canrecyclerviewdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;
import com.canyinghao.canrecyclerview.CanScaleRecyclerView;

/**
 * Created by yangjian on 16/9/24.
 */

public class ScaleActivity extends AppCompatActivity {
    CanScaleRecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        recycler = (CanScaleRecyclerView) findViewById(R.id.recycler);


        CanRVAdapter<Integer> adapter = new CanRVAdapter<Integer>(recycler, R.layout.item_image) {
            @Override
            protected void setView(CanHolderHelper helper, int position, Integer bean) {

                ImageView iv = helper.getImageView(R.id.item_image);

                Glide.with(mContext).load(bean).into(iv);

            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {

            }


        };


        adapter.addLastItem(R.mipmap.image1);
        adapter.addLastItem(R.mipmap.image2);
        adapter.addLastItem(R.mipmap.image3);
        adapter.addLastItem(R.mipmap.image4);
        adapter.addLastItem(R.mipmap.image5);
        adapter.addLastItem(R.mipmap.image6);
        adapter.addLastItem(R.mipmap.image7);


//        recycler.setCanScale(false);
//        recycler.setCanDoubleScale(false);
//        recycler.setCanDoubleTapListener(false);
//        recycler.setCanSingleTapListener(false);
//        recycler.setCanLongListener(false);
//        recycler.setCanScaleListener(false);



        recycler.setAdapter(adapter);

        recycler.setOnGestureListener(new CanScaleRecyclerView.OnGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Log.e("Canyinghao","onScale");
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.e("Canyinghao","onSingleTapConfirmed");
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.e("Canyinghao","onDoubleTap");
                return false;
            }

            @Override
            public boolean onLongClick(MotionEvent e) {

                Log.e("Canyinghao","onLongClick");
                return false;
            }
        });


    }


}
