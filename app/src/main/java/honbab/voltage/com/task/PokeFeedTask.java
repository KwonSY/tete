package honbab.voltage.com.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PokeFeedTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    String process;
    private RestData restData;
    private UserData userData;

    public PokeFeedTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        String result = "1";
        Log.e("abc", "host_id : " + objects[1] + ", rest_id = " + objects[0] + ", feed_time" + objects[2]);
        FormBody body = new FormBody.Builder()
                .add("opt", "poke")
                .add("my_id", Statics.my_id)
                .add("host_id", objects[1])
                .add("rest_id", objects[0])
                .add("feed_time", objects[2])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");

                process = obj.getString("process");

                JSONObject user_obj = obj.getJSONObject("user");
                String user_id = user_obj.getString("sid");
                String user_name = user_obj.getString("name");
                String user_age = user_obj.getString("age");
                String user_gender = user_obj.getString("gender");
                String user_token = user_obj.getString("token");
                String user_img;
                if (user_obj.getString("img_url").contains("http")) {
                    user_img = user_obj.getString("img_url");
                } else {
                    user_img = Statics.main_url + user_obj.getString("img_url");
                }
                userData = new UserData(user_id, user_name, user_age, user_gender, user_token, user_img, "", "");

                JSONObject rest_obj = obj.getJSONObject("rest");
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

                restData = new RestData(rest_id, rest_name,
                        compound_code, latLng, place_id, rest_img, rest_phone, vicinity, 0);
            } else {
                Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            if (result.equals("0")) {
                if (process.equals("already")) {
                    Toast.makeText(mContext, "앗! 그 사이에 이미 다른 분을 수락하였습니다. 새로고침하시고 다른 분을 찾아보세요.", Toast.LENGTH_SHORT).show();
                } else if (process.equals("okay")) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("fromId", Statics.my_id);
                    intent.putExtra("toId", userData.getUser_id());
                    intent.putExtra("toUserName", userData.getUser_name());
                    intent.putExtra("toUserImg", userData.getImg_url());
                    intent.putExtra("toToken", userData.getToken());
                    intent.putExtra("restData", restData);
                    mContext.startActivity(intent);
                } else if (process.equals("my")) {
                    Toast.makeText(mContext, "이 분께서 먼저 <같이먹어요> 신청을 하셨네요. 채팅방으로 이동합니다.", Toast.LENGTH_SHORT).show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            finish();
//                            Intent intent2 = new Intent(DelayHandlerActivity.this, MainActivity.class);
//                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent2.putExtra("position", 1);
//                            startActivity(intent2);
                            Intent intent = new Intent(mContext, ChatActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("fromId", Statics.my_id);
                            intent.putExtra("toId", userData.getUser_id());
                            intent.putExtra("toUserName", userData.getUser_name());
                            intent.putExtra("toUserImg", userData.getImg_url());
                            intent.putExtra("toToken", userData.getToken());
                            intent.putExtra("restData", restData);
                            mContext.startActivity(intent);
                        }
                    }, 1800);

                } else {
                    Toast.makeText(mContext, "다시 시도해보세요.", Toast.LENGTH_SHORT).show();
                }
//                new RestLikeListTask(mContext, httpClient).execute();
//                new MyFeedListTask(mContext, httpClient). execute();
            }
        }

        Log.e("abc", "PokeFeed = " + mContext.getClass());
        if (mContext.getClass().equals(MainActivity.class)) {
            if (result.equals("0")) {
//                ((OneRestaurantActivity) mContext).btn_poke.setClickable(true);
//
//                if (process.equals("insert")) {
//                    ((OneRestaurantActivity) mContext).btn_poke.setText(R.string.poke_reserved);
//                    ((OneRestaurantActivity) mContext).btn_poke.setBackgroundResource(R.drawable.border_circle_or2);
//                } else if (process.equals("delete")) {
//                    ((OneRestaurantActivity) mContext).btn_poke.setText(R.string.poke_reserve);
//                    ((OneRestaurantActivity) mContext).btn_poke.setBackgroundResource(R.drawable.border_circle_bk1);
//                }

            } else {
//                ((OneRestaurantActivity) mContext).btn_poke.setClickable(false);
            }
        }
    }

}