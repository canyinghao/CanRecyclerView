package com.canyinghao.canrecyclerviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangjian on 16/7/14.
 */
public class HeaderFooterActivity extends AppCompatActivity implements CanRecyclerViewHeaderFooter.OnLoadMoreListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.can_content_view)
    RecyclerViewEmpty recycler;
    @BindView(R.id.header)
    CanRecyclerViewHeaderFooter header;
    @BindView(R.id.footer)
    CanRecyclerViewHeaderFooter footer;


    @BindView(R.id.pb)
    ProgressBar pb;
    @BindView(R.id.tv_loadmore)
    TextView tvLoadmore;

    CanRVAdapter<String> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header);
        ButterKnife.bind(this);

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
                .size(2).build());

        recycler.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .size(2).build());


        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
//        recycler.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.HORIZONTAL,true));
//        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        recycler.setAdapter(adapter);


        header.attachTo(recycler, true);
        footer.attachTo(recycler, false);

        footer.setLoadMoreListener(this);


    }


    @OnClick(R.id.iv_head)
    public void click(View v) {


        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();


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
