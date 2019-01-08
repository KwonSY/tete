package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OneDayFeedCheckTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;
    int cnt = 0;

    public OneDayFeedCheckTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... objects) {
        Log.e("abc", "Error : " + objects[0] + " , "+ objects[1]);

        FormBody body = new FormBody.Builder()
                .add("opt", "oneday_feed_check")
                .add("my_id", Statics.my_id)
                .add("location", objects[0])
                .add("date", objects[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "OneDayFeedCheckTask  : " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
                if (result.equals("0")) {
                    cnt = obj.getInt("cnt");

                    JSONArray feedArr = obj.getJSONArray("feed");
                }
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("DelayBefroePickRestActivity")) {
            if (cnt > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.already_reserved_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(mContext, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(intent);
                                ((Activity) mContext).finish();
                            }
                        });
                builder.show();
            }
        }

    }
}