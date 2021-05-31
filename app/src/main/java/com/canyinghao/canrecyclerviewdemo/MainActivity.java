package com.canyinghao.canrecyclerviewdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


/**
 * Created by yangjian on 16/7/14.
 */
public class MainActivity extends AppCompatActivity {




    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);

        findViewById(R.id.btn_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(view);
            }
        });


        findViewById(R.id.btn_viewpager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(view);
            }
        });


        findViewById(R.id.btn_scale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                click(view);
            }
        });

        toolbar.setTitle("CanRecyclerView");
    }



    public void click(View v){

        switch (v.getId()){

            case R.id.btn_header:



                startActivity(new Intent(this,HeaderFooterActivity.class));
                break;

            case R.id.btn_viewpager:

                startActivity(new Intent(this,ViewPagerActivity.class));
                break;

            case R.id.btn_scale:

                startActivity(new Intent(this,ScaleActivity.class));
                break;
        }



    }
}
