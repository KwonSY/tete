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

import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.tete.AfterEatingActivity;
import honbab.voltage.com.tete.JoinActivity2;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.Encryption;
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
//    private String cnt_babfr;
    private int cnt_babfr = 0; int cnt_reqfr = 0;
    private String my_name, my_img, my_comment;
    private String after_feed_id, host_id, host_name, host_img, guest_id, guest_name, guest_img;

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
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab2.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                cnt_babfr = obj.getInt("cnt_babfr");
                cnt_reqfr = obj.getInt("cnt_reqfr");

                JSONObject myProfile = obj.getJSONObject("my");
                my_name = myProfile.getString("name");
                my_img = myProfile.getString("img_url");
                my_comment = myProfile.getString("comment");

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
                    JSONObject user_obj = obj2.getJSONArray("users").getJSONObject(0);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String user_img = user_obj.getString("img_url");
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
                if (after_arr.length() > 0) {
                    //
                    JSONObject after_ar0 = after_arr.getJSONObject(0);
                    after_feed_id = after_ar0.getString("sid");
                    //
                    JSONObject obj_host = after_ar0.getJSONObject("host");
                    host_id = obj_host.getString("sid");
                    host_name = obj_host.getString("name");
                    host_img = obj_host.getString("img_url");
                    //
                    JSONObject obj_guest = after_ar0.getJSONObject("guest");
                    guest_id = obj_guest.getString("sid");
                    guest_name = obj_guest.getString("name");
                    guest_img = obj_guest.getString("img_url");

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

            ((MyFeedFragment) fragment).btn_go_babfrlist.setText("친구 " + cnt_babfr);
            if (cnt_reqfr > 0)
                ((MyFeedFragment) fragment).cnt_reqfr.setVisibility(View.VISIBLE);
            else
                ((MyFeedFragment) fragment).cnt_reqfr.setVisibility(View.INVISIBLE);
//            ((MyFeedFragment) fragment).cnt_reqfr.setText(cnt_reqfr);

            if (cnt_after > 0) {
                Intent intent = new Intent(mContext, AfterEatingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("after_feed_id", after_feed_id);
                if (host_id.equals(Statics.my_id)) {
                    intent.putExtra("toId", guest_id);
                    intent.putExtra("after_user_name", guest_name);
                    intent.putExtra("after_user_img", guest_img);
                } else {
                    intent.putExtra("toId", host_id);
                    intent.putExtra("after_user_name", host_name);
                    intent.putExtra("after_user_img", host_img);
                }
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
                ((MyFeedFragment) fragment).line_timeline_vertical.setVisibility(View.VISIBLE);
                ((MyFeedFragment) fragment).layout_no_my_schedule.setVisibility(View.GONE);
                ((MyFeedFragment) fragment).recyclerView_myfeed.setVisibility(View.VISIBLE);
            }

            if (my_img == null || my_img.contains("null")) {
                Log.e("abc", "222 이미지 없음 = " + my_img);
                Intent intent = new Intent(mContext, JoinActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            } else {
                Picasso.get().load(my_img)
                        .placeholder(R.drawable.icon_noprofile_circle)
                        .error(R.drawable.icon_noprofile_circle)
                        .transform(new CircleTransform())
                        .into(((MyFeedFragment) fragment).img_my);
            }

            ((MyFeedFragment) fragment).txt_myName.setText(my_name);
            ((MyFeedFragment) fragment).txt_comment.setText(my_comment);

//            try {
////                UserData myData = new AccountTask(mContext, 0).execute(Statics.my_id).get();
//
//                Log.e("abc", "222 myData.getImg_url() = " + my_img);
////                if (myData.getImg_url() == null || myData.getImg_url().contains("null")) {
//                if (my_img == null || my_img.contains("null")) {
//                    Log.e("abc", "222 이미지 없음 = " + my_img);
//                    Intent intent = new Intent(mContext, JoinActivity2.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mContext.startActivity(intent);
//                    ((Activity) mContext).finish();
//                } else {
//                    Picasso.get().load(my_img)
//                            .placeholder(R.drawable.icon_noprofile_circle)
//                            .error(R.drawable.icon_noprofile_circle)
//                            .transform(new CircleTransform())
//                            .into(((MyFeedFragment) fragment).img_my);
//                }
//
//                ((MyFeedFragment) fragment).txt_myName.setText(my_name);
//                ((MyFeedFragment) fragment).txt_comment.setText(my_comment);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

}