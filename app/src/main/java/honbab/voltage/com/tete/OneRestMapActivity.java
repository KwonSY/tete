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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import honbab.voltage.com.utils.ButtonUtil;

public class OneRestMapActivity extends FragmentActivity implements OnMapReadyCallback,
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
//    double latitude, longitude;
    LatLng latLng;
    String rest_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onerest_map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 이상
            checkLocationPermission();
        }

        Intent intent = getIntent();
        latLng = intent.getParcelableExtra("latLng");
        rest_name = intent.getParcelableExtra("rest_name");

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(rest_name);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        Parcelable mCurrentLocation = savedInstanceState.getParcelable("location");
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        final GestureDetector gestureDetector;
//        gestureDetector = new GestureDetector(getApplicationContext(),new GestureDetector.SimpleOnGestureListener() {
//
//            //누르고 뗄 때 한번만 인식하도록 하기위해서
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return true;
//            }
//        });


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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMaxZoomPreference(18);
        mMap.setMinZoomPreference(14);
        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(rest_name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
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

//                mFusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                // Got last known location. In some rare situations this can be null.
//                                if (location != null) {
//                                    // Logic to handle location object
////                                    Location myLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
//                                    Log.e("abc", "myLocation = " + location);
//                                    latitude = location.getLatitude();
//                                    longitude = location.getLongitude();
//                                    LatLng latLng = new LatLng(latitude, longitude);
//
////                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//                                }
//                            }
//                        });

            }
        }
    }





    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        client.connect();
    }

    @Override
    public void onLocationChanged(Location location) {

//        latitude = location.getLatitude();
//        longitude = location.getLongitude();
//        lastlocation = location;
//        if (currentLocationmMarker != null) {
//            currentLocationmMarker.remove();
//        }
//
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Location");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//        currentLocationmMarker = mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
//
//        if (client != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
//        }

//        j++;

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