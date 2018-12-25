package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UpdateRestPhoneTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    String result, rest_id, rest_phone;

    public UpdateRestPhoneTask(Context mContext, OkHttpClient httpClient, String[] date, String[] rest) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "updateRestPhone")
                .add("rest_id", rest_id)
                .add("phone", rest_phone)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "UpdateRestPhoneTask bodyStr = " + bodyStr);

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
        }
    }

}