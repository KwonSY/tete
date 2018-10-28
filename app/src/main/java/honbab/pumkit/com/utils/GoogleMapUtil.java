package honbab.pumkit.com.utils;

import android.content.Context;
import android.util.Log;

import honbab.pumkit.com.tete.R;

public class GoogleMapUtil {

    public static String getPlacePhoto(Context mContext, String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + mContext.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();
    }

    public static String getDetailUrl(Context mContext, String placeId) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("placeid=" + placeId);
        googlePlaceUrl.append("&fields=" + "name,rating,formatted_phone_number,photo");
        googlePlaceUrl.append("&key=" + mContext.getString(R.string.google_maps_api_key));
        Log.e("abc", "detail = " + googlePlaceUrl);

//        Object dataTransfer[] = new Object[2];
//        dataTransfer[0] = mContext;
//        dataTransfer[1] = googlePlaceUrl.toString();
//
//        GetPhotoTask getPhotoTask = new GetPhotoTask(viewPager, dotsLayout);
//        getPhotoTask.execute(dataTransfer);

        return googlePlaceUrl.toString();
    }
}