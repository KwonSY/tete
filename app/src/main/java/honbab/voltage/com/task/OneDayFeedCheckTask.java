package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import honbab.voltage.com.adapter.OneImageAdapter;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.DelayBefroePickRestActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OneDayFeedCheckTask extends AsyncTask<String, Void, Integer> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;
    private String feed_time, today_status = "n";

    private ArrayList<RestData> restList = new ArrayList<>();

    public OneDayFeedCheckTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        restList.clear();
    }

    @Override
    protected Integer doInBackground(String... objects) {
        int cnt = 0;
        Log.e("abc", "my_id = " + Statics.my_id);
//        Log.e("abc", "area = " + objects[0]);
        Log.e("abc", "date = " + objects[0]);

        FormBody body = new FormBody.Builder()
                .add("opt", "oneday_feed_check")
                .add("my_id", Statics.my_id)
//                .add("area", objects[0])
                .add("date", objects[0])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "OneDayFeedCheckTask  : " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
                cnt = obj.getInt("cnt");

                if (result.equals("0")) {
                    JSONArray feedArr = obj.getJSONArray("feed");
                    for (int i=0; i<feedArr.length(); i++) {
                        JSONObject feed_obj = feedArr.getJSONObject(i);

                        String feed_id = feed_obj.getString("sid");
                        feed_time = feed_obj.getString("time");
                        String status = feed_obj.getString("status");
                        if (status.equals("y"))
                            today_status = "y";

                        JSONObject rest_obj = feed_obj.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name");
                        String rest_img_url = rest_obj.getString("img_url");

                        RestData restData = new RestData();
                        restData.setRest_id(rest_id);
                        restData.setRest_name(rest_name);
                        restData.setRest_img(rest_img_url);
                        restList.add(restData);
                    }
                }
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return cnt;
    }

    @Override
    protected void onPostExecute(Integer cnt) {
        super.onPostExecute(cnt);

        Log.e("abc", "cnt = " + cnt + ", " + today_status);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("DelayBefroePickRestActivity")) {
            if (today_status.equals("y")) {
                ((DelayBefroePickRestActivity) mContext).today_status = today_status;

//                ((DelayBefroePickRestActivity) mContext).btn_go_pick_rest.setEnabled(false);
                ((DelayBefroePickRestActivity) mContext).btn_go_pick_rest.setBackgroundResource(R.drawable.border_circle_gr1);
            } else {
                ((DelayBefroePickRestActivity) mContext).today_status = today_status;

                Log.e("abc", "버튼이 켜진다. = " + cnt + today_status);
                ((DelayBefroePickRestActivity) mContext).btn_go_pick_rest.setEnabled(true);
                ((DelayBefroePickRestActivity) mContext).btn_go_pick_rest.setBackgroundResource(R.drawable.btn_circle_or1);
            }

            if (cnt > 0) {
                ((DelayBefroePickRestActivity) mContext).title_reserved_rest.setVisibility(View.VISIBLE);
                ((DelayBefroePickRestActivity) mContext).recyclerView.setVisibility(View.VISIBLE);

                try {
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("이미 dd일 aa h시" + "로 아래 음식점을 예약하셨습니다.\n시간을 갱신하시겠습니까?");
                    Date date = formatter1.parse(feed_time);
                    String str_feed_time = formatter2.format(date);

                    ((DelayBefroePickRestActivity) mContext).title_reserved_rest.setText(str_feed_time);

                    ((DelayBefroePickRestActivity) mContext).mAdapter = new OneImageAdapter(mContext, restList);
                    ((DelayBefroePickRestActivity) mContext).recyclerView.setAdapter(((DelayBefroePickRestActivity) mContext).mAdapter);
                    ((DelayBefroePickRestActivity) mContext).mAdapter.notifyDataSetChanged();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                ((DelayBefroePickRestActivity) mContext).title_reserved_rest.setVisibility(View.GONE);
                ((DelayBefroePickRestActivity) mContext).recyclerView.setVisibility(View.GONE);
            }
        }
    }

}