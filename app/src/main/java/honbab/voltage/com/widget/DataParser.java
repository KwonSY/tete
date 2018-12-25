package honbab.voltage.com.widget;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlacesMap = new HashMap<>();

        String compound_code = "";
        String formatted_phone_number = "";
        String fullAddress = "";
        String latitude = "";
        String longitude = "";
        String placeName = "-NA-";
        String rating = "";
        String vicinity = "-NA-";

        String photo_height = "";
        String photo_html_attributions = "";
        String photo_reference = "";
        String photo_width = "";

        String place_id = "";
        String reference = "";
        Log.e("abc", "googlePlaceJson = " + googlePlaceJson);
        try {
            if (!googlePlaceJson.isNull("formatted_phone_number")) {
                formatted_phone_number = googlePlaceJson.getString("formatted_phone_number");
            }
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            if (!googlePlaceJson.isNull("address_components")) {
                JSONArray addressArray = googlePlaceJson.getJSONArray("address_components");
                for (int i=0; i<addressArray.length(); i++) {
                    JSONObject addObj = addressArray.getJSONObject(i);
                    String long_name = addObj.getString("long_name");
                    fullAddress += long_name;
                }
            }

            if (!googlePlaceJson.isNull("photos")) {
                JSONArray photosArray = googlePlaceJson.getJSONArray("photos");
                photo_height = photosArray.getJSONObject(0).getString("height");
                photo_html_attributions = photosArray.getJSONObject(0).getString("html_attributions");
                photo_reference = photosArray.getJSONObject(0).getString("photo_reference");
                photo_width = photosArray.getJSONObject(0).getString("width");
            }

            if (!googlePlaceJson.isNull("plus_code")) {
                place_id = googlePlaceJson.getString("place_id");
            }
            if (!googlePlaceJson.isNull("plus_code")) {
                JSONObject plus_code = googlePlaceJson.getJSONObject("plus_code");
                compound_code = plus_code.getString("compound_code");
            }
            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }
            reference = googlePlaceJson.getString("reference");

            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }

            googlePlacesMap.put("compound_code", compound_code);
            googlePlacesMap.put("formatted_phone_number", formatted_phone_number);
            googlePlacesMap.put("fullAddress", fullAddress);
            googlePlacesMap.put("place_name", placeName);
            googlePlacesMap.put("place_id", place_id);
            googlePlacesMap.put("lat", latitude);
            googlePlacesMap.put("lag", longitude);
            googlePlacesMap.put("rating", rating);
            googlePlacesMap.put("reference", reference);
            googlePlacesMap.put("vicinity", vicinity);

            googlePlacesMap.put("photo_height", photo_height);
            googlePlacesMap.put("photo_html_attributions", photo_html_attributions);
            googlePlacesMap.put("photo_reference", photo_reference);
            googlePlacesMap.put("photo_width", photo_width);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
        HashMap<String, String> placeMap = null;

        for (int i=0; i<count; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    public List<HashMap<String, String>> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            Log.e("abc", "DataParser jsonObject = " + jsonObject);
            if (jsonObject.has("results")) {
                Log.e("abc", "어딜통과하나1 = ");
                jsonArray = jsonObject.getJSONArray("results");
            } else {
                JSONObject obj = jsonObject.getJSONObject("result");
                Log.e("abc", "어딜통과하나2 = " + obj);
//                jsonArray.p
                jsonArray = new JSONArray();
                jsonArray.put(obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

}
