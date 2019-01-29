package honbab.voltage.com.tete;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.auth.FirebaseAuth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import honbab.voltage.com.adapter.TabPagerAdapter;
import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.fragment.NoProfileFragment;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.VersionTask;
import honbab.voltage.com.utils.NetworkUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    public TabPagerAdapter pagerAdapter;
    public ViewPager viewPager;

//    public RestLikeFragment restLikeFragment = new RestLikeFragment();
//    public SelectFeedFragment selectFeedFragment = new SelectFeedFragment();
//    public MyFeedFragment myFeedFragment = new MyFeedFragment();
//    public NoProfileFragment noProfileFragment = new NoProfileFragment();

    int tab_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        Statics.my_id = user.get("my_id");
        Statics.my_username = user.get("my_username");
        Statics.my_gender = user.get("my_gender");

        //Wifi check
//        if (!NetworkUtil.isNetworkPresent(this)) {
        if (!NetworkUtil.isOnline(this)) {
            //인터넷이 안 될 때
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connect_network);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mStartActivity = new Intent(MainActivity.this, MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
            builder.show();
        } else {

            if (Statics.i_splash == 0) {
                Statics.i_splash++;

                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

//                FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
//                Log.e("abc", "fireUser = " + fireUser.getUid());
                if (Statics.my_id == null || Statics.my_username == null || Statics.my_username.equals("null")) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else if (FirebaseAuth.getInstance().getCurrentUser() == null || Statics.my_gender == null) {
                    Intent intent = new Intent(MainActivity.this, JoinActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Create channel to show notifications.
                    String channelId  = getString(R.string.default_notification_channel_id);
                    String channelName = getString(R.string.default_notification_channel_name);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                            channelName, NotificationManager.IMPORTANCE_LOW));
                }


                try {
                    PackageInfo info = getPackageManager().getPackageInfo("honbab.voltage.com.tete", PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);

                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_1_like_rest)));
                tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_2_my)));
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                List<Fragment> fragmentList = new ArrayList<>();
                fragmentList.add(new SelectFeedFragment());
                if (Statics.my_id == null) {
                    fragmentList.add(new NoProfileFragment());
                } else {
                    fragmentList.add(new MyFeedFragment());
                }

                viewPager = (ViewPager) findViewById(R.id.viewPager);
                pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), MainActivity.this, fragmentList, tabLayout.getTabCount());
                viewPager.setAdapter(pagerAdapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
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
//                pagerAdapter.setPrimaryItem(pagerAdapter.getC);
//                pagerAdapter.getFragmentTag(0x7f0901fe, 0);

                ImageView btn_go_tinder = (ImageView) findViewById(R.id.btn_go_tinder);
                btn_go_tinder.setOnClickListener(mOnClickListener);

                Intent intent = getIntent();
                tab_position = intent.getIntExtra("position", 0);
            }

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkUtil.isOnline(this)) {
            new VersionTask(MainActivity.this, httpClient).execute();
        }

        if (tab_position == 1)
            viewPager.setCurrentItem(tab_position);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        try {
            Badges.setBadge(this, 0);
        } catch (BadgesNotSupportedException e) {
            e.printStackTrace();
        }
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_tinder:
//                    Intent intent = new Intent(MainActivity.this, GodTinderActivity.class);
                    Intent intent = new Intent(MainActivity.this, DelayBefroePickRestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    break;
            }
        }
    };
}