package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.SelectDateListAdapter;
import honbab.voltage.com.adapter.SelectRestListAdapter;
import honbab.voltage.com.adapter.SelectUserListAdapter;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SelectFeedListTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;

    private int split;
    private String area_cd;
    private String first_feedTime;
    private String loadStatus = "";
    private ArrayList<SelectDateData> dateList = new ArrayList<>();
    private ArrayList<RestData> restList = new ArrayList<>();
    private ArrayList<UserData> userList = new ArrayList<>();

    public SelectFeedListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

        dateList.clear();
        restList.clear();
        userList.clear();

        ((SelectFeedFragment) fragment).dateLikeList.clear();
        ((SelectFeedFragment) fragment).restLikeList.clear();
        ((SelectFeedFragment) fragment).restLikeList.clear();
    }

    @Override
    protected String doInBackground(String... params) {
        loadStatus = params[3];

        FormBody body = new FormBody.Builder()
                .add("opt", "select_feed_list")
                .add("my_id", Statics.my_id)
                .add("datetime", params[0])
                .add("area_cd", params[1])
                .add("rest_id", params[2])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "SelectFeedListTask obj : " + obj);

                area_cd = obj.getString("area_cd");
                split = obj.getInt("split");

                JSONArray time_arr = obj.getJSONArray("time");
                for (int i = 0; i < time_arr.length(); i++) {
                    JSONObject time_obj = time_arr.getJSONObject(i);
                    String time = time_obj.getString("time");
                    int cnt_time = time_obj.getInt("cnt");
                    String status = time_obj.getString("status");

                    SelectDateData dateData = new SelectDateData(time, cnt_time, status);
                    ((SelectFeedFragment) fragment).dateLikeList.add(dateData);
                    if (dateData.getStatus().equals("y") || dateData.getStatus().equals("a"))
                        dateList.add(dateData);

                    if (i == 0)
                        first_feedTime = time;
                }

                JSONArray rest_arr = obj.getJSONArray("rest");
                for (int i = 0; i < rest_arr.length(); i++) {
                    JSONObject rest_obj = rest_arr.getJSONObject(i);
                    String rest_id = rest_obj.getString("sid");
                    String rest_name = rest_obj.getString("name");
                    String compound_code = rest_obj.getString("compound_code");
                    String vicinity = rest_obj.getString("vicinity");
                    String place_id = rest_obj.getString("place_id");
                    double lat = rest_obj.getDouble("lat");
                    double lng = rest_obj.getDouble("lng");
                    String phone = rest_obj.getString("phone");
                    String rest_img = rest_obj.getString("img_url");
                    String like_yn = rest_obj.getString("like_yn");
                    int cnt = rest_obj.getInt("cnt");
//                    Log.e("abc", i + ", ((SelectFeedFragment) fragment).feed_rest_id = " + ((SelectFeedFragment) fragment).feed_rest_id + " / " + rest_id);

                    RestData restData = new RestData(rest_id, rest_name, compound_code, new LatLng(lat, lng), place_id, rest_img, phone, vicinity, cnt);
                    if (((SelectFeedFragment) fragment).feed_rest_id.equals(rest_id))
                        restData.setChecked(true);
                    else
                        restData.setChecked(false);
                    restData.setLike_yn(like_yn);
                    if (like_yn.equals("y")) {
                        restList.add(restData);
                        ((SelectFeedFragment) fragment).restLikeList.add(rest_id);
                    }
                }

                JSONArray user_arr = obj.getJSONArray("user");
                for (int i = 0; i < user_arr.length(); i++) {
                    JSONObject user_obj = user_arr.getJSONObject(i);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String age = user_obj.getString("age");
                    String gender = user_obj.getString("gender");
                    String token = user_obj.getString("token");
                    String user_img = Statics.main_url + user_obj.getString("img_url");

                    UserData userData = new UserData(user_id, user_name, age, gender, token, user_img, null);
                    userList.add(userData);
                }

            } else {
                    Log.e("abc", "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return area_cd;
    }

    @Override
    protected void onPostExecute(final String area_cd) {
        super.onPostExecute(area_cd);
//        Log.e("abc", loadStatus + " : " + ((SelectFeedFragment) fragment).feed_time + ", feed_rest_id = " + ((SelectFeedFragment) fragment).feed_rest_id);
        Log.e("abc", "SelectFeedListTask mContext = " + mContext);
        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            ((SelectFeedFragment) fragment).area_cd = area_cd;
            ((SelectFeedFragment) fragment).split = split;

            if (loadStatus.equals("readOnlyUser")) {
                ((SelectFeedFragment) fragment).to_id = "";
                if (((SelectFeedFragment) fragment).feed_rest_id.equals(""))
                    ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();
            } else if (loadStatus.equals("readBelowRest")) {
//                ((SelectFeedFragment) fragment).feed_rest_id = "";
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);

                if (((SelectFeedFragment) fragment).areaNameList.size() == 1)
                    new AreaRestTask(mContext).execute();
            } else {
                ((SelectFeedFragment) fragment).feed_time = "";
                ((SelectFeedFragment) fragment).feed_rest_id = "";
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_date.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_date = new SelectDateListAdapter(mContext, dateList);
                ((SelectFeedFragment) fragment).recyclerView_date.setAdapter(((SelectFeedFragment) fragment).mAdapter_date);

                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).swipeContainer.setRefreshing(false);

                if (((SelectFeedFragment) fragment).areaNameList.size() == 1)
                    new AreaRestTask(mContext).execute();
            }
        }
    }

}