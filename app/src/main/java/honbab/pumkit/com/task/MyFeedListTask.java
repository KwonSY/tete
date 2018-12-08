package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedListAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.fragment.ProfileFragment;
import honbab.pumkit.com.tete.MyFeedListActivity;
import honbab.pumkit.com.tete.Statics;
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

                if (result.equals("0")) {
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
                        Double lat = Double.parseDouble(restObj.getString("lat"));
                        Double lng = Double.parseDouble(restObj.getString("lng"));
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
                                String status = userObj.getString("status");
                                String time = userObj.getString("time");

                                UserData userData = new UserData(user_id, user_name, age, gender, user_img, status);
                                reqUsersList.add(userData);
                            }
                        }

                        FeedReqData data = new FeedReqData(feed_id, feed_status, feed_time,
                                host_id, host_name, host_img,
                                rest_id, rest_name, rest_img, reqUsersList, commentsList);
                        feedReqList.add(data);
                    }
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
        Log.e("abc", "mContext = " + mContext);
        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            //vvvvvvvvvvvvvv static ->
            ProfileFragment.badge_poke_cnt.setText(String.valueOf(cnt_poke));

            if (cnt_poke==0)
                ProfileFragment.badge_poke_cnt.setVisibility(View.GONE);

            if (feedReqList.size() > 0) {
//                ((MainActivity) mContext).txt_no_feed.setVisibility(View.GONE);
//                ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.GONE);
//                ((MyFeedListActivity) mContext).recyclerView_myFeed.setVisibility(View.VISIBLE);

//                Fragment fragment = ((MainActivity) mContext).pagerAdapter.getItem(1);
//                ProfileFragment.newInstance(feedReqList);
//                ((MainActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment_profile, fragment).commit();

                //vvvvvvvvvvvvvvv static 처리되어있음
                ProfileFragment.myFeedListAdapter = new MyFeedListAdapter(mContext, httpClient, feedReqList);
                ProfileFragment.recyclerView_myFeed.setAdapter(ProfileFragment.myFeedListAdapter);
                ProfileFragment.myFeedListAdapter.notifyDataSetChanged();
            } else {
//                ((MyFeedListActivity) mContext).txt_no_feed.setVisibility(View.VISIBLE);
//                ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.VISIBLE);
//                ProfileFragment.recyclerView_myFeed.setVisibility(View.GONE);
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

//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
////        builder.setTitle("AlertDialog Title");
//        builder.setMessage(R.string.ask_cancle_godmuk);
//        builder.setPositiveButton(R.string.no,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
////                        Intent intent = new Intent(mContext, MyFeedListActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        mContext.startActivity(intent);
//                    }
//                });
//        builder.setNegativeButton(R.string.yes,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
////                        Toast.makeText(getActivity().getApplicationContext(), R.string.make_new_godmuk, Toast.LENGTH_LONG).show();
////                        Intent intent = new Intent(getActivity(), ReservActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        getActivity().startActivity(intent);
//
//                        if (feedReqList.size() > 0) {
//
//                            ((MyFeedListActivity) mContext).txt_no_feed.setVisibility(View.GONE);
//                            ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.GONE);
//                            ((MyFeedListActivity) mContext).recyclerView_myFeed.setVisibility(View.VISIBLE);
//
//                            ((MyFeedListActivity) mContext).myFeedListAdapter = new MyFeedListAdapter(mContext, httpClient, feedReqList);
//                            ((MyFeedListActivity) mContext).recyclerView_myFeed.setAdapter(((MyFeedListActivity) mContext).myFeedListAdapter);
//                            ((MyFeedListActivity) mContext).myFeedListAdapter.notifyDataSetChanged();
//                        } else {
//                            ((MyFeedListActivity) mContext).txt_no_feed.setVisibility(View.VISIBLE);
//                            ((MyFeedListActivity) mContext).btn_go_reserv.setVisibility(View.VISIBLE);
//                            ((MyFeedListActivity) mContext).recyclerView_myFeed.setVisibility(View.GONE);
//                        }
//                    }
//                });
//        builder.show();
        }
    }

}