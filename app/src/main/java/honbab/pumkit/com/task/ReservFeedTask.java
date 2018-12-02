package honbab.pumkit.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.tete.MainActivity;
import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ReservFeedTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private OkHttpClient httpClient;

    String result;
    String date_reserv;
    String comment;
    String rest_name, place_id, lat, lng, rest_img;

    public ReservFeedTask(Context mContext, OkHttpClient httpClient, String[] date, String[] rest) {
        this.mContext = mContext;
        this.httpClient = httpClient;

        String year = date[0];
        String month = date[1];
        String day = date[2];
        String hour = date[3];
        String min = date[4];

        String str_year = String.valueOf(year);
        String str_mon = String.valueOf(month);
        String str_da = String.valueOf(day);
        String str_hour = String.valueOf(hour);
        String str_min = String.valueOf(min);
        date_reserv = str_year + "-" + str_mon + "-" + str_da + " " + str_hour + ":" + str_min;

        comment = ((OneRestaurantActivity) mContext).edit_comment.getText().toString();
//        comment = edit_comment.getText().toString();

        rest_name = rest[0];
        place_id = rest[1];
        lat = rest[2];
        lng = rest[3];
        rest_img = rest[4];
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "reservation")
                .add("my_id", Statics.my_id)
                .add("rest_name", rest_name)
                .add("place_id", place_id)
                .add("lat", lat)
                .add("lng", lng)
                .add("img_url", rest_img)
                .add("date_reserv", date_reserv)
                .add("comment", comment)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

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
            }
        }
    }

}