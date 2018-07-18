package com.canyinghao.canrecyclerviewdemo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;
import com.canyinghao.canrecyclerview.CanRecyclerViewPager;
import com.canyinghao.canrecyclerview.FullyGridLayoutManager;
import com.canyinghao.canrecyclerview.FullyLinearLayoutManager;
import com.canyinghao.canrecyclerview.HorizontalDividerItemDecoration;
import com.canyinghao.canrecyclerview.RecyclerViewEmpty;
import com.canyinghao.canrecyclerview.VerticalDividerItemDecoration;
import com.canyinghao.canrecyclerviewdemo.model.MainBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ViewPagerActivity extends Activity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    CanRecyclerViewPager viewpager;

    Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        ButterKnife.bind(this);
        context = this;
        toolbar.setTitle(R.string.app_name);


        initViewPager();
    }


    protected void initViewPager() {

        final int[] colors = {R.color.report_color1, R.color.report_color2, R.color.report_color3, R.color.report_color4};
        viewpager = (CanRecyclerViewPager) findViewById(R.id.viewpager);
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false);
        viewpager.setLayoutManager(layout);
        CanRVAdapter<MainBean> adapter = new CanRVAdapter<MainBean>(viewpager, R.layout.item_main) {

            @Override
            protected void setView(CanHolderHelper helper, int i, MainBean bean) {
                helper.getConvertView().setBackgroundResource(colors[i]);

                if (i == 0 || i == 1) {
                    helper.setVisibility(R.id.scrollView, View.GONE);
                    helper.setVisibility(R.id.rv_item2, View.VISIBLE);
                } else {
                    helper.setVisibility(R.id.scrollView, View.VISIBLE);
                    helper.setVisibility(R.id.rv_item2, View.GONE);
                }


                final CanRVAdapter<MainBean> adapterItem = new CanRVAdapter<MainBean>(viewpager, R.layout.item_3) {

                    @Override
                    protected void setView(CanHolderHelper helper, int i, MainBean bean) {

                        helper.setText(R.id.tv_title, bean.mText);
                        helper.setText(R.id.tv_content, bean.mText);
                    }

                    @Override
                    protected void setItemListener(CanHolderHelper helper) {

                    }
                };

                for (int j = 0; j < 20; j++) {
                    adapterItem.addLastItem(new MainBean("this is itme " + j));
                }


                RecyclerViewEmpty rv_item1 = helper.getView(R.id.rv_item1);
                RecyclerViewEmpty rv_item2 = helper.getView(R.id.rv_item2);

                LinearLayoutManager layout ;

                switch (i) {

                    case 0:
                        layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                                false);

                        rv_item2.setLayoutManager(layout);
                        rv_item2.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.line).size(10).build());
                        rv_item2.setEmptyView(helper.getView(R.id.empty));
                        rv_item2.setAdapter(adapterItem);
                        adapterItem.setOnItemListener(new CanOnItemListener() {


                            public void onItemChildClick(View view, int position) {

                                adapterItem.removeItem(position);
                                App.getInstance().show("deleteItem:" + position);
                            }


                        });
                        break;
                    case 1:

                        layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                                false);
                        rv_item2.setLayoutManager(layout);
                        rv_item2.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.line).size(5).showLastDivider().build());
                        rv_item2.setEmptyView(helper.getView(R.id.empty));


                        adapterItem.clear();
                        adapterItem.addLastItem(new MainBean("点击删除"));
                        adapterItem.setOnItemListener(new CanOnItemListener() {


                            public void onItemChildClick(View view, int position) {

                                adapterItem.clear();
                            }




                        });

                        rv_item2.setAdapter(adapterItem);

                        break;
                    case 2:

                        layout = new FullyGridLayoutManager(context,
                                2);
                        rv_item1.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.line).size(5).showLastDivider().build());
                        rv_item1.addItemDecoration(new VerticalDividerItemDecoration.Builder(context).colorResId(R.color.line).size(5).showLastDivider().build());

                        rv_item1.setLayoutManager(layout);
                        rv_item1.setAdapter(adapterItem);
                        break;
                    case 3:

                        layout = new FullyLinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                                false);
                        rv_item1.addItemDecoration(new HorizontalDividerItemDecoration.Builder(context).colorResId(R.color.color_main).size(2).showLastDivider().build());

                        rv_item1.setLayoutManager(layout);
                        rv_item1.setAdapter(adapterItem);
                        break;
                }


            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {

            }


        };
        adapter.setRatio(1);

        if(Build.VERSION.SDK_INT>=11){
//            viewpager.addScaleListener(0.9f);
        }

        viewpager.setAdapter(adapter);

        viewpager.setHasFixedSize(true);
        viewpager.setLongClickable(true);
        viewpager.setOnePage(true);








        viewpager.addOnPageChangedListener(new CanRecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                Log.e(CanRecyclerViewPager.TAG, "oldPosition:" + oldPosition + " newPosition:" + newPosition);
                App.getInstance().show("oldPosition:" + oldPosition + " newPosition:" + newPosition);
            }
        });


        final String[] array_list = getResources().getStringArray(R.array.array_list);

        viewpager.setTabLayoutSupport(tabLayout, new CanRecyclerViewPager.ViewPagerTabLayoutAdapter() {
            @Override
            public String getPageTitle(int position) {
                return array_list[position];
            }

            @Override
            public int getItemCount() {
                return array_list.length;
            }
        });


        List<MainBean> list = new ArrayList<>();
        list.add(new MainBean("xxx"));
        list.add(new MainBean("xxx"));
        list.add(new MainBean("xxx"));
        list.add(new MainBean("xxx"));
        adapter.setList(list);


    }
}
