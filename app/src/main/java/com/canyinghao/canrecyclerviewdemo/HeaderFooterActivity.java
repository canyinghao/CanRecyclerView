package com.canyinghao.canrecyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanRVAdapter;
import com.canyinghao.canrecyclerview.CanRecyclerViewHeaderFooter;
import com.canyinghao.canrecyclerview.HorizontalDividerItemDecoration;
import com.canyinghao.canrecyclerview.RecyclerViewEmpty;
import com.canyinghao.canrecyclerview.VerticalDividerItemDecoration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;


/**
 * Created by yangjian on 16/7/14.
 */
public class HeaderFooterActivity extends AppCompatActivity implements CanRecyclerViewHeaderFooter.OnLoadMoreListener {



    Toolbar toolbar;

    RecyclerViewEmpty recycler;

    CanRecyclerViewHeaderFooter header;

    CanRecyclerViewHeaderFooter footer;



    ProgressBar pb;

    TextView tvLoadmore;

    CanRVAdapter<String> adapter;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);

        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.can_content_view);
        header = findViewById(R.id.header);
        footer = findViewById(R.id.footer);
        pb = findViewById(R.id.pb);
        tvLoadmore = findViewById(R.id.tv_loadmore);

        toolbar.setTitle("CanRecyclerViewHeaderFooter");


        adapter = new CanRVAdapter<String>(recycler, R.layout.item_header) {
            @Override
            protected void setView(CanHolderHelper helper, final int position, String bean) {


                if (position == 0) {

                    helper.setText(R.id.tv_content, bean);

                } else {
                    helper.setText(R.id.tv_content, R.string.appbar_scrolling_view_behavior);
                }

                helper.getView(R.id.tv_content).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(mContext, "item" + position, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {

            }


        };

        for (int i = 0; i < 19; i++) {

            adapter.addLastItem("ddd");
        }

        recycler.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .size(20)
                .newStyle()
                .build());

        recycler.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .size(20)
                .newStyle()
                .build());


//        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        recycler.setLayoutManager(new GridLayoutManager(this,3));
//        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recycler.setAdapter(adapter);


        header.attachTo(recycler, true);
        footer.attachTo(recycler, false);

        footer.setLoadMoreListener(this);

        findViewById(R.id.iv_head).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "click", Toast.LENGTH_SHORT).show();
            }
        });

    }





    int i = 0;

    @Override
    public void onLoadMore() {


        recycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 19; i++) {

                    adapter.addLastItem("ddd");
                }

                i++;

                if (i == 3) {
                    tvLoadmore.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    footer.setLoadEnable(false);
                }


                footer.loadMoreComplete();
            }
        }, 2000);


    }
}
