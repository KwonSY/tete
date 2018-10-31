package honbab.pumkit.com.tete;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import honbab.pumkit.com.adapter.GridViewNearByAdapter;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.CustomTimePickerDialog;
import honbab.pumkit.com.widget.GetNearPlacesTaskForReserv;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SnapHelper;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ReservActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    GestureDetector gestureDetector;

    public SlidingUpPanelLayout layout_slidingPanel;
    public static RecyclerView recyclerView;
    public static GridViewNearByAdapter mAdapter;
    private TextView textClock;
    public TextView txt_restName;

    public int REQUEST_LOCATION_CODE = 99;
    int PROXIMITY_RADIUS = 700;
    int hourOfDay, minute;//Calender 에서 얻는 값
    int hour = 0, min = 0;//Task post로 넘길 값

    public static String rest_name, place_id, rest_img, lat, lng;
    //vvvvv GetNearPlacesTaskForReserv.mMapList -> ReservActivity.mMapList
//    public static ArrayList<MapData> mMapList = new ArrayList<>();

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
                Log.e("abc", "onPanelStateChanged");
            }
        });
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "setFadeOnClickListener");
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

        txt_restName = (TextView) findViewById(R.id.txt_restName);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_rest);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new SnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        //vvvvvvvvvvvv 예약버튼 분리
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                //손으로 터치한 곳의 좌표를 토대로 해당 Item의 View를 가져옴
//                View childView = rv.findChildViewUnder(e.getX(), e.getY());
//
//                //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
//                //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
//                //한번만 눌려서 그 값이 true가 넘어왔다면
//                if (childView != null && gestureDetector.onTouchEvent(e)) {
//                    //현재 터치된 곳의 position을 가져오고
//                    int currentPosition = rv.getChildAdapterPosition(childView);
//
//                    //해당 위치의 Data를 가져옴
//                    restName = GetNearPlacesTaskForReserv.mMapList.get(currentPosition).getRest_name();
//                    Double l1 = GetNearPlacesTaskForReserv.mMapList.get(currentPosition).getLatLng().latitude;
//                    Double l2 = GetNearPlacesTaskForReserv.mMapList.get(currentPosition).getLatLng().longitude;
//                    String coordl1 = l1.toString();
//                    String coordl2 = l2.toString();
//                    lat = coordl1;
//                    lng = coordl2;
//                    img_url = GetNearPlacesTaskForReserv.mMapList.get(currentPosition).getRest_img().toString();
////                    Toast.makeText(MainActivity.this, "현재 터치한 Item의 Student Name은 " + currentItemStudent.getStudentName(), Toast.LENGTH_SHORT).show();
//
//                    txt_restName.setText(restName);
//
//                    layout_slidingPanel.setPanelState(PanelState.ANCHORED);
////                    layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
//
//                    //vvvvvvvvvvvvv photo 여러개 찾아보기 test
//                    String reference = GetNearPlacesTaskForReserv.mMapList.get(currentPosition).getReference();
//
//
//                    return true;
//                }
//
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });

        Date currentTime = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(currentTime);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        Log.e("abc", "현재시간 = " + hourOfDay + minute);

        textClock = (TextView) findViewById(R.id.txt_clock);
        String timeString;
        if (hourOfDay < 12) {
            hour = 12;
            min = 0;
            timeString = "오후 " + 12 + "시 " + "00분";
        } else {
            if (hourOfDay < 18) {
                hour = 19;
                min = 0;
                timeString = "오후 " + 7 + "시 " + "00분";
            } else {
                hour = hourOfDay + 1 - 12;
                timeString = "오후 " + hour + "시 ";

                if (minute < 30) {
                    min = 30;
                    timeString += "30분";
                } else {
                    min = 0;
                    timeString += "00분";
                }
            }
        }
        textClock.setText(timeString);
        textClock.setOnClickListener(mOnClickListener);

        ImageView btn_go_map = (ImageView) findViewById(R.id.btn_go_map);
        btn_go_map.setOnClickListener(mOnClickListener);

        Button btn_reserv = (Button) findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

//        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(mOnClickListener);
        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new GetNearRestTask().execute();
        getRestList();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    Log.e("abc", "백버튼 클릭");
                    onBackPressed();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog = new CustomTimePickerDialog(ReservActivity.this, timeSetListener, hourOfDay, minute, false);
//                dialog.updateTime();
                    dialog.show();

                    break;
                case R.id.btn_go_map:
                    Intent intent = new Intent(ReservActivity.this, MapsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_reserv:
                    new ReservTask().execute();

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

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
//            Log.e("abc", "hourOfDay = " + hourOfDay);
            String timeString;

            hour = hourOfDay;
            min = minute;

            if (hourOfDay < 12) {
                timeString = "오전 " + hourOfDay + "시 " + minute + "분";
            } else {
                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
//                    hour = hourOfDay - 12;
                }

                timeString = "오후 " + hourOfDay + "시 " + minute + "분";
            }
            textClock.setText(timeString);
        }
    };

    public boolean checkLocationPermission() {
        Log.e("abc", "checkLocationPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
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
                            String url = getUrl(latitude, longitude, search);
                            Object dataTransfer[] = new Object[2];
                            dataTransfer[0] = ReservActivity.this;
                            dataTransfer[1] = url;
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

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("language=ko");
        googlePlaceUrl.append("&location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&keyword=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key=" + getString(R.string.google_maps_api_key));

        return googlePlaceUrl.toString();
    }

    public class ReservTask extends AsyncTask<Void, Void, Void> {
        String result;
        String time_reserv;

        @Override
        protected void onPreExecute() {
            String str_hour = String.valueOf(hour);
            String str_min = String.valueOf(min);
            // vvvvvvvvvvv 날짜를 추가해야한다
            time_reserv = str_hour + ":" + str_min;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("abc", "ppxxxxxxxxp = " + place_id);
            FormBody body = new FormBody.Builder()
                    .add("opt", "reservation")
                    .add("my_id", Statics.my_id)
                    .add("rest_name", rest_name)
                    .add("place_id", place_id)
                    .add("lat", lat)
                    .add("lng", lng)
                    .add("img_url", rest_img)
                    .add("time_reserv", time_reserv)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "예약 : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
                } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {
                Intent intent2 = new Intent(ReservActivity.this, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            }
        }
    }
}