package honbab.voltage.com.tete;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import honbab.voltage.com.adapter.FeedMapHorzRestAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.task.GetNearPlacesTaskForMap;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.CustomTimePickerDialog;
import honbab.voltage.com.widget.SnapHelper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker currentLocationmMarker;
    public static final int REQUEST_LOCATION_CODE = 99;
//    int PROXIMITY_RADIUS = 700;
    double latitude, longitude;

    int hour, min;
    int i = 0;
    int j = 0;

    private FusedLocationProviderClient mFusedLocationClient;
//    public SlidingUpPanelLayout layout_slidingPanel;
    public TextView txt_restName, txt_clock;
    public static RecyclerView recyclerView;
    public static FeedMapHorzRestAdapter feedMapHorzRestAdapter;

    public static ArrayList<String> mNames = new ArrayList<>();
    public static ArrayList<FeedData> mMapList = new ArrayList<>();
    public static ArrayList<Marker> mMarkersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservmap);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 이상
            checkLocationPermission();
        }

        Intent intent = getIntent();
        hour = intent.getIntExtra("hour", 0);
        min = intent.getIntExtra("min", 0);

//        layout_slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.layout_slidingPanel);
//        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
//            }
//        });
        txt_restName = (TextView) findViewById(R.id.txt_restName);
        txt_clock = (TextView) findViewById(R.id.txt_clock);
        txt_clock.setOnClickListener(mOnClickListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Parcelable mCurrentLocation = savedInstanceState.getParcelable("location");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_rest);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new SnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener() {

            //누르고 뗄 때 한번만 인식하도록 하기위해서
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                //손으로 터치한 곳의 좌표를 토대로 해당 Item의 View를 가져옴
//                View childView = rv.findChildViewUnder(e.getX(),e.getY());
//
//                //터치한 곳의 View가 RecyclerView 안의 아이템이고 그 아이템의 View가 null이 아니라
//                //정확한 Item의 View를 가져왔고, gestureDetector에서 한번만 누르면 true를 넘기게 구현했으니
//                //한번만 눌려서 그 값이 true가 넘어왔다면
//                if(childView != null && gestureDetector.onTouchEvent(e)){
//                    int currentPosition = rv.getChildAdapterPosition(childView);
//
//                    layout_slidingPanel.setPanelState(PanelState.ANCHORED);
//
//                    return true;
//                }
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
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    //vvvvvvvvvvvvvv 레스토랑 더 로딩하기
//                    Toast.makeText(MapsActivity.this, "Last Position", Toast.LENGTH_SHORT).show();
                }

                if (lastVisibleItemPosition >= 0) {
                    Marker marker = mMarkersList.get(lastVisibleItemPosition);
                    marker.showInfoWindow();

//                    mMap.animateCamera(CameraUpdateFactory.newLatLng(GetNearPlacesTaskForMap.mMapRestList.get(lastVisibleItemPosition).getLatLng()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkersList.get(lastVisibleItemPosition).getPosition()));
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });

        ButtonUtil.setBackButtonClickListener(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txt_clock:
//                    TimePickerDialog.OnTimeSetListener timeSetListener = ((ReservActivity) MapsActivity.this.getApplicationContext()).timeSetListener;
                    CustomTimePickerDialog dialog = new CustomTimePickerDialog(MapsActivity.this, timeSetListener, hour, min, false);
                    dialog.show();

                    break;
            }
        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
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
            txt_clock.setText(timeString);
        }
    };



    boolean mLocationPermissionGranted;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("abc", "onRequestPermissionsResult" + requestCode);
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                        mLocationPermissionGranted = true;
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("abc", "onMapReady i = " + i);

        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.e("abc", "1 mapReady checkSelfPermission = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
//            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(true);

            i++;
        }
//        else {
//            Log.e("abc", "2 mapReady checkSelfPermission = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
//            mMap.setOnMyLocationButtonClickListener(this);
//            mMap.setOnMyLocationClickListener(this);
//            enableMyLocation();
//        }

        // edited by KSY 2018-07-30
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(9);

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.getId();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                String markerId = marker.getId();
//                LatLng markerPosition = marker.getPosition();
//                String markerSnippet = marker.getSnippet();
//                float markerZIndex = marker.getZIndex();

                //마커 -> recycler 적용
                int position = mNames.indexOf(marker.getTitle());
                recyclerView.smoothScrollToPosition(position);

                return false;
            }
        });




        //처음에 마커를 GetNearPlacesTaskForReserv.mMapList에서 가져다서 쓴다.
        LatLng first_latLng = null;

//        for (int i=0; i<GetNearPlacesTaskForReserv.mMapList.size(); i++) {
        for (int i=0; i<mMapList.size(); i++) {
            Log.e("abc", "mMarkersList 만드는 중  = " + i);
            LatLng latLng = mMapList.get(i).getLatLng();
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            markerOptions.title(mMapList.get(i).getRest_name());
//            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bmp));
//            markerOptions.anchor(0.5f, 1);
//            markerOptions.snippet(String.valueOf(i+1));

            Marker marker = mMap.addMarker(markerOptions);

            mMarkersList.add(marker);

            if (i==0) {
                first_latLng = mMapList.get(i).getLatLng();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(first_latLng));
                marker.showInfoWindow();
            }

            mNames.add(mMapList.get(i).getRest_name());
        }

        feedMapHorzRestAdapter = new FeedMapHorzRestAdapter(MapsActivity.this, mMapList);
        recyclerView.setAdapter(feedMapHorzRestAdapter);
        feedMapHorzRestAdapter.notifyDataSetChanged();

        Toast.makeText(MapsActivity.this, "주변 음식점 검색", Toast.LENGTH_SHORT).show();
    }







    private void enableMyLocation() {
        Log.e("abc", "enableMyLocation FINE_LOCATION = " + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) + "     , enableMyLocation PERMISSION_GRANTED = " + PackageManager.PERMISSION_GRANTED);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            Log.e("abc", "enableMyLocation PermissionUtils");
            // v= 퍼미션이 없으면 수락을 받아내라
//            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE, Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // 이미 설치되어 퍼미션 완료한 상태
            Log.e("abc", "enableMyLocation mMap.isMyLocationEnabled() = " + mMap.isMyLocationEnabled());
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

            if (mMap.isMyLocationEnabled()) {

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

//                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                                }
                            }
                        });

            }
        }
    }





    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastlocation = location;
        if (currentLocationmMarker != null) {
            currentLocationmMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationmMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

        j++;

        //vvvvvvvvvvvvvvvvvvv
        Log.e("abc",  "location = " + j + ", " + location.getAltitude());
        recyclerView.scrollToPosition(3);
    }

    public void onClick(View v) {
        Object dataTransfer[] = new Object[3];

        switch (v.getId()) {
            case R.id.btn_search_word:
                EditText tf_location = findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;
                Log.e("abc", "현 위치 찾기1 = " + location);
                Log.e("abc", "Zoom레벨 = " + mMap.getCameraPosition().zoom);

                if (!location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);

                    try {
                        addressList = geocoder.getFromLocationName(location, 5);

                        if (addressList != null) {
                            for (int i = 0; i < addressList.size(); i++) {
                                LatLng latLng = new LatLng(addressList.get(i).getLatitude(), addressList.get(i).getLongitude());
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(latLng);
                                markerOptions.title(location);
                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btn_search_near:
                mMap.clear();
                mMapList.clear();
                mNames.clear();

                LatLng camearaLatLng = mMap.getCameraPosition().target;
                latitude = camearaLatLng.latitude;
                longitude = camearaLatLng.longitude;
                String search = "음식점";
                float zoomLevel = mMap.getCameraPosition().zoom;
                Log.e("abc", "restuarant lat,lon = (" + latitude + "," + longitude + ") , " + search);

//                String url = getUrl(latitude, longitude, search);
                dataTransfer[0] = mMap;
                dataTransfer[1] = GoogleMapUtil.getNearBySearch(this, latitude, longitude, search, zoomLevel);
                dataTransfer[2] = this;

                new GetNearPlacesTaskForMap().execute(dataTransfer);

                Toast.makeText(MapsActivity.this, R.string.search_near_rest, Toast.LENGTH_SHORT).show();

                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("abc", "onConnected = " + bundle);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }


    public boolean checkLocationPermission() {
        Log.e("abc", "checkLocationPermission Map");
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


    @Override
    public void onConnectionSuspended(int i) {
        Log.e("abc", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("abc", "onConnectionFailed");
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();

        return false;
    }


}