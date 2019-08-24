package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tooltip.Tooltip;

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
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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
        Button btn_go_return = (Button) findViewById(R.id.btn_go_return);
        ImageView btn_report = (ImageView) findViewById(R.id.btn_report);
        btn_cancle.setOnClickListener(mOnClickListener);
        btn_go_return.setOnClickListener(mOnClickListener);
        btn_report.setOnClickListener(mOnClickListener);

        showTooltip(btn_report, Gravity.BOTTOM);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancle:
                    onBackPressed();

                    break;
                case R.id.btn_go_return:
                    onBackPressed();

                    break;
                case R.id.btn_report:
                    Intent intent1 = new Intent(PickRestLikeActivity.this, ReportActivity.class);
                    intent1.putExtra("title", "문의하기");
                    intent1.putExtra("feed_id", "addRestaurant");
                    intent1.putExtra("to_id", "");
                    startActivity(intent1);

                    break;
            }
        }
    };


    private void showTooltip(View v, int gravity) {
        float txtSize = 10;

        ImageView btn = (ImageView) v;
        Tooltip toolTip = new Tooltip.Builder(btn)
                .setText("원하는 음식점/지역이\n없으신가요?")
                .setTextColor(R.color.white)
                .setTextSize(txtSize)
                .setGravity(gravity)
                .setCornerRadius(8f)
                .setDismissOnClick(true)
                .show();

        Handler delayHandler = new Handler();
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toolTip.dismiss();
            }
        }, 1800);
    }

}