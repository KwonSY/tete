package honbab.voltage.com.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import honbab.voltage.com.adapter.DialogRestListAdapter;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;

public class PickRestDialog {
    private Context mContext;
    public Dialog dlg;
    private Fragment fragment;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //    public RecyclerView recyclerView_city, recyclerView_area, recyclerView_rest;
    public RecyclerView recyclerView_rest;
//    private DialogCityListAdapter mAdapter_city;
    //    public DialogAreaListAdapter mAdapter_area;
    private DialogRestListAdapter mAdapter_rest;

    public PickRestDialog(Context mContext) {
        this.mContext = mContext;
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    public void callFunction() {
        final Dialog dlg = new Dialog(mContext);
//        Dialog dlg = new Dialog(mContext);

        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_pickrest);
        WindowManager.LayoutParams params = dlg.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        dlg.getWindow().setAttributes(params);
        dlg.show();

//        FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace();



        tabLayout = (TabLayout) dlg.findViewById(R.id.tabLayout_city);
//        tabLayout.addTab(tabLayout.newTab().setText("Tab One"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab Two"));
//        tabLayout.addTab(tabLayout.newTab().setText("Tab Three"));
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

//        try {
//            ArrayList<CityData> cityList;
//            cityList = new OneFeedRestLikeListTask(mContext).execute("").get();
//
//            for (int i = 0; i < cityList.size(); i++) {
//                tabLayout.addTab(tabLayout.newTab().setText(cityList.get(i).getCity_name()));
//            }
//            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


            viewPager = (ViewPager) dlg.findViewById(R.id.viewPager_area);


//            TabPagerAdapterTest pagerAdapter = new TabPagerAdapterTest(((MainActivity) mContext).getSupportFragmentManager(), tabLayout.getTabCount());
//            viewPager.setAdapter(pagerAdapter);
//            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//            TabPagerAreaAdapter tabPagerAreaAdapter = new TabPagerAreaAdapter(((MainActivity) mContext).getSupportFragmentManager(), mContext, cityList);
//            DialogAreaFragment dialogAreaFragment = new DialogAreaFragment();
//            tabPagerAreaAdapter.addFragment(dialogAreaFragment, "ssSss");
//            viewPager.setAdapter(tabPagerAreaAdapter);
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
//            tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
//                @Override
//                public void onTabSelected(TabLayout.Tab tab) {
//                    viewPager.setCurrentItem(tab.getPosition());
////                    int pos = tab.getPosition() ;
////                    changeView(pos) ;
//                }
//
//                @Override
//                public void onTabUnselected(TabLayout.Tab tab) {
//
//                }
//
//                @Override
//                public void onTabReselected(TabLayout.Tab tab) {
//
//                }
//            });

//            FragmentManager fragmentManager = ((MainActivity) mContext).getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.cancel_button, DialogAreaFragment.newInstance(cityList));
//            fragmentTransaction.commit();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


//        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
//        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager layoutManager3 = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);



        //음식점
        recyclerView_rest = (RecyclerView) dlg.findViewById(R.id.recyclerView_rest);
        recyclerView_rest.setLayoutManager(layoutManager3);
        mAdapter_rest = new DialogRestListAdapter();
        recyclerView_rest.setAdapter(mAdapter_rest);
        while (recyclerView_rest.getItemDecorationCount() > 0) {
            recyclerView_rest.removeItemDecorationAt(0);
        }
        recyclerView_rest.measure(WindowManager.LayoutParams.MATCH_PARENT, 350);
        recyclerView_rest.addItemDecoration(new SpacesItemDecoration(1));


        ImageView btn_cancle = (ImageView) dlg.findViewById(R.id.btn_cancle);
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });

        Button btn_go_return = (Button) dlg.findViewById(R.id.btn_go_return);
        btn_go_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");

                dlg.dismiss();
            }
        });
    }
}