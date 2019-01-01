package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestaurantListTask extends AsyncTask<Void, Void, ArrayList<FeedData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private ArrayList<FeedData> feedList = new ArrayList<>();
    String result;

    public RestaurantListTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        feedList.clear();
    }

    @Override
    protected ArrayList<FeedData> doInBackground(Void... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "feed_list")
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray hash_arr = obj.getJSONArray("feed_list");
                for (int i = 0; i < hash_arr.length(); i++) {
                    JSONObject obj2 = hash_arr.getJSONObject(i);

                    int feed_id = obj2.getInt("sid");

                    //등록자 정보
                    JSONObject host_obj = obj2.getJSONObject("host");
                    String user_id = host_obj.getString("sid");
                    String user_name = host_obj.getString("name");
                    String user_img = host_obj.getString("img_url");
                    String user_age = host_obj.getString("age");
                    String user_gender = host_obj.getString("gender");

                    //음식점 정보
                    JSONObject rest_obj = obj2.getJSONObject("rest");
                    int rest_id = rest_obj.getInt("sid");
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

                    String status = obj2.getString("status");
                    String time = obj2.getString("time");

                    FeedData feedData = new FeedData(feed_id,
                            user_id, user_name, user_age, user_gender, user_img,
                            rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                            status, time);
                    feedList.add(feedData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return feedList;
    }

    @Override
    protected void onPostExecute(final ArrayList<FeedData> feedList) {
        super.onPostExecute(feedList);
        Log.e("abc", "onPostExecute feedReqList.size = " + feedList.size());
        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("GodTinderActivity")) {

            if (feedList.size() > 0) {

            } else {

            }
        }
    }

}