package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.tete.AfterEatingActivity;
import honbab.voltage.com.tete.JoinActivity2;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedListTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;

    public ArrayList<FeedData> feedList = new ArrayList<>();
    private int cnt_after = 0;
    private String after_feed_id, after_user_id, after_user_name, after_user_img;

    public MyFeedListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = (Fragment) ((MainActivity) mContext).pagerAdapter.instantiateItem(((MainActivity) mContext).viewPager, 1);

        feedList.clear();
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

                JSONArray hash_arr = obj.getJSONArray("feed");
                for (int i = 0; i < hash_arr.length(); i++) {
                    JSONObject obj2 = hash_arr.getJSONObject(i);
                    //피드정보
                    String feed_id = obj2.getString("sid");
                    String status = obj2.getString("status");
                    String feed_time = obj2.getString("time");

                    //등록자 정보
                    JSONObject host_obj = obj2.getJSONObject("host");
                    String host_id = host_obj.getString("sid");
                    String host_name = host_obj.getString("name");
                    String host_img = Statics.main_url + host_obj.getString("img_url");
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
                    JSONObject user_obj = obj2.getJSONArray("users").getJSONObject(0);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String user_img = Statics.main_url + user_obj.getString("img_url");
                    String user_age = user_obj.getString("age");
                    String user_gender = user_obj.getString("gender");
                    String user_token = user_obj.getString("token");

                    FeedData feedData;
                    if (host_id.equals(Statics.my_id)) {
                        feedData = new FeedData(feed_id, feed_time,
                                user_id, user_name, user_age, user_gender, user_img, user_token,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                status);
                    } else {
                        feedData = new FeedData(feed_id, feed_time,
                                host_id, host_name, host_age, host_gender, host_img, host_token,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                status);
                    }
                    feedList.add(feedData);
                }

                JSONArray after_arr = obj.getJSONArray("after");
                for (int i = 0; i < after_arr.length(); i++) {
                    JSONObject obj2 = after_arr.getJSONObject(i);
                    after_feed_id = obj2.getString("sid");
                    JSONObject user_obj = obj2.getJSONObject("users");
                    after_user_id = user_obj.getString("sid");
                    after_user_name = user_obj.getString("name");
                    after_user_img = Statics.main_url + user_obj.getString("img_url");

                    cnt_after++;
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
            ((MyFeedFragment) fragment).mAdapter = new MyFeedListAdapter(mContext, feedList);
            ((MyFeedFragment) fragment).recyclerView_myfeed.setAdapter(((MyFeedFragment) fragment).mAdapter);
            ((MyFeedFragment) fragment).mAdapter.notifyDataSetChanged();

            ((MyFeedFragment) fragment).swipeContainer.setRefreshing(false);

            if (cnt_after > 0) {
                Intent intent = new Intent(mContext, AfterEatingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("after_feed_id", after_feed_id);
                intent.putExtra("toId", after_user_id);
                intent.putExtra("after_user_name", after_user_name);
                intent.putExtra("after_user_img", after_user_img);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            }

            if (((MyFeedFragment) fragment).mAdapter.getItemCount() == 0) {
                ((MyFeedFragment) fragment).line_timeline_vertical.setVisibility(View.INVISIBLE);
                ((MyFeedFragment) fragment).layout_no_my_schedule.setVisibility(View.VISIBLE);
                ((MyFeedFragment) fragment).recyclerView_myfeed.setVisibility(View.INVISIBLE);
//                ((MyFeedFragment) fragment).btn_go_rest_like.setOnClickListener(((MyFeedFragment) fragment).mOnClickListener);
//                ((MainActivity) mContext).viewPager.setCurrentItem(0);
//                ((MainActivity) mContext).getSupportFragmentManager().beginTransaction()
//                        .replace(((MainActivity) mContext).viewPager.getChildAt(0).getId(), new NoMyFeedFragment())
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .addToBackStack("myfeed")
//                        .commit();
            } else {
                Log.e("abc", "222 mAdapter.getItemCount() = " + ((MyFeedFragment) fragment).mAdapter.getItemCount());
                ((MyFeedFragment) fragment).line_timeline_vertical.setVisibility(View.VISIBLE);
                ((MyFeedFragment) fragment).layout_no_my_schedule.setVisibility(View.GONE);
                ((MyFeedFragment) fragment).recyclerView_myfeed.setVisibility(View.VISIBLE);
            }

            try {
                UserData myData = new AccountTask(mContext, 0).execute(Statics.my_id).get();

                if (myData.getImg_url().contains("null")) {
                    Intent intent = new Intent(mContext, JoinActivity2.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                }

                Picasso.get().load(myData.getImg_url())
                        .placeholder(R.drawable.icon_noprofile_circle)
                        .error(R.drawable.icon_noprofile_circle)
                        .transform(new CircleTransform())
                        .into(((MyFeedFragment) fragment).img_my);
                ((MyFeedFragment) fragment).txt_myName.setText(myData.getUser_name());
                ((MyFeedFragment) fragment).txt_comment.setText(myData.getComment());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}