package honbab.pumkit.com.utils;

import android.content.Context;

import honbab.pumkit.com.tete.R;

public class GoogleMapUtil {

    public static String getDetailUrl(Context mContext, String placeId) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        googlePlaceUrl.append("placeid=" + placeId);
        googlePlaceUrl.append("&language=" + "ko");
        googlePlaceUrl.append("&fields=" + "name,rating,formatted_phone_number,address_component,adr_address,photo");
        googlePlaceUrl.append("&key=" + mContext.getString(R.string.google_maps_api_key));

        return googlePlaceUrl.toString();
    }

    public static String getPlacePhoto(Context mContext, String photoreference) {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        sb.append("maxwidth=" + "400");
        sb.append("&photoreference=" + photoreference);
        sb.append("&key=" + mContext.getResources().getString(R.string.google_maps_api_key));

        return sb.toString();
    }

    public static String getNearBySearch(Context mContext, double latitude, double longitude, String nearbyPlace, float zoomLevel) {
        int PROXIMITY_RADIUS = 300;

        if (zoomLevel == 0) {
            PROXIMITY_RADIUS = 500;
        } else if (zoomLevel>=15.84 && zoomLevel<16.22) {
            PROXIMITY_RADIUS = 100;
        } else if (zoomLevel>=15.22 && zoomLevel<15.84) {
            PROXIMITY_RADIUS = 150;
        } else if (zoomLevel>=15 && zoomLevel<15.22) {
            PROXIMITY_RADIUS = 300;
        } else if (zoomLevel>=14.7 && zoomLevel<15) {
            PROXIMITY_RADIUS = 500;
        } else if (zoomLevel>=14.55 && zoomLevel<14.7) {
            PROXIMITY_RADIUS = 700;
        } else if (zoomLevel>=14.3 && zoomLevel<14.55) {
            PROXIMITY_RADIUS = 1000;
        } else if (zoomLevel>=14 && zoomLevel<14.3) {
            PROXIMITY_RADIUS = 1250;
        } else if (zoomLevel<14) {
            PROXIMITY_RADIUS = 1500;//13.42
        } else {
            PROXIMITY_RADIUS = 500;
        }

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("language=ko");
        googlePlaceUrl.append("&location=" + latitude + "," + longitude);
//        googlePlaceUrl.append("&rankby=" + "distance");
        googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type=" + nearbyPlace);
        googlePlaceUrl.append("&keyword=" + nearbyPlace);
//        googlePlaceUrl.append("&fields=" + "photos,formatted_address,name,opening_hours,rating");
        googlePlaceUrl.append("&sensor=true");
//        googlePlaceUrl.append("&locationbias=circle:2000@" + latitude + "," + longitude);
        googlePlaceUrl.append("&key=" + mContext.getString(R.string.google_maps_api_key));

        return googlePlaceUrl.toString();
    }
}