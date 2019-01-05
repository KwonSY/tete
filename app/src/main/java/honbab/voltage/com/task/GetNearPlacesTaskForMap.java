package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import honbab.voltage.com.adapter.FeedMapHorzRestAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.tete.MapsActivity;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.DataParser;
import honbab.voltage.com.widget.DownloadUrl;

public class GetNearPlacesTaskForMap extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    public Context mContext;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        mContext = (Context) objects[2];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        MapsActivity.mMapList.clear();
        MapsActivity.mMarkersList.clear();

        for (int i=0; i<nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

            for (String name: googlePlace.keySet()){
                String key =name.toString();
//                String value = example.get(name).toString();
//                System.out.println(key + " " + value);
                String xxx = googlePlace.get(key);

                Log.e("abc", "show key = " + key + " : " + xxx);
            }

            String compound_code = googlePlace.get("compound_code");
            String formatted_phone_number = googlePlace.get("formatted_phone_number");
            String photo_html_attributions = googlePlace.get("photo_html_attributions");
            String photo_reference = googlePlace.get("photo_reference");
            String placeName = googlePlace.get("place_name");
            String place_id = googlePlace.get("place_id");
//            String plus_code = googlePlace.get("plus_code");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lag"));
            String vicinity = googlePlace.get("vicinity");
            Log.e("abc", "GetNearPlacesTaskFormap formatted_phone_number = " + formatted_phone_number);
            Log.e("abc", "이거 빈시트 도냐? = " + vicinity);

            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            FeedData data = new FeedData();
            data.setRest_name(placeName);
            data.setCompound_code(compound_code);
            data.setLatLng(latLng);
            data.setPlace_id(place_id);
            data.setRest_phone(formatted_phone_number);
            data.setVicinity(vicinity);

            if(photo_reference != null && !photo_reference.isEmpty()) {
//                String photoUrl = getPlacePhoto(photo_reference);
                String photoUrl = GoogleMapUtil.getPlacePhoto(mContext, photo_reference);

                data.setRest_img(photoUrl);
            } else {
                data.setRest_img("");
            }

            MapsActivity.mMapList.add(data);
            MapsActivity.mNames.add(data.getRest_name());

            Marker marker = mMap.addMarker(markerOptions);
            MapsActivity.mMarkersList.add(marker);
        }

        MapsActivity.feedMapHorzRestAdapter = new FeedMapHorzRestAdapter(mContext, MapsActivity.mMapList);
        MapsActivity.recyclerView.setAdapter(MapsActivity.feedMapHorzRestAdapter);
        MapsActivity.feedMapHorzRestAdapter.notifyDataSetChanged();
    }

}
