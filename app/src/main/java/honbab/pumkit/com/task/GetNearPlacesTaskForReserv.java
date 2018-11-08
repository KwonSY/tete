package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import honbab.pumkit.com.adapter.GridViewNearByAdapter;
import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.MapsActivity;
import honbab.pumkit.com.tete.OneFeedActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.ReservActivity;
import honbab.pumkit.com.widget.DataParser;
import honbab.pumkit.com.widget.DownloadUrl;

public class GetNearPlacesTaskForReserv extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;
    public Context mContext;

//    public static ArrayList<MapData> mMapList = new ArrayList<>();
//    public ArrayList<MapData> mMapList = MapsActivity.mMapList;

    @Override
    protected String doInBackground(Object... objects) {
        mContext = (Context) objects[0];
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
//        mMapList.clear();
        MapsActivity.mMapList.clear();

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
            Log.e("abc",  placeName+ "플레이스2 latLng = " + latLng);

            MapData data = new MapData(place_id, placeName, latLng, reference, "");
            if(photo_reference != null && !photo_reference.isEmpty()) {
                String photoUrl = getPlacePhoto(photo_reference);

                data.setRest_img(photoUrl);
            } else {
                data.setRest_img("");
            }

//            mMapList.add(data);
            MapsActivity.mMapList.add(data);
        }
        //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362
        // &radius=1500&type=restaurant
        // &keyword=cruise
        // &key=YOUR_API_KEY
        if (mContext.getClass().equals(ReservActivity.class)) {
            ReservActivity.mAdapter = new GridViewNearByAdapter(mContext, MapsActivity.mMapList);
            ReservActivity.recyclerView.setAdapter(ReservActivity.mAdapter);
            ReservActivity.mAdapter.notifyDataSetChanged();
        } else if (mContext.getClass().equals(OneFeedActivity.class)) {
            Log.e("abc", "여기 액티비티는 = OneFeedActivity ," + OneFeedActivity.class);
            ((OneFeedActivity) mContext).mMapList = MapsActivity.mMapList;

            String place_id = MapsActivity.mMapList.get(0).getPlace_id();
        }

    }

    public String getPlacePhoto(String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + mContext.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();
    }

}