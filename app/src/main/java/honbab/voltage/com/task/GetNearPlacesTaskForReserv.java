package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import honbab.voltage.com.adapter.GridViewNearByAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.tete.MapsActivity;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.DataParser;
import honbab.voltage.com.widget.DownloadUrl;

public class GetNearPlacesTaskForReserv extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    String url;
    public Context mContext;

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

            String compound_code = googlePlace.get("compound_code");
            String formatted_phone_number = googlePlace.get("formatted_phone_number");
            String photo_html_attributions = googlePlace.get("photo_html_attributions");
            String photo_reference = googlePlace.get("photo_reference");
            String placeName = googlePlace.get("place_name");
            String place_id = googlePlace.get("place_id");
            double lat = Double.parseDouble( googlePlace.get("lat"));
            double lng = Double.parseDouble( googlePlace.get("lag"));
            String reference = googlePlace.get("reference");
            String vicinity = googlePlace.get("vicinity");
            Log.e("abc", "GetNearPlacesTask compound_code = " + compound_code);
            Log.e("abc", "GetNearPlacesTask formatted_phone_number = " + formatted_phone_number);

            LatLng latLng = new LatLng(lat, lng);

            FeedData data = new FeedData(null,
                    null, null, null, null, null, null,
                    null, placeName,
                    compound_code, latLng, place_id, "", formatted_phone_number, vicinity,
                    null, null);
            if(photo_reference != null && !photo_reference.isEmpty()) {
//                String photoUrl = getPlacePhoto(photo_reference);
                String photoUrl = GoogleMapUtil.getPlacePhoto(mContext, photo_reference);

                data.setRest_img(photoUrl);
            } else {
                data.setRest_img("");
            }

            MapsActivity.mMapList.add(data);
        }

        if (mContext.getClass().equals(ReservActivity.class)) {
            ((ReservActivity)mContext).mAdapter = new GridViewNearByAdapter(mContext, MapsActivity.mMapList);
            ((ReservActivity)mContext).recyclerView.setAdapter(((ReservActivity)mContext).mAdapter);
            ((ReservActivity)mContext).mAdapter.notifyDataSetChanged();
        } else if (mContext.getClass().equals(OneRestaurantActivity.class)) {
            Log.e("abc", "여기 액티비티는 = OneRestaurantActivity ," + OneRestaurantActivity.class);
//            ((OneRestaurantActivity) mContext).mMapList = MapsActivity.mMapList;
//
//            String place_id = MapsActivity.mMapList.get(0).getPlace_id();
        }

    }

    //xxxxxxxxxxxxxx
//    public String getPlacePhoto(String photoreference) {
//        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
//        sb.append("maxwidth=" + "400");
//        sb.append("&photoreference=" + photoreference);
//        sb.append("&key=" + mContext.getResources().getString(R.string.google_maps_api_key));
//
//        return sb.toString();
//    }

}