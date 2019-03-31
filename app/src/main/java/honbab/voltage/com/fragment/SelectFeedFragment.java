package honbab.voltage.com.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import honbab.voltage.com.adapter.SelectDateListAdapter;
import honbab.voltage.com.adapter.SelectRestListAdapter;
import honbab.voltage.com.adapter.SelectUserListAdapter;
import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.task.AreaRestTask;
import honbab.voltage.com.task.ReservFeedTask;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.JoinActivity;
import honbab.voltage.com.tete.JoinActivity2;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SpacesItemDecoration;
import okhttp3.OkHttpClient;

public class SelectFeedFragment extends Fragment implements LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener {
    private OkHttpClient httpClient;

    public TextView txt_date;
    public RecyclerView recyclerView_date, recyclerView_rest, recyclerView_user;
    public SelectDateListAdapter mAdapter_date;
    public SelectRestListAdapter mAdapter_rest;
    public SelectUserListAdapter mAdapter_user;
    public SwipeRefreshLayout swipeContainer;
    public Spinner spinner;
    public SpinnerAdapter spinnerAdapter;
    public TextView txt_explain_pick;
    public TextView txt_explain_time, txt_explain_rest, txt_explain_reserv;
    public SlidingUpPanelLayout layout_slidingPanel;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;

    public int split = 2;
    public String area_cd = "GNS1";
    public String feed_time = "";
    public String feed_rest_id = "";
    public String to_id = "", to_name = "";
    public ArrayList<AreaData> areaList;
    public ArrayList<String> areaNameList;
    private double latitude, longitude;

    public ArrayList<SelectDateData> dateLikeList = new ArrayList<>();
    public ArrayList<String> restLikeList = new ArrayList<>();

    public static SelectFeedFragment newInstance(int val) {
        SelectFeedFragment fragment = new SelectFeedFragment();

        Bundle args = new Bundle();
        args.putInt("val", val);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selectfeed, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        areaList = new ArrayList<>();
        areaNameList = new ArrayList<>();
        AreaData areaData = new AreaData("GNS1", "강남역");
        areaList.add(areaData);
        areaNameList.add("강남역");

        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();

//        Log.e("abc", "longitude = " + longitude);
//        if (longitude == 0) {
//            new AreaRestTask(getActivity()).execute();
////            new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
//        }

//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
////                    LatLng latLng = new LatLng(latitude, longitude);
////                    Log.e("abc", "latitude = " + latitude + ", " + longitude);
//
//                    if (latitude > 37.517117) {
//                        area_cd = "SDH1";
//                        spinner.setSelection(1);
//
//                        new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
//                    } else {
//                        area_cd = "GNS1";
//                        spinner.setSelection(0);
//                    }
//
//                    Log.e("abc", "area_cd = " + area_cd + " , latitude = " + latitude + ", " + longitude);
//
//                }
//            }
//        });
    }

    private void initControls() {
        spinner = (Spinner) getActivity().findViewById(R.id.spinner_location);
        spinnerAdapter = new ArrayAdapter(getActivity(), R.layout.item_row_spinner, areaNameList);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e("abc", "parent.getCount() = " + parent.getCount());

                if (parent.getCount() == 1) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
//                    LatLng latLng = new LatLng(latitude, longitude);
                                Log.e("abc", "latitude = " + latitude + ", " + longitude);

                                LatLng llGangNam = new LatLng(37.4979462, 127.025427);//강남역
                                LatLng llEulJiRo = new LatLng(37.5660602, 126.980468);//을지로입구역
                                LatLng llSinChon = new LatLng(37.5597212, 126.9403285);//신촌역
                                LatLng llSeoulUni = new LatLng(37.4812142, 126.950518);//서울대입구역
                                LatLng llNoRyang = new LatLng(37.513567, 126.9393334);//노량진역

                                LatLng llJamSil = new LatLng(37.5153934, 127.10273);//잠실역
                                LatLng llGunDae = new LatLng(37.5392502, 127.0691476);//건대입구역
                                LatLng llHaeHwa = new LatLng(37.5820842, 126.9997033);//혜화역
                                LatLng llSeoMyeon = new LatLng(35.1568282, 129.057955);//부산 서면역
                                LatLng llHaeUnDae = new LatLng(35.1647738, 129.1379978);//부산 해운대역

                                LatLng[] stationLLGroup = new LatLng[]{llGangNam, llEulJiRo, llSinChon, llSeoulUni, llNoRyang};
                                //, llJamSil, llGunDae, llHaeHwa,
                                // llSeoMyeon, llHaeUnDae

                                int i = 0;
                                Location target = new Location("target");
                                for (LatLng point : stationLLGroup) {
                                    target.setLatitude(point.latitude);
                                    target.setLongitude(point.longitude);
                                    if (location.distanceTo(target) < 1700) {
                                        // bingo!
                                        Log.e("abc", "bingo" + point.latitude + point.longitude);

                                        if (i == 0)
                                            area_cd = "GNS1";
                                        else if (i == 1)
                                            area_cd = "JGS1";
                                        else if (i == 2)
                                            area_cd = "SDH1";
                                        else if (i == 3)
                                            area_cd = "GAS1";
                                        else if (i == 4)
                                            area_cd = "DJS1";
                                        else
                                            area_cd = "GNS1";
                                    }

                                    i++;
                                }

//                                if (latitude > 37.511083) {
//                                    area_cd = "SDH1";
////                                    spinner.setSelection(1);
//
////                                    new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
//                                } else {
//                                    area_cd = "GNS1";
////                                    spinner.setSelection(0);
//                                }

                                new AreaRestTask(getActivity()).execute();
//                                new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("abc", "addOnFailureListener = " + e);
                            new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
                        }
                    });
                } else {
                    int r = 0;

                    for (int i = 0; i < areaList.size(); i++) {
                        if (areaList.get(i).getArea_name().equals(spinner.getSelectedItem().toString()))
                            r = i;
                    }

                    area_cd = areaList.get(r).getArea_cd();
//                    Log.e("abc", "onItemSelected area_cd = " + area_cd);
//                    Log.e("abc", "spinner.getSelectedItem() = " + spinner.getSelectedItem());

                    new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                area_cd = areaList.get(0).getArea_cd();
                Log.e("abc", "onNothingSelected area_cd = " + area_cd);


                return;
            }
        });


//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        Criteria crta = new Criteria();
//        crta.setAccuracy(Criteria.ACCURACY_FINE);
//        crta.setAltitudeRequired(true);
//        crta.setBearingRequired(true);
//        crta.setCostAllowed(true);
//        crta.setPowerRequirement(Criteria.POWER_LOW);
//        String provider = locationManager.getBestProvider(crta, true);
//        Log.d("", "provider : " + provider);
//        // String provider = LocationManager.GPS_PROVIDER;
//        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
//        Location location = locationManager.getLastKnownLocation(provider);
//        Log.e("abc", "location = " + location);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        txt_date = (TextView) getActivity().findViewById(R.id.txt_date);

        recyclerView_date = (RecyclerView) getActivity().findViewById(R.id.recyclerView_date);
        recyclerView_date.setLayoutManager(layoutManager);
        mAdapter_date = new SelectDateListAdapter();
        recyclerView_date.setAdapter(mAdapter_date);
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
        while (recyclerView_date.getItemDecorationCount() > 0) {
            recyclerView_date.removeItemDecorationAt(0);
        }
        recyclerView_date.addItemDecoration(new SpacesItemDecoration(18));
//        recyclerView_date.addItemDecoration(itemDecoration);

        recyclerView_rest = (RecyclerView) getActivity().findViewById(R.id.recyclerView_rest);
        recyclerView_rest.setLayoutManager(layoutManager2);
        mAdapter_rest = new SelectRestListAdapter(getActivity());
        recyclerView_rest.setAdapter(mAdapter_rest);
        while (recyclerView_rest.getItemDecorationCount() > 0) {
            recyclerView_rest.removeItemDecorationAt(0);
        }
        recyclerView_rest.addItemDecoration(new SpacesItemDecoration(18));

        recyclerView_user = (RecyclerView) getActivity().findViewById(R.id.recyclerView_user);
        recyclerView_user.setLayoutManager(gridLayoutManager);
        mAdapter_user = new SelectUserListAdapter();
        recyclerView_user.setAdapter(mAdapter_user);
        while (recyclerView_user.getItemDecorationCount() > 0) {
            recyclerView_user.removeItemDecorationAt(0);
        }
        recyclerView_user.addItemDecoration(new SpacesItemDecoration(26));

        txt_explain_pick = (TextView) getActivity().findViewById(R.id.txt_explain_pick);

        //sliding Up Panel
        txt_explain_time = (TextView) getActivity().findViewById(R.id.txt_explain_time);
        txt_explain_rest = (TextView) getActivity().findViewById(R.id.txt_explain_rest);
        txt_explain_reserv = (TextView) getActivity().findViewById(R.id.txt_explain_reserv);

        layout_slidingPanel = (SlidingUpPanelLayout) getActivity().findViewById(R.id.layout_slidingPanel);
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_feed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter_date.clearItemList();
                mAdapter_rest.clearItemList();
                mAdapter_user.clearItemList();

//                String str_date = year + String.valueOf(month) + "/" + String.valueOf(day);
                new SelectFeedListTask(getActivity()).execute(feed_time, area_cd, feed_rest_id, "");
            }
        });

        ImageButton btn_reserv = (ImageButton) getActivity().findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

        //현재 위치 찾기
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

//        mMap.setMyLocationEnabled(true);
//        mMap.setOnMyLocationChangeListener(this);
    }

    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reserv:

                    if (Statics.my_id == null || Statics.my_username == null || Statics.my_username.equals("null") || Integer.parseInt(Statics.my_id) < 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("로그인을 해야 같이먹기가 가능합니다.");
                        builder.setPositiveButton(R.string.go_to_login,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((MainActivity) getActivity()).viewPager.setCurrentItem(1);
//                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                            getActivity().finish();
                                    }
                                });
                        builder.setNegativeButton(R.string.join,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity(), JoinActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
//                                        getActivity().finish();
                                    }
                                });
//                        AlertDialog alert = builder.create();
                        builder.show();
//                        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
//                        nbutton.setBackgroundResource(R.color.black);
                    }
                    else if (feed_time.equals("")) {
                        Toast.makeText(getActivity(), "가능한 식사시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (feed_rest_id.equals("")) {
                        Toast.makeText(getActivity(), "음식점을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (to_id.equals("")) {
                        Toast.makeText(getActivity(), "같이 식사하실 분을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
//                        if (Statics.my_id == null || Statics.my_username == null || Statics.my_username.equals("null") || Integer.parseInt(Statics.my_id) < 1) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setMessage("로그인을 하셔야 같이먹기가 가능합니다.");
//                            builder.setPositiveButton(R.string.go_to_login,
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            ((MainActivity) getActivity()).viewPager.setCurrentItem(1);
////                                            Intent intent = new Intent(getActivity(), LoginActivity.class);
////                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                            startActivity(intent);
////                                            getActivity().finish();
//                                        }
//                                    });
//                            builder.show();
//                        } else
                            if (FirebaseAuth.getInstance().getCurrentUser() == null || Statics.my_gender == null) {
                            Intent intent = new Intent(getActivity(), JoinActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            new ReservFeedTask(getActivity()).execute(to_id, feed_rest_id, feed_time);
                        }
                    }

                    break;
//                case R.id.txt_date:
//                    DatePickerDialog dialog = new DatePickerDialog(getActivity(), dateSetListener,
//                            year, month - 1, day);
//                    dialog.show();
//
//                    break;
            }
        }
    };

//    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
//        @Override
//        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//            year = i;
//            month = i1 + 1;
//            day = i2;
//
//            calendar.set(Calendar.YEAR, year);
//            calendar.set(Calendar.MONTH, i1);
//            calendar.set(Calendar.DAY_OF_MONTH, day);
//
//            txt_date.setText(month + "/" + day);
//
//            //현재시간보다 이후인지 체크
//            Calendar curCal = Calendar.getInstance();
//            long time_setting = calendar.getTimeInMillis();
//            long time_current = curCal.getTimeInMillis();
//
//            if (time_setting <= time_current) {
//                Toast.makeText(getActivity(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //onMyLocationChange
    @Override
    public void onMyLocationChange(Location location) {
        LatLng POINTA = new LatLng(37.4979462, 127.025427);//강남역
        LatLng POINTB = new LatLng(37.5660602, 126.980468);//을지로입구역
        LatLng POINTC = new LatLng(37.5597212, 126.9403285);//신촌역
        LatLng POINTD = new LatLng(37.4812142, 126.950518);//서울대입구역

        Location target = new Location("target");
        for (LatLng point : new LatLng[]{POINTA, POINTB, POINTC, POINTD}) {
            target.setLatitude(point.latitude);
            target.setLongitude(point.longitude);
            if (location.distanceTo(target) < 5000) {
                // bingo!
                Log.e("abc", "bingo" + point.latitude + point.longitude);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        setUpMap();
    }
}