package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.DialogRestListAdapter;
import honbab.voltage.com.adapter.TabPagerAreaAdapter;
import honbab.voltage.com.data.CityData;
import honbab.voltage.com.data.OneTimeRestLikeListData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.OneFeedRestLikeListTask;

public class PickRestLikeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public ProgressBar progressBar_rest;
    public RecyclerView recyclerView_rest;
    public DialogRestListAdapter mAdapter_rest;

    public String area_cd = "";
    public String timelike_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickrestlike);

        Intent intent = getIntent();
        timelike_id = intent.getStringExtra("timelike_id");

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

//        ArrayList<OneTimeRestLikeListData> oneTimeRestLikeList;
        OneTimeRestLikeListData oneTimeRestLikeListData;
        ArrayList<CityData> cityList;
        ArrayList<RestData> restList;
        try {
            oneTimeRestLikeListData = new OneFeedRestLikeListTask(PickRestLikeActivity.this).execute(timelike_id, area_cd).get();
            cityList = oneTimeRestLikeListData.getCityList();
            restList = oneTimeRestLikeListData.getRestList();

            for (int i = 0; i < cityList.size(); i++) {
                tabLayout.addTab(tabLayout.newTab().setText(cityList.get(i).getCity_name()));
            }
//            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            viewPager = (ViewPager) findViewById(R.id.pager);
            TabPagerAreaAdapter pagerAdapter = new TabPagerAreaAdapter(getSupportFragmentManager(), PickRestLikeActivity.this, cityList);
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//            tabLayout.setupWithViewPager(viewPager);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });


            progressBar_rest = (ProgressBar) findViewById(R.id.progressBar_rest);

            GridLayoutManager layoutManager2 = new GridLayoutManager(PickRestLikeActivity.this, 3);
//        Log.e("abc", "restList.size() = " + restList.size());
            recyclerView_rest = (RecyclerView) findViewById(R.id.recyclerView_rest);
            recyclerView_rest.setLayoutManager(layoutManager2);
            if (restList.size() > 0) {
                mAdapter_rest = new DialogRestListAdapter(PickRestLikeActivity.this, restList);
                recyclerView_rest.setAdapter(mAdapter_rest);
            } else {
                mAdapter_rest = new DialogRestListAdapter();
                recyclerView_rest.setAdapter(mAdapter_rest);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ImageView btn_cancle = (ImageView) findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
////                        ((SelectFeedFragment) fragment).area_cd,
//                        ((SelectFeedFragment) fragment).feed_rest_id,
//                        "");

//                dlg.dismiss();
                onBackPressed();
            }
        });

        Button btn_go_return = (Button) findViewById(R.id.btn_go_return);
        btn_go_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
////                        ((SelectFeedFragment) fragment).area_cd,
//                        ((SelectFeedFragment) fragment).feed_rest_id,
//                        "");

//                dismiss();
                onBackPressed();
            }
        });


    }
}