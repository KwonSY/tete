package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import honbab.voltage.com.adapter.FeedListAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedListTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    Fragment fragment;

    String my_id;

    String result;
    String rest_name;

    public MyFeedListTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        FragmentManager fm = ((MainActivity) mContext).getSupportFragmentManager();
        fragment = fm.getFragments().get(0);
        Log.e("abc", "frfragment = " + fragment);
//        MyFeedFragment fragment = (MyFeedFragment) fm.findFragmentById(R.id.placeholder_fragment);
//        fragment.setItem();
        ((MyFeedFragment) fragment).feedList.clear();
        ((MyFeedFragment) fragment).mAdapter.clearItemList();
    }

    @Override
    protected Void doInBackground(Void... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "my_feed_list")
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "my_feed_list = " + obj);

                JSONArray hash_arr = obj.getJSONArray("feed");
                for (int i = 0; i < hash_arr.length(); i++) {
                    JSONObject obj2 = hash_arr.getJSONObject(i);
                    //피드정보
                    String feed_id = obj2.getString("sid");
                    String status = obj2.getString("status");
                    String time = obj2.getString("time");

                    //등록자 정보
                    JSONObject host_obj = obj2.getJSONObject("host");
                    String host_id = host_obj.getString("sid");
                    String host_name = host_obj.getString("name");
                    String host_img = host_obj.getString("img_url");
                    String host_age = host_obj.getString("age");
                    String host_gender = host_obj.getString("gender");
                    String host_token = host_obj.getString("token");

                    //음식점 정보
                    JSONObject rest_obj = obj2.getJSONObject("rest");
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

                    //참가자
//                        if (obj2.getJSONArray("users").length() > 0) {
                    JSONObject user_obj = obj2.getJSONArray("users").getJSONObject(0);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String user_img = user_obj.getString("img_url");
                    String user_age = user_obj.getString("age");
                    String user_gender = user_obj.getString("gender");
                    String user_token = user_obj.getString("token");

                    FeedData feedData;
                    if (host_id.equals(Statics.my_id)) {
                        feedData = new FeedData(feed_id,
                                user_id, user_name, user_age, user_gender, user_img, user_token,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                status, time);
                    } else {
                        feedData = new FeedData(feed_id,
                                host_id, host_name, host_age, host_gender, host_img,host_token,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                status, time);
                    }
                    ((MyFeedFragment) fragment).feedList.add(feedData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(feedReqList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {

            ((MyFeedFragment) fragment).mAdapter = new FeedListAdapter(mContext, httpClient, ((MyFeedFragment) fragment).feedList);
            ((MyFeedFragment) fragment).gridView_feed.setAdapter(((MyFeedFragment) fragment).mAdapter);
            ((MyFeedFragment) fragment).mAdapter.notifyDataSetChanged();

            ((MyFeedFragment) fragment).swipeContainer.setRefreshing(false);

        }
    }

}