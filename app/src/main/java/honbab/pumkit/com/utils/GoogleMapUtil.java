package honbab.pumkit.com.utils;

import android.content.Context;

import honbab.pumkit.com.tete.R;

public class GoogleMapUtil {

    public static String getPlacePhoto(Context mContext, String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + mContext.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();
    }

}