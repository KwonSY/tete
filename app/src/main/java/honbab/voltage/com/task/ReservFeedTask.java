package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ReservFeedTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;
    private String date_reserv;
    private RestData restData;
//    String lat, lng, place_id, rest_name, rest_phone, rest_img, compound_code, vicinity;

    public ReservFeedTask(Context mContext, OkHttpClient httpClient, RestData restData) {
        this.mContext = mContext;
        this.httpClient = httpClient;

//        String year = date[0];
//        String month = date[1];
//        String day = date[2];
//        String hour = date[3];
//        String min = date[4];
//
//        String str_year = String.valueOf(year);
//        String str_mon = String.valueOf(month);
//        String str_da = String.valueOf(day);
//        String str_hour = String.valueOf(hour);
//        String str_min = String.valueOf(min);
//        date_reserv = str_year + "-" + str_mon + "-" + str_da + " " + str_hour + ":" + str_min;
//        Log.e("abc", "ReservTask date_reserv = " + date_reserv);

//        String activityName = mContext.getClass().getSimpleName();
//        if (activityName.equals("OneRestaurantActivity")) {
//            comment = ((OneRestaurantActivity) mContext).edit_comment.getText().toString();
//        } else if (activityName.equals("ReservActivity")) {
//            comment = ((ReservActivity) mContext).edit_comment.getText().toString();
//        } else if (activityName.equals("ChatActivity")) {
//            comment = ((ChatActivity) mContext).edit_comment.getText().toString();
//        }
        this.restData = restData;
//        rest_name = rest[0];
//        compound_code = rest[1];
//        lat = rest[2];
//        lng = rest[3];
//        place_id = rest[4];
//        rest_img = rest[5];
//        rest_phone = rest[6];
//        vicinity = rest[7];
//        Log.e("abc", "ReservTask lat = " + lat + lng);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        Log.e("abc", "ReservTask date_reserv = " + params[1]);
        FormBody body = new FormBody.Builder()
                .add("opt", "reservation")
                .add("my_id", Statics.my_id)
                .add("to_id", params[0])
                .add("rest_name", restData.getRest_name())
                .add("compound_code", restData.getCompound_code())
                .add("lat", String.valueOf(restData.getLatitude()))
                .add("lng", String.valueOf(restData.getLongtitue()))
                .add("place_id", restData.getPlace_id())
                .add("phone", restData.getRest_phone())
                .add("rest_img_url", restData.getRest_img())
                .add("vicinity", restData.getVicinity())
                .add("date_reserv", params[1])
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

//                Intent intent2 = new Intent(mContext, MainActivity.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                mContext.startActivity(intent2);
//                ((Activity) mContext).finish();
            } else {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            }
        }
    }

}