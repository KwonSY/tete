package honbab.pumkit.com.widget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import honbab.pumkit.com.data.MapData;

public class PhotoParser {

    private HashMap<String, Object> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, Object> googlePlacesMap = new HashMap<>();

        String placeName = "-NA-";
        String vicinity = "-NA-";
        String adr_address = "";
        String fullAddress = "";
        String formatted_phone_number = "";
        String name = "";
        String rating = "";

        ArrayList<MapData> photos = new ArrayList<>();

//        Log.e("abc", "googlePlaceJson = " + googlePlaceJson);
        try {
            if (!googlePlaceJson.isNull("address_components")) {
                JSONArray addressArray = googlePlaceJson.getJSONArray("address_components");
                for (int i=0; i<addressArray.length(); i++) {
                    JSONObject addObj = addressArray.getJSONObject(i);
                    String long_name = addObj.getString("long_name");

                    if (i>0)
                        fullAddress += " ";

                    fullAddress = fullAddress + long_name;
                }
            }

            if (!googlePlaceJson.isNull("adr_address")) {
                adr_address = googlePlaceJson.getString("adr_address");
            }

            if (!googlePlaceJson.isNull("formatted_phone_number")) {
                formatted_phone_number = googlePlaceJson.getString("formatted_phone_number");
            }

            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }

            if (!googlePlaceJson.isNull("rating")) {
                rating = googlePlaceJson.getString("rating");
            }

            if (!googlePlaceJson.isNull("photos")) {
                JSONArray photoArr = googlePlaceJson.getJSONArray("photos");

                for (int i=0; i<photoArr.length(); i++) {
                    JSONObject photoObj = photoArr.getJSONObject(i);
                    String height = photoObj.getString("height");
                    String html_attributions = photoObj.getString("html_attributions");
                    String photo_reference = photoObj.getString("photo_reference");
                    String width = photoObj.getString("width");

                    MapData mapData = new MapData();
                    mapData.setHeight(Integer.parseInt(height));
                    mapData.setRest_img(photo_reference);
                    mapData.setWidth(Integer.parseInt(width));

                    photos.add(mapData);
                }
            }

            googlePlacesMap.put("adr_address", adr_address);
            googlePlacesMap.put("formatted_phone_number", formatted_phone_number);
            googlePlacesMap.put("fullAddress", fullAddress);
            googlePlacesMap.put("name", name);
            googlePlacesMap.put("photos", photos);
            googlePlacesMap.put("rating", rating);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
//        int count = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<>();
//        HashMap<String, String> placeMap = null;
//
//        for (int i=0; i<count; i++) {
//            try {
//                placeMap = getPlace((JSONObject) jsonArray.get(i));
//                placesList.add(placeMap);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
        return placesList;
    }

    public HashMap<String, Object> parse(String jsonData) {
        JSONArray jsonArray = null;
        JSONObject jsonObject;
        JSONObject obj = null;

        try {
            jsonObject = new JSONObject(jsonData);
//            jsonArray = jsonObject.getJSONArray("results");
            obj = jsonObject.getJSONObject("result");

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        return getPlaces(jsonArray);
        return getPlace(obj);
    }

}
