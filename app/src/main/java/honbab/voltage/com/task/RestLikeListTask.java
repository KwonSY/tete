package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.RestLikeOneDateAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.RestLikeData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.RestLikeFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestLikeListTask extends AsyncTask<Void, Void, ArrayList<RestLikeData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment, fragment2;

    private ArrayList<RestLikeData> restLikeList = new ArrayList<>();
    String result;

    public RestLikeListTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().getFragments().get(0);
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
        fragment2 = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
        Log.e("abc","((MainActivity) mContext).getSupportFragmentManager() = " + ((MainActivity) mContext).getSupportFragmentManager().getFragments());
        Log.e("abc","ffffffffragment = " + fragment);

        restLikeList.clear();
        ((RestLikeFragment) fragment).mAdapter.clearItemList();
    }

    @Override
    protected ArrayList<RestLikeData> doInBackground(Void... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "rest_like_list")
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray hash_arr = obj.getJSONArray("rest_like");
                for (int i = 0; i < hash_arr.length(); i++) {
                    JSONObject restlikeObj = hash_arr.getJSONObject(i);

                    String feed_time = restlikeObj.getString("time");
                    JSONArray feedArr = restlikeObj.getJSONArray("feed");

                    ArrayList<FeedData> feedList = new ArrayList<>();

                    for (int j = 0; j < feedArr.length(); j++) {
                        JSONObject feedObj = feedArr.getJSONObject(j);

                        String feed_id = feedObj.getString("sid");

                        //음식점 정보
                        JSONObject rest_obj = feedObj.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name");
                        String compound_code = rest_obj.getString("compound_code");
                        Double db_lat = Double.parseDouble(rest_obj.getString("lat"));
                        Double db_lng = Double.parseDouble(rest_obj.getString("lng"));
                        LatLng latLng = new LatLng(db_lat, db_lng);
                        String place_id = rest_obj.getString("place_id");
                        String rest_phone = rest_obj.getString("phone");
                        String rest_img = rest_obj.getString("img_url");
                        String vicinity = rest_obj.getString("vicinity");

                        ArrayList<UserData> usersList = new ArrayList<>();
                        //음식점좋아요 누른 User
                        JSONArray users_arr = feedObj.getJSONArray("users");
                        for (int k = 0; k < users_arr.length(); k++) {
                            JSONObject user_obj = users_arr.getJSONObject(k);

                            String user_id = user_obj.getString("sid");
                            String user_name = user_obj.getString("name");
                            String age = user_obj.getString("age");
                            String gender = user_obj.getString("gender");
                            String token = user_obj.getString("token");
                            String user_img = Statics.main_url + user_obj.getString("img_url");

                            UserData userData = new UserData(
                                    user_id, user_name,
                                    age, gender, token, user_img, null);
                            usersList.add(userData);
                        }

                        FeedData feedData = new FeedData(
                                feed_id, feed_time,
                                null, null, null, null, null, null,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                null
                        );
                        feedData.setUsersList(usersList);
                        feedList.add(feedData);
                    }

                    RestLikeData RestLikeData = new RestLikeData(feed_time, feedList);
                    restLikeList.add(RestLikeData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return restLikeList;
    }

    @Override
    protected void onPostExecute(final ArrayList<RestLikeData> restLikeList) {
        super.onPostExecute(restLikeList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            if (restLikeList.size() > 0) {
                ((RestLikeFragment) fragment).btn_go_tinder.setVisibility(View.VISIBLE);
                ((RestLikeFragment) fragment).swipeContainer.setVisibility(View.VISIBLE);
                ((RestLikeFragment) fragment).mAdapter = new RestLikeOneDateAdapter(mContext, httpClient, restLikeList);
                ((RestLikeFragment) fragment).recyclerView.setAdapter(((RestLikeFragment) fragment).mAdapter);
                ((RestLikeFragment) fragment).mAdapter.notifyDataSetChanged();

                ((RestLikeFragment) fragment).layout_rest.setVisibility(View.GONE);
                ((RestLikeFragment) fragment).swipeContainer.setRefreshing(false);
//                try {
////                    SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 hh:mm");
////                    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
////                    Date date = formatter2.parse(feed_time + "");
////                    Log.e("abc", "date : " + date.toString());
////                    String str_feed_time = formatter.format(date);
////                    Log.e("abc", "str_feed_time : " + str_feed_time);
//
////                    ((RestLikeOneDateAdapter) ((RestLikeFragment) fragment).mAdapter).
////                    ((RestLikeFragment) fragment).mAdapter.txt_feedTime.setText(str_feed_time);
//
//                    ((RestLikeFragment) fragment).mAdapter = new RestLikeOneDateAdapter(mContext, httpClient, restLikeList);
//                    ((RestLikeFragment) fragment).recyclerView.setAdapter(((RestLikeFragment) fragment).mAdapter);
//                    ((RestLikeFragment) fragment).mAdapter.notifyDataSetChanged();
//
//                    ((RestLikeFragment) fragment).swipeContainer.setRefreshing(false);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }

//                ((RestLikeFragment) fragment).mAdapter = new RestLikeListAdapter(mContext, httpClient, feedList);
            } else {
                ((RestLikeFragment) fragment).btn_go_tinder.setVisibility(View.GONE);
                ((RestLikeFragment) fragment).swipeContainer.setVisibility(View.GONE);
                ((RestLikeFragment) fragment).layout_rest.setVisibility(View.VISIBLE);
                ((RestLikeFragment) fragment).swipeContainer.setRefreshing(false);

//                fragment2;
                new MyFeedListTask(mContext).execute();
            }
        }
    }

}