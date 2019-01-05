package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestaurantListTask extends AsyncTask<Void, Void, ArrayList<RestData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private ArrayList<RestData> restList = new ArrayList<>();
    String result;

    public RestaurantListTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        restList.clear();
    }

    @Override
    protected ArrayList<RestData> doInBackground(Void... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "rest_list")
                .add("pack", "GNG1")
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray rest_arr = obj.getJSONArray("rest");
                for (int i = 0; i < rest_arr.length(); i++) {
                    JSONObject rest_obj = rest_arr.getJSONObject(i);
                    String rest_id = rest_obj.getString("sid");
                    String rest_name = rest_obj.getString("name");
                    String compound_code = rest_obj.getString("compound_code");
                    String vicinity = rest_obj.getString("vicinity");
                    String place_id = rest_obj.getString("place_id");
                    String lat = rest_obj.getString("lat");
                    String lng = rest_obj.getString("lng");
                    Double db_lat = Double.parseDouble(lat);
                    Double db_lng = Double.parseDouble(lng);
                    LatLng latLng = new LatLng(db_lat, db_lng);
                    String rest_phone = rest_obj.getString("phone");
                    String rest_img = rest_obj.getString("img_url");

//                    FeedData feedData = new FeedData(feed_id,
//                            null, null, null, null, null, null,
//                            rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
//                            null, null);
                    RestData restData = new RestData(rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity);
                    restList.add(restData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return restList;
    }

    @Override
    protected void onPostExecute(final ArrayList<RestData> restList) {
        super.onPostExecute(restList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("GodTinderActivity")) {

            if (restList.size() > 0) {

            } else {

            }
        }
    }

}