package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ReservFeedTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;
    private String to_id;
//    private String datetime;
//    private RestData restData;
//    String lat, lng, place_id, rest_name, rest_phone, rest_img, compound_code, vicinity;

    public ReservFeedTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        to_id = params[0];
//        Log.e("abc", "ReservTask date_reserv = " + params[1]);
        FormBody body = new FormBody.Builder()
                .add("opt", "reserv_feed")
                .add("my_id", Statics.my_id)
                .add("to_id", params[0])
                .add("rest_id", params[1])
                .add("datetime", params[2])
//                .add("comment", comment)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "ReservTask bodyStr = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
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
        if (activityName.equals("OneRestaurantActivity")) {
            if (result.equals("0")) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }
        } else if (activityName.equals("ReservActivity")) {
            if (result.equals("0")) {
                Intent intent2 = new Intent(mContext, MainActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent2);
                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }
        } else if (activityName.equals("ChatActivity")) {
            if (result.equals("0")) {
                Toast.makeText(mContext, "식사가 예약되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }
        } else if (activityName.equals("MainActivity")) {
            if (result.equals("0")) {
                Toast.makeText(mContext, "식사가 예약되었습니다.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fromId", Statics.my_id);
                intent.putExtra("toId", to_id);
//                intent.putExtra("toUserName", data.getUser_name());
//                intent.putExtra("toUserImg", data.getUser_img());
//                intent.putExtra("toToken", data.getToken());
//                intent.putExtra("restData", restData);
                mContext.startActivity(intent);
//                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, R.string.cannot_reserve, Toast.LENGTH_SHORT).show();
            }
        }
    }

}