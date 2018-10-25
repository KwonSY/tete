package honbab.pumkit.com.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import honbab.pumkit.com.adapter.GridViewNearByAdapter;
import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.ReservActivity;

public class GetNearPlacesTaskForReserv extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;
    public Context context;

    public static ArrayList<MapData> mMapList = new ArrayList<>();

    @Override
    protected String doInBackground(Object... objects) {
        context = (Context) objects[0];
        url = (String) objects[1];

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
        Log.e("abc", "onPostExecute s = " + s);

        List<HashMap<String, String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);
        showNearbyPlaces(nearbyPlaceList);
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlaceList) {
        mMapList.clear();

        for (int i=0; i<nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

//            for (String name: googlePlace.keySet()){
//                String key = name.toString();
////                String value = example.get(name).toString();
////                System.out.println(key + " " + value);
//                String xxx = googlePlace.get(key);
//
//                Log.e("abc", "show key = " + key + " : " + xxx);
//            }

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lag"));

            String photo_html_attributions = googlePlace.get("photo_html_attributions");
            String photo_reference = googlePlace.get("photo_reference");
            String place_id = googlePlace.get("place_id");
            String reference = googlePlace.get("reference");

            LatLng latLng = new LatLng(lat, lng);
//            markerOptions.position(latLng);
////            markerOptions.title(placeName + " : " + vicinity);
//            markerOptions.title(placeName);
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

//            mMap.addMarker(markerOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));

            MapData data = new MapData(place_id, placeName, latLng, reference, "");
//            data.setRest_name(placeName);
//            data.setLatLng(latLng);
            if(photo_reference != null && !photo_reference.isEmpty()) {
                String photoUrl = getPlacePhoto(photo_reference);

                data.setRest_img(photoUrl);
            } else {
                data.setRest_img("");
            }
//            data.setPlace_id(place_id);
//            data.setReference(reference);

            mMapList.add(data);
        }
//        Log.e("abc","on GetNearPlacesTaskForReserv mMapList = " + mMapList.size());
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362
        // &radius=1500&type=restaurant
        // &keyword=cruise
        // &key=YOUR_API_KEY

        ReservActivity.mAdapter = new GridViewNearByAdapter(context, mMapList);
        ReservActivity.recyclerView.setAdapter(ReservActivity.mAdapter);
        ReservActivity.mAdapter.notifyDataSetChanged();
    }

    public String getPlacePhoto(String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + context.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();
    }

}