package honbab.pumkit.com.tete;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import honbab.pumkit.com.fragment.FeedFragment;
import honbab.pumkit.com.widget.TabPagerAdapter;

public class MainActivity extends AppCompatActivity {

//    private TabLayout tabLayout;
    public TabPagerAdapter pagerAdapter;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.reservation)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.waitlist)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.profile)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 2) {
                    FeedFragment feedFragment = new FeedFragment();
//                    feedFragment.FeedListTask;
//                    new FeedFragment.FeedListTask.execute();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    public static void changeFragment(View view, Fragment fragment) {
//        FragmentActivity activity = (FragmentActivity) view.getContext();
////        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(activity.getSupportFragmentManager());
////
////        if (fragment == null) {
////            viewPagerAdapter.addFrag(new DateFragment(), "ONE");
////        } else {
////            viewPagerAdapter.addFrag(fragment, "ONE");
////        }
////        viewPagerAdapter.addFrag(new ChatFragment(), "TWO");
//
//        ft = activity.getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.fragment_main, fragment);
//        ft.commit();
//    }
}