package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.adapter.SelectDateListAdapter;
import honbab.voltage.com.adapter.SelectUserListAdapter;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import me.toptas.fancyshowcase.FancyShowCaseView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SelectFeedListTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;

    private int split;
    private int cnt_rest_arr;
//    private ArrayList<String> areasList;
//    private String area_cd;
    private String loadStatus = "";
    private ArrayList<SelectDateData> datePickedList = new ArrayList<>();
//    private ArrayList<RestData> restList = new ArrayList<>();
    private ArrayList<UserData> userList = new ArrayList<>();

    public SelectFeedListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity2) mContext).getSupportFragmentManager().findFragmentByTag("Match");
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

        datePickedList.clear();
//        restList.clear();
        userList.clear();

        ((SelectFeedFragment) fragment).dateAllList.clear();
//        ((SelectFeedFragment) fragment).restLikeList.clear();
//        ((SelectFeedFragment) fragment).userLikeList.clear();
    }

    @Override
    protected String doInBackground(String... params) {
        Log.e("abc", "params[0] = " + params[0]);
        Log.e("abc", "params[1] = " + params[1]);
        loadStatus = params[2];

        Calendar curCal = Calendar.getInstance();
        Date curDate = curCal.getTime();

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        FormBody body = new FormBody.Builder()
                .add("opt", "select_feed_list")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("datetime", params[0])
                .add("rest_id", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab1.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "SelectFeedListTask obj : " + obj);

//                JSONArray areasList = obj.getJSONArray("area_cds");
//                for (int i = 0; i<areasList.length(); i++) {
//                    String area_cd = areasList.get(i);
//                }

//                area_cd = obj.getString("area_cds");
                split = obj.getInt("split");

                // 예약시간
                JSONArray time_arr = obj.getJSONArray("times");
                for (int i = 0; i < time_arr.length(); i++) {
                    JSONObject time_obj = time_arr.getJSONObject(i);
                    String timelike_id = time_obj.getString("sid");
                    String time = time_obj.getString("time");
                    Date d_time = formatter1.parse(time);
                    String[] timeArr = time.split(" ");
                    String onlyTime = timeArr[1];
                    String timeName = "";
//                    if (onlyTime.equals("15:00:00"))
//                        timeName = "점심";
//                    else if (onlyTime.equals("21:00:00"))
//                        timeName = "저녁";
                    String day_of_week = time_obj.getString("day_of_week");
                    if (day_of_week.equals("Mon"))
                        day_of_week = "월";
                    else if (day_of_week.equals("Tue"))
                        day_of_week = "화";
                    else if (day_of_week.equals("Wed"))
                        day_of_week = "수";
                    else if (day_of_week.equals("Thu"))
                        day_of_week = "목";
                    else if (day_of_week.equals("Fri"))
                        day_of_week = "금";
                    else if (day_of_week.equals("Sat"))
                        day_of_week = "토";
                    else if (day_of_week.equals("Sun"))
                        day_of_week = "일";
                    else
                        day_of_week = "";
                    int cnt_time = time_obj.getInt("cnt");
                    Log.e("abc", "SelectFeedListTask cnt_time : " + cnt_time);

                    // 예약 음식점
                    JSONArray restArr = time_obj.getJSONArray("rests");
//                    Log.e("abc", "SelectFeedListTask restArr : " + restArr.toString());
                    ArrayList<RestData> restList = new ArrayList<>();

                    for (int j=0; j<restArr.length(); j++) {
                        JSONObject rest_obj = restArr.getJSONObject(j);
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
                        int sale = rest_obj.getInt("sale");
                        int cnt = rest_obj.getInt("cnt");

                        RestData restData = new RestData(rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity, sale, cnt);
                        restList.add(restData);
                    }

                    String status = time_obj.getString("status");

                    SelectDateData dateData = new SelectDateData(timelike_id, time, timeName, day_of_week, restList, cnt_time, status);
                    ((SelectFeedFragment) fragment).dateAllList.add(dateData);
                    if (dateData.getStatus().equals("y") && d_time.after(curDate))
                        datePickedList.add(dateData);
                }

                // 예약 대기 사용자
                JSONArray user_arr = obj.getJSONArray("users");
//                Log.e("abc", "SelectFeedListTask user_arr : " + user_arr.toString());
                for (int i = 0; i < user_arr.length(); i++) {
                    JSONObject user_obj = user_arr.getJSONObject(i);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String age = user_obj.getString("age");
                    String gender = user_obj.getString("gender");
                    String token = user_obj.getString("token");
                    String user_img = user_obj.getString("img_url");
                    String comment = user_obj.getString("comment");

                    UserData userData = new UserData(user_id, user_name, age, gender, token, user_img, null, null);
                    userData.setComment(comment);
                    userList.add(userData);
                }

            } else {
                    Log.e("abc", "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return "GNS1";
    }

    @Override
    protected void onPostExecute(final String area_cd) {
        super.onPostExecute(area_cd);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
//            ((SelectFeedFragment) fragment).area_cd = area_cd;
            ((SelectFeedFragment) fragment).split = split;
//            Log.e("abc", "((SelectFeedFragment) fragment).spinnerAdapter.getCount() = " + ((SelectFeedFragment) fragment).spinnerAdapter.getCount());
//
//            if (cnt_rest_arr == 0 && ((SelectFeedFragment) fragment).spinnerAdapter.getCount() > 1) {
//                PickRestDialog dialog = new PickRestDialog(mContext);
//                dialog.callFunction(null);


            if (loadStatus.equals("readOnlyUser")) {
                if (((SelectFeedFragment) fragment).feed_time.equals(""))
                    ((SelectFeedFragment) fragment).txt_explain_time.setText("");
                if (((SelectFeedFragment) fragment).feed_rest_id.equals(""))
                    ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");

                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();
            } else if (loadStatus.equals("readBelowRest")) {
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

//                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
//                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
//                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
//                ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();
            } else {
                ((SelectFeedFragment) fragment).feed_time = "";
                ((SelectFeedFragment) fragment).feed_rest_id = "";
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_time.setText("");
                ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_date.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_date = new SelectDateListAdapter(mContext, datePickedList);
                if (((SelectFeedFragment) fragment).mAdapter_date.getItemCount() == 1) {
                    new FancyShowCaseView.Builder(((MainActivity) mContext))
                            .title("시간을 선택해보세요.")
                            .focusOn(((SelectFeedFragment) fragment).recyclerView_date.getChildAt(0))
                            .build()
                            .show();
                }
                ((SelectFeedFragment) fragment).recyclerView_date.setAdapter(((SelectFeedFragment) fragment).mAdapter_date);
                ((SelectFeedFragment) fragment).mAdapter_date.notifyDataSetChanged();

//                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
//                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
//                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
//                ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).swipeContainer.setRefreshing(false);
            }
        } else if (activityName.equals("MainActivity2")) {
            ((SelectFeedFragment) fragment).split = split;
//            Log.e("abc", "((SelectFeedFragment) fragment).spinnerAdapter.getCount() = " + ((SelectFeedFragment) fragment).spinnerAdapter.getCount());
//
//            if (cnt_rest_arr == 0 && ((SelectFeedFragment) fragment).spinnerAdapter.getCount() > 1) {
//                PickRestDialog dialog = new PickRestDialog(mContext);
//                dialog.callFunction(null);


            if (loadStatus.equals("readOnlyUser")) {
                if (((SelectFeedFragment) fragment).feed_time.equals(""))
                    ((SelectFeedFragment) fragment).txt_explain_time.setText("");
                if (((SelectFeedFragment) fragment).feed_rest_id.equals(""))
                    ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");

                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();
            } else if (loadStatus.equals("readBelowRest")) {
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

//                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
//                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
//                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
//                ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();
            } else {
                ((SelectFeedFragment) fragment).feed_time = "";
                ((SelectFeedFragment) fragment).feed_rest_id = "";
                ((SelectFeedFragment) fragment).to_id = "";
                ((SelectFeedFragment) fragment).txt_explain_time.setText("");
                ((SelectFeedFragment) fragment).txt_explain_rest.setText("-");
                ((SelectFeedFragment) fragment).txt_explain_reserv.setText(R.string.explain_choose_feedee);

                ((SelectFeedFragment) fragment).mAdapter_date.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_date = new SelectDateListAdapter(mContext, datePickedList);
                if (((SelectFeedFragment) fragment).mAdapter_date.getItemCount() == 1) {
                    new FancyShowCaseView.Builder(((MainActivity2) mContext))
                            .title("시간을 선택해보세요.")
                            .focusOn(((SelectFeedFragment) fragment).recyclerView_date.getChildAt(0))
                            .build()
                            .show();
                }
                ((SelectFeedFragment) fragment).recyclerView_date.setAdapter(((SelectFeedFragment) fragment).mAdapter_date);
                ((SelectFeedFragment) fragment).mAdapter_date.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).swipeContainer.setRefreshing(false);
            }
        }
    }

}