package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.CommentData;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.MyFeedListActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    int cnt_poke = 0;

    String result;
    String rest_name;

    public MyFeedListTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        cnt_poke = 0;
    }

    @Override
    protected ArrayList<FeedReqData> doInBackground(Void... params) {
        ArrayList<FeedReqData> feedReqList = new ArrayList<>();

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

                result = obj.getString("result");

                cnt_poke = Integer.parseInt(obj.getString("cnt_poke"));

                JSONArray feedArr = obj.getJSONArray("feed");
                for (int i = 0; i < feedArr.length(); i++) {
                    ArrayList<UserData> reqUsersList = new ArrayList<>();
                    ArrayList<CommentData> commentsList = new ArrayList<>();

                    JSONObject feedObj = feedArr.getJSONObject(i);

                    String feed_id = feedObj.getString("sid");
                    String feed_status = feedObj.getString("status");
                    String feed_time = feedObj.getString("time");

                    JSONObject hostObj = feedObj.getJSONObject("host");
                    String host_id = hostObj.getString("sid");
                    String host_name = hostObj.getString("name");
                    String host_img = Statics.main_url + hostObj.getString("img_url");

                    JSONObject restObj = feedObj.getJSONObject("rest");
                    String rest_id = restObj.getString("sid");
                    rest_name = restObj.getString("name");
                    String place_id = restObj.getString("place_id");
                    String compound_code = restObj.getString("compound_code");
                    String vicinity = restObj.getString("vicinity");
                    Double lat = Double.parseDouble(restObj.getString("lat"));
                    Double lng = Double.parseDouble(restObj.getString("lng"));
                    LatLng latLng = new LatLng(lat, lng);
                    String rest_phone = restObj.getString("phone");
                    String rest_img = restObj.getString("img_url");

                    if (feedObj.has("users")) {
                        JSONArray usersArr = feedObj.getJSONArray("users");

                        for (int j = 0; j < usersArr.length(); j++) {
                            JSONObject userObj = usersArr.getJSONObject(j);

                            String user_id = userObj.getString("sid");
                            String user_name = userObj.getString("name");
                            String user_img = Statics.main_url + userObj.getString("img_url");
                            String age = userObj.getString("age");
                            String gender = userObj.getString("gender");
                            String token = userObj.getString("token");
                            String status = userObj.getString("status");
                            String time = userObj.getString("time");

                            UserData userData = new UserData(user_id, user_name, age, gender, token, user_img, status);
                            reqUsersList.add(userData);
                        }
                    }

                    FeedReqData data = new FeedReqData(feed_id, feed_status, feed_time,
                            place_id, compound_code, vicinity, latLng,
                            host_id, host_name, host_img,
                            rest_id, rest_name, rest_phone, rest_img,
                            reqUsersList);
                    feedReqList.add(data);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return feedReqList;
    }

    @Override
    protected void onPostExecute(final ArrayList<FeedReqData> feedReqList) {
        super.onPostExecute(feedReqList);
        Log.e("abc", "onPostExecute feedReqList.size = " + feedReqList.size());
        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
//            //vvvvvvvvvvvvvv static ->
//            ProfileFragment.badge_poke_cnt.setText(String.valueOf(cnt_poke));
//
//            if (cnt_poke == 0)
//                ProfileFragment.badge_poke_cnt.setVisibility(View.GONE);

            if (feedReqList.size() > 0) {
                //vvvvvvvvvvvvvvv static 처리되어있음
//                ProfileFragment.myFeedListAdapter = new MyFeedListAdapter(mContext, httpClient, feedReqList);
//                ProfileFragment.recyclerView_myFeed.setAdapter(ProfileFragment.myFeedListAdapter);
//                ProfileFragment.myFeedListAdapter.notifyDataSetChanged();
            } else {

            }



        } else if (activityName.equals("MyFeedListActivity")) {
            if (feedReqList.size() > 0) {
                ((MyFeedListActivity) mContext).txt_no_feed.setVisibility(View.GONE);
                ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.GONE);
                ((MyFeedListActivity) mContext).recyclerView_myFeed.setVisibility(View.VISIBLE);

                ((MyFeedListActivity) mContext).myFeedListAdapter = new MyFeedListAdapter(mContext, httpClient, feedReqList);
                ((MyFeedListActivity) mContext).recyclerView_myFeed.setAdapter(((MyFeedListActivity) mContext).myFeedListAdapter);
                ((MyFeedListActivity) mContext).myFeedListAdapter.notifyDataSetChanged();
            } else {
                ((MyFeedListActivity) mContext).txt_no_feed.setVisibility(View.VISIBLE);
                ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.VISIBLE);
                ((MyFeedListActivity) mContext).recyclerView_myFeed.setVisibility(View.GONE);
            }

        }
    }

}