package honbab.voltage.com.tete;

import android.app.ActivityManager;
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
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.MainFragment;
import honbab.voltage.com.fragment.NoProfileFragment;
import honbab.voltage.com.fragment.ProfileFragment;
import honbab.voltage.com.fragment.PublicEatFragment;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.VersionTask;
import honbab.voltage.com.utils.NetworkUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.MyService;
import honbab.voltage.com.widget.SessionManager;
import io.fabric.sdk.android.Fabric;

public class MainActivity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//    private OkHttpClient httpClient;
    private SessionManager session;

    private Toolbar toolbar;
    public DrawerLayout drawerLayout;

    private View headerLayout;
    private LinearLayout layout_my_box;
    private ImageView img_my_pic;
    public TextView txt_my_name, txt_go_login;
//    public TabPagerAdapter pagerAdapter;
//    public ViewPager viewPager;

//    public RestLikeFragment restLikeFragment = new RestLikeFragment();
//    public SelectFeedFragment selectFeedFragment = new SelectFeedFragment();
//    public MyFeedFragment myFeedFragment = new MyFeedFragment();
//    public NoProfileFragment noProfileFragment = new NoProfileFragment();

//    int tab_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main2);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        if (user.get("my_id") == null || user.get("my_id").equals(null))
            Statics.my_id = "-1";
        else
            Statics.my_id = user.get("my_id");
        Statics.my_username = user.get("my_username");
        Statics.my_gender = user.get("my_gender");
        Log.e("abc", "my_id = " + Statics.my_id);

        //Wifi check
//        if (!NetworkUtil.isNetworkPresent(this)) {
        if (!NetworkUtil.isOnline(this)) {
            //인터넷이 안 될 때
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connect_network);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mStartActivity = new Intent(MainActivity2.this, MainActivity2.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity2.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
            builder.show();
        } else {

            if (Statics.i_splash == 0) {
                Statics.i_splash++;

                Intent intent = new Intent(MainActivity2.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

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



                if (fragment == null) {
                    fragment = new MainFragment();
                }

                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
//                PublicEatFragment publicEatFragment = new PublicEatFragment();

                fm.findFragmentByTag("PUBLIC_EAT_FRAGMENT");
//                ft.add(R.id.fragment_main, publicEatFragment);
                ft.add(R.id.fragment_main, fragment);
                ft.addToBackStack(null);

                ft.commit();

                drawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
//            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
                drawerLayout.setDrawerListener(toggle);
                toggle.syncState();

                NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
                nav_view.setNavigationItemSelectedListener(this);

                NavigationView nav_view2 = (NavigationView) findViewById(R.id.nav_view2);
                nav_view2.setNavigationItemSelectedListener(this);

                ImageView btn_menu_date = (ImageView) findViewById(R.id.btn_menu_date);
                btn_menu_date.setOnClickListener(mOnClickListener);

                ImageView btn_chatlist = (ImageView) findViewById(R.id.btn_chatlist);
                btn_chatlist.setOnClickListener(mOnClickListener);

                headerLayout = nav_view.getHeaderView(0);
                layout_my_box = (LinearLayout) headerLayout.findViewById(R.id.layout_my_box);
                img_my_pic = (ImageView) headerLayout.findViewById(R.id.img_my_pic);
                txt_my_name = (TextView) headerLayout.findViewById(R.id.txt_my_name);
                txt_go_login = (TextView) headerLayout.findViewById(R.id.txt_go_login);
                ImageView img_pay_heart = headerLayout.findViewById(R.id.img_pay_heart);
                img_pay_heart.setColorFilter(R.color.orange);
                img_my_pic.setOnClickListener(mOnClickListener);
            }

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if (isMyServiceRunning(MyService.class)) return;
        Intent startIntent = new Intent(this, MyService.class);
        startIntent.setAction("start");
        startService(startIntent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (NetworkUtil.isOnline(this)) {
            new VersionTask(MainActivity2.this).execute();

            if (Integer.parseInt(Statics.my_id) > 0) {
//                header_main
                try {
                    UserData userData = new AccountTask(MainActivity2.this, 0).execute(Statics.my_id).get();

                    Picasso.get().load(userData.getImg_url())
                            .placeholder(R.drawable.icon_noprofile_circle)
                            .error(R.drawable.icon_noprofile_circle)
                            .transform(new CircleTransform())
                            .into(img_my_pic);
                    txt_my_name.setText(userData.getUser_name());
                    txt_go_login.setVisibility(View.GONE);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
//                layout_my_box.setVisibility(View.GONE);
//                txt_go_login.setVisibility(View.VISIBLE);
                txt_go_login.setOnClickListener(mOnClickListener);
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        try {
            Badges.setBadge(MainActivity2.this, 0);
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
                case R.id.btn_menu_date:
//                    drawerLayout.openDrawer(nav_menudate);
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    } else {
//                        drawerLayout.openDrawer(R.id.nav_view);
                        drawerLayout.openDrawer(GravityCompat.START);
                    }

                    break;
                case R.id.btn_chatlist:
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }

                    break;
                case R.id.img_my_pic:
                    Intent intent = new Intent(MainActivity2.this, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user_id", Statics.my_id);
                    startActivity(intent);

                    break;
                case R.id.txt_go_login:
                    Intent intent2 = new Intent(MainActivity2.this, LoginActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent2);

                    break;
            }
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    public Fragment fragment;
    private FragmentManager fm;
    public FragmentTransaction ft, ft2;

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        fragment = null;
        String title = getString(R.string.app_name);

        if (id == R.id.nav_main) {
            fragment = new MainFragment();
            title = "Main";
        } else if (id == R.id.nav_publiceat) {
            fragment = new PublicEatFragment();
            title = "PublicEat";
        } else if (id == R.id.nav_match) {
            fragment = new SelectFeedFragment();
            title = "Match";
        } else if (id == R.id.nav_profile) {
            if (Integer.parseInt(Statics.my_id) < 0)
                fragment = new NoProfileFragment();
            else
                fragment = new ProfileFragment();
            title = "Profile";
        }
// else if (id == R.id.nav_announcement) {
//            fragment = new AnnounceFragment();
//            title = "Announce";
//        }
        else if (id == R.id.nav_setting) {
//            Toast.makeText(this, "세팅", Toast.LENGTH_SHORT).show();
//            fragment = new SettingFragment();
//            title = "Setting";
            if (Statics.my_id == null || Statics.my_id == "" || Integer.parseInt(Statics.my_id) < 1) {
                Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
        else {
            fragment = new MainFragment();
            title = "Main";
        }


        Log.e("abc", "xxxxxxxxx fragment = " + fragment);
        Log.e("abc", "xxxxxxxxx getFragments = " + fm.getFragments());

        if (fragment != null) {
//            DateFragment.stopAnimate();
//
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main, fragment, title);
            ft.addToBackStack(null);
            ft.commit();
        } else {
            fragment = new MainFragment();

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_main, fragment, title);
            ft.addToBackStack(null);
            ft.commit();
        }



        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.layout_drawer);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}