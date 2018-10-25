package honbab.pumkit.com.widget;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import honbab.pumkit.com.adapter.RecyclerViewRestAdapter;
import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.MapsActivity;
import honbab.pumkit.com.tete.R;

public class GetNearPlacesTaskForMap extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;
    public Context context;

    public static ArrayList<MapData> mMapRestList = new ArrayList<>();

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        context = (Context) objects[2];

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
        mMapRestList.clear();

        for (int i=0; i<nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlaceList.get(i);

//            for (String name: googlePlace.keySet()){
//                String key =name.toString();
////                String value = example.get(name).toString();
////                System.out.println(key + " " + value);
//                String xxx = googlePlace.get(key);
//
//                Log.e("abc", "show key = " + key + " : " + xxx);
//            }

            String placeName = googlePlace.get("place_name");
//            String placeName = googlePlace.get("name");
            String vicinity = googlePlace.get("vicinity");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lag"));

            String photo_html_attributions = googlePlace.get("photo_html_attributions");
            String photo_reference = googlePlace.get("photo_reference");


            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


            MapData data = new MapData();
            data.setRest_name(placeName);
            data.setLatLng(latLng);
            if(photo_reference != null && !photo_reference.isEmpty()) {
                String photoUrl = getPlacePhoto(photo_reference);

                data.setRest_img(photoUrl);
            } else {
                data.setRest_img("");
            }

            mMapRestList.add(data);
        }

        MapsActivity.recyclerViewRestAdapter = new RecyclerViewRestAdapter(context, mMapRestList);
        MapsActivity.recyclerView.setAdapter(MapsActivity.recyclerViewRestAdapter);
        MapsActivity.recyclerViewRestAdapter.notifyDataSetChanged();
    }

    public String getPlacePhoto(String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + context.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();

        //xxxxxxxxxxxxxxxx 안씀
        //Volley Que
//        RequestQueue mQueue = Volley.newRequestQueue(context.getApplicationContext());
//        StringRequest request = new StringRequest(Request.Method.GET, sb.toString(), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("abc", "xxxx  response = "  + response);
////                ParserTask parserTask = new ParserTask(googleMap,context);
////                parserTask.execute(response);
////
////                double latitude = Double.valueOf(obj.getGeometry().getLocation().getLat());
////                double longitude = Double.valueOf(obj.getGeometry().getLocation().getLng());
////                Log.v("lnglat",latitude+","+longitude+" "+Latitude+","+Longitude);
//////                LatLngBounds.Builder builder = new LatLngBounds.Builder();
////                LatLngBounds.Builder mapBound = new LatLngBounds.Builder();
////                mapBound.include(new LatLng(latitude, longitude)).include(new LatLng(Double.valueOf(Latitude),Double.valueOf(Longitude)));
//////                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapBound.getCenter(), 12));
////                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(mapBound.build(), 12);
////                googleMap.moveCamera(cameraUpdate);
//            }
//        }
//                , new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//
//        mQueue.add(request);
    }




}
