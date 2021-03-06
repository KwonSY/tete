package honbab.voltage.com.tete;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import honbab.voltage.com.widget.SnapHelper;

public class FeedMapActivity extends FragmentActivity implements OnMapReadyCallback,
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
    int PROXIMITY_RADIUS = 700;
    double latitude, longitude;

    int i = 0;
    int j = 0;
    ArrayList<FeedData> feedList = new ArrayList<>();
    ArrayList<String> mNames = new ArrayList<>();
    ArrayList<Marker> mMarkersList = new ArrayList<>();

    public RecyclerView recyclerView;
    public FeedMapHorzRestAdapter feedMapHorzRestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 이상
            checkLocationPermission();
        }

        Intent intent = getIntent();
        feedList = (ArrayList<FeedData>) intent.getSerializableExtra("feedList");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setVisibility(View.GONE);

        Button btn_search_word = (Button) findViewById(R.id.btn_search_word);
        btn_search_word.setVisibility(View.GONE);

        Button btn_search_near = (Button) findViewById(R.id.btn_search_near);
        btn_search_near.setVisibility(View.GONE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_rest);
        recyclerView.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new SnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        final GestureDetector gestureDetector;
        gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {

            //누르고 뗄 때 한번만 인식하도록 하기위해서
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
//        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View childView = rv.findChildViewUnder(e.getX(),e.getY());
//
//                if(childView != null && gestureDetector.onTouchEvent(e)){
//                    int currentPosition = rv.getChildAdapterPosition(childView);
//                    Log.e("abc", "currentPosition = " + currentPosition);
//
//                    Intent intent = new Intent(FeedMapActivity.this, OneRestaurantActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("feed_id", feedList.get(currentPosition).getSid());
//                    startActivity(intent);
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
                    Toast.makeText(FeedMapActivity.this, "Last Position", Toast.LENGTH_SHORT).show();
                }

                if (lastVisibleItemPosition >= 0) {
                    Marker marker = mMarkersList.get(lastVisibleItemPosition);
                    marker.showInfoWindow();

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(mMarkersList.get(lastVisibleItemPosition).getPosition()));
                }
            }
        });

        ButtonUtil.setBackButtonClickListener(this);
    }


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

        // edited by KSY 2018-07-30
        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(9);

        LatLng first_latLng = null;
        for (int i = 0; i < feedList.size(); i++) {
            FeedData feedData = feedList.get(i);

            LatLng latLng = feedData.getLatLng();
            double latitude = feedData.getLatitude();
            double longitude = feedData.getLongtitue();

            if (i == 0) {
                first_latLng = new LatLng(latitude, longitude);
            }

            LatLng mark_latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(mark_latLng)).setTitle(feedData.getRest_name().toString());

            //마커 추가
            LatLng marker_latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions().position(marker_latLng);
//            markerOptions.snippet(String.valueOf(i + 1));
            markerOptions.title(feedData.getRest_name());

            Marker marker = mMap.addMarker(markerOptions);
            mMarkersList.add(marker);

            mNames.add(feedData.getRest_name());
        }
        feedMapHorzRestAdapter = new FeedMapHorzRestAdapter(FeedMapActivity.this, feedList);
        recyclerView.setAdapter(feedMapHorzRestAdapter);
        feedMapHorzRestAdapter.notifyDataSetChanged();

        if (feedList.size() > 0) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(first_latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            enableMyLocation();
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.getId();
            }
        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerId = marker.getId();
                //마커 -> recycler 적용
                int position = mNames.indexOf(marker.getTitle());
                recyclerView.smoothScrollToPosition(position);

                return false;
            }
        });
    }


    private FusedLocationProviderClient mFusedLocationClient;

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

                //새로운 도전
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

                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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
        Log.e("abc", j + "onLocationChanged lat,lon = (" + latitude + "," + longitude + ")");

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
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_word:
                EditText tf_location = findViewById(R.id.TF_location);
                String location = tf_location.getText().toString();
                List<Address> addressList;
                Log.e("abc", "현 위치 찾기1 = " + location);

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

                //검색어 = 음식점
                String search = getResources().getString(R.string.restaurant);

                float zoomLevel = mMap.getCameraPosition().zoom;
                String url = GoogleMapUtil.getNearBySearch(FeedMapActivity.this, latitude, longitude, search, zoomLevel);

                Object dataTransfer[] = new Object[3];
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = this;

                new GetNearPlacesTaskForMap().execute(dataTransfer);

                Toast.makeText(FeedMapActivity.this, R.string.search_near_rest, Toast.LENGTH_SHORT).show();

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

//    int PLACE_PICKER_REQUEST = 1;
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                Log.e("abc","플레이스 = " + place.getId());
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
//    }

}