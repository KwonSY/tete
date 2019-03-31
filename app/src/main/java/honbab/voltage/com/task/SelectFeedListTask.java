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
import honbab.voltage.com.widget.PickRestDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SelectFeedListTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;

    private int split;
    private int cnt_rest_arr;
    private String area_cd;
    private String loadStatus = "";
    private ArrayList<SelectDateData> datePickedList = new ArrayList<>();
    private ArrayList<RestData> restList = new ArrayList<>();
    private ArrayList<UserData> userList = new ArrayList<>();

    public SelectFeedListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

        datePickedList.clear();
        restList.clear();
        userList.clear();

        ((SelectFeedFragment) fragment).dateLikeList.clear();
        ((SelectFeedFragment) fragment).restLikeList.clear();
        ((SelectFeedFragment) fragment).restLikeList.clear();
    }

    @Override
    protected String doInBackground(String... params) {
        loadStatus = params[3];
        Log.e("abc", "SelectFeedListTask Statics.my_id = " + Statics.my_id);

        Calendar curCal = Calendar.getInstance();
        Date curDate = curCal.getTime();

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        FormBody body = new FormBody.Builder()
                .add("opt", "select_feed_list")
                .add("my_id", Statics.my_id)
                .add("datetime", params[0])
                .add("area_cd", params[1])
                .add("rest_id", params[2])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab1/index.php").post(body).build();
//        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "SelectFeedListTask obj : " + obj);

                area_cd = obj.getString("area_cd");
                split = obj.getInt("split");

                JSONArray time_arr = obj.getJSONArray("times");
                for (int i = 0; i < time_arr.length(); i++) {
                    JSONObject time_obj = time_arr.getJSONObject(i);
                    String time = time_obj.getString("time");
                    Date d_time = formatter1.parse(time);
                    String[] timeArr = time.split(" ");
                    String onlyTime = timeArr[1];
                    String timeName = "";
                    if (onlyTime.equals("15:00:00"))
                        timeName = "점심";
                    else if (onlyTime.equals("21:00:00"))
                        timeName = "저녁";
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
                    String status = time_obj.getString("status");

                    SelectDateData dateData = new SelectDateData(time, timeName, day_of_week, cnt_time, status);
                    ((SelectFeedFragment) fragment).dateLikeList.add(dateData);
                    if (dateData.getStatus().equals("y") && d_time.after(curDate))
                        datePickedList.add(dateData);
                }

                JSONArray rest_arr = obj.getJSONArray("rests");
                cnt_rest_arr = rest_arr.length();
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

                JSONArray user_arr = obj.getJSONArray("users");
                for (int i = 0; i < user_arr.length(); i++) {
                    JSONObject user_obj = user_arr.getJSONObject(i);
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String age = user_obj.getString("age");
                    String gender = user_obj.getString("gender");
                    String token = user_obj.getString("token");
                    String user_img = user_obj.getString("img_url");
                    String comment = user_obj.getString("comment");

                    UserData userData = new UserData(user_id, user_name, age, gender, token, user_img, null);
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
        return area_cd;
    }

    @Override
    protected void onPostExecute(final String area_cd) {
        super.onPostExecute(area_cd);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            ((SelectFeedFragment) fragment).area_cd = area_cd;
            ((SelectFeedFragment) fragment).split = split;
            Log.e("abc", "((SelectFeedFragment) fragment).spinnerAdapter.getCount() = " + ((SelectFeedFragment) fragment).spinnerAdapter.getCount());

            if (cnt_rest_arr == 0 && ((SelectFeedFragment) fragment).spinnerAdapter.getCount() > 1) {
                PickRestDialog dialog = new PickRestDialog(mContext);
                dialog.callFunction(null);

//                Log.e("abc", "((SelectFeedFragment) fragment).area_cd = " + ((SelectFeedFragment) fragment).area_cd);
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage("이 지역 음식점을 둘러보시겠어요?");
//                builder.setPositiveButton("음식점 둘러보기",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(mContext, GodTinderActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("area_cd", ((SelectFeedFragment) fragment).area_cd);
//                                mContext.startActivity(intent);
//                            }
//                        });
////                builder.setNegativeButton(R.string.no,
////                        new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int which) {
//////                                holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_n);
////                            }
////                        });
//                builder.show();
            }

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

                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
                ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();

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
                ((SelectFeedFragment) fragment).recyclerView_date.setAdapter(((SelectFeedFragment) fragment).mAdapter_date);
                ((SelectFeedFragment) fragment).mAdapter_date.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList);
                ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
                ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).mAdapter_user.clearItemList();
                ((SelectFeedFragment) fragment).mAdapter_user = new SelectUserListAdapter(mContext, userList);
                ((SelectFeedFragment) fragment).recyclerView_user.setAdapter(((SelectFeedFragment) fragment).mAdapter_user);
                ((SelectFeedFragment) fragment).mAdapter_user.notifyDataSetChanged();

                ((SelectFeedFragment) fragment).swipeContainer.setRefreshing(false);
            }
        }
    }

}