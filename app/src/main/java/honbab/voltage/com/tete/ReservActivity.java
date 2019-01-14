package honbab.voltage.com.tete;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.adapter.GridViewNearByAdapter;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.GetNearPlacesTaskForReserv;
import honbab.voltage.com.task.ReservFeedTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.CustomTimePickerDialog;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SnapHelper;
import okhttp3.OkHttpClient;

public class ReservActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    GestureDetector gestureDetector;

    public RecyclerView recyclerView;
    public GridViewNearByAdapter mAdapter;

    public SlidingUpPanelLayout layout_slidingPanel;
    public TextView txt_restName;
    private TextView txt_date, txt_clock;
    public EditText edit_comment;

    private Calendar calendar;

    public int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 700;
    int year, month, day, hour, min;// Calender 에서 얻는 값
//    int hour = 0;
//    int min = 0;// Task post로 넘길 값

    public RestData restData;
//    public String rest_name, place_id, lat, lng, rest_phone, rest_img, compound_code, vicinity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserv);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 이상
            checkLocationPermission();
        }

        layout_slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.layout_slidingPanel);
        layout_slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.e("abc", "onPanelSlide");
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                Log.e("abc", "onPanelStateChanged previousState = " + previousState + ", newState = " + newState);

                if (previousState.toString().equals("DRAGGING") && newState.toString().equals("COLLAPSED")) {
                    for (int i=0; i<mAdapter.getItemCount(); i++) {
//                        ImageView tmp = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)).findViewById(R.id.btn_one_reserv);
//                        Log.e("abc", "222 = " + recyclerView.findContainingItemView(recyclerView.getChildAt(i)));
                        if (recyclerView.getChildAt(i) != null) {
                            ImageView tmp = (ImageView) recyclerView.findContainingItemView(recyclerView.getChildAt(i)).findViewById(R.id.btn_one_reserv);
                            tmp.setBackgroundResource(R.drawable.icon_check_n);
                        }
                    }
                }
            }
        });
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "setFadeOnClickListener" + view.getContext());
                layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
            }
        });



        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            //누르고 뗄 때 한번만 인식하도록 하기위해서
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_rest);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new SnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        //layout_slidingPanel
        txt_restName = (TextView) findViewById(R.id.txt_restName);

        setDateTime();

        edit_comment = (EditText) findViewById(R.id.edit_comment);


        ImageView btn_go_map = (ImageView) findViewById(R.id.btn_go_map);
        btn_go_map.setOnClickListener(mOnClickListener);

        Button btn_reserv = (Button) findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getRestList();
    }

    private void setDateTime() {
        //날짜
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_clock = (TextView) findViewById(R.id.txt_clock);

        Date currentTime = new Date();
//        calendar = GregorianCalendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 2);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int ampm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        String str_date, str_time;
        if (ampm == 0) {
            str_time = "오전 ";
        } else {
            str_time = "오후 ";
        }

        if (hour > 12)
            str_time += String.valueOf(hour-12) + "시 ";
        else
            str_time += String.valueOf(hour) + "시 ";

        if (min < 30) {
            calendar.set(Calendar.MINUTE, 30);
            min = 30;
            str_time += "30분";
        } else {
            calendar.set(Calendar.MINUTE, 0);
            min = 0;
            str_time += "00분";
        }
        str_date = String.valueOf(month) + "/" + String.valueOf(day);

        txt_date.setText(str_date);
        txt_date.setOnClickListener(mOnClickListener);
        txt_clock.setText(str_time);
        txt_clock.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_date:
                    DatePickerDialog dialog = new DatePickerDialog(ReservActivity.this, dateSetListener, year, month-1, day);
                    dialog.show();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(ReservActivity.this, timeSetListener,
                            hour, min, false);
//                dialog.updateTime();
                    dialog2.show();

                    break;
                case R.id.btn_go_map:
                    Intent intent = new Intent(ReservActivity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("hour", hour);
                    intent.putExtra("min", min);
                    startActivity(intent);

                    break;
                case R.id.btn_reserv:
//                    String[] date = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
                    RestData rData = restData;
//                    String[] rest = {rest_name, compound_code, lat, lng, place_id, rest_img, rest_phone, vicinity};

                    Calendar curCal = Calendar.getInstance();
                    long time_setting = calendar.getTimeInMillis();
                    long time_current = curCal.getTimeInMillis();

                    if (time_setting > time_current) {
                        new ReservFeedTask(ReservActivity.this, httpClient, rData).execute("", "");
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (layout_slidingPanel != null) {
            if (layout_slidingPanel.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (layout_slidingPanel != null) {
                    if (layout_slidingPanel.getPanelState() != PanelState.HIDDEN) {
                        layout_slidingPanel.setPanelState(PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (layout_slidingPanel != null) {
                    if (layout_slidingPanel.getAnchorPoint() == 1.0f) {
                        layout_slidingPanel.setAnchorPoint(0.7f);
                        layout_slidingPanel.setPanelState(PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        layout_slidingPanel.setAnchorPoint(1.0f);
                        layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1 + 1;
            day = i2;

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            txt_date.setText(month + "/" + i2);
        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String str_time;

            hour = hourOfDay;
            min = minute;

            if (hourOfDay < 12) {
                calendar.set(Calendar.AM_PM, 0);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                str_time = "오전 " + hourOfDay + "시 " + minute + "분";
            } else {
                calendar.set(Calendar.AM_PM, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
                }

                str_time = "오후 " + hourOfDay + "시 " + minute + "분";
            }

            txt_clock.setText(str_time);
        }
    };

    public boolean checkLocationPermission() {
        Log.e("abc", "checkLocationPermission Reserv");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.e("abc", "퍼미션 있다.");
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                Log.e("abc", "퍼미션 없다.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }

            return false;
        } else
            return true;
    }

    private FusedLocationProviderClient mFusedLocationClient;
    double latitude, longitude;

    public void getRestList() {
//        mMap.setMyLocationEnabled(true);
//
//        if (mMap.isMyLocationEnabled()) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
//                                    Location myLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                            Log.e("abc", "myLocation = " + location);
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng latLng = new LatLng(latitude, longitude);




                            String search = "음식점";
//                            String url = getUrl(latitude, longitude, search);
                            Object dataTransfer[] = new Object[2];
                            dataTransfer[0] = ReservActivity.this;
                            dataTransfer[1] = GoogleMapUtil.getNearBySearch(ReservActivity.this, latitude, longitude, search, 0);
//                            dataTransfer[2] = this;
                            Log.e("abc", "dataTransfer[0] = " + dataTransfer[0]);
                            Log.e("abc", "dataTransfer[1] = " + dataTransfer[1]);

//                            GetNearPlacesTaskForReserv getNearPlacesTask = new GetNearPlacesTaskForReserv();
//                            getNearPlacesTask.execute(dataTransfer);
                            new GetNearPlacesTaskForReserv().execute(dataTransfer);
                        }
                    }
                });
//        }
    }


    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("abc", "퍼미션이도나요 짠짠짠");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}