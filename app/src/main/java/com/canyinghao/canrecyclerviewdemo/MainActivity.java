package com.canyinghao.canrecyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangjian on 16/7/14.
 */
public class MainActivity extends AppCompatActivity {



    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle("CanRecyclerView");
    }



    @OnClick({R.id.btn_header,R.id.btn_viewpager})
    public void click(View v){

        switch (v.getId()){

            case R.id.btn_header:



                startActivity(new Intent(this,HeaderFooterActivity.class));
                break;

            case R.id.btn_viewpager:

                startActivity(new Intent(this,ViewPagerActivity.class));
                break;
        }



    }
}
