package honbab.pumkit.com.tete;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import honbab.pumkit.com.widget.SessionManager;
import honbab.pumkit.com.adapter.TabPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;

//    private TabLayout tabLayout;
    public TabPagerAdapter pagerAdapter;
    public ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Statics.my_id = user.get("my_id");
        Statics.my_gender = user.get("mygender");

        if (Statics.i_splash == 0) {
            Statics.i_splash++;

            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        try {
            PackageInfo info = getPackageManager().getPackageInfo("honbab.pumkit.com.tete", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("abc", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }




        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.reservation)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.waitlist)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.my)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

//                if (tab.getPosition() == 2) {
//                    FeedFragment feedFragment = new FeedFragment();
////                    feedFragment.FeedListTask;
////                    new FeedFragment.FeedListTask.execute();
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        viewPager.setCurrentItem(position);
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