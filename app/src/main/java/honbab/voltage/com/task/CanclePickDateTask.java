package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CanclePickDateTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;

    public CanclePickDateTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "cancle_pick_date")
                .add("my_id", Statics.my_id)
                .add("rest_ids", params[0])
                .add("datetime", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "PickDateTask bodyStr = " + bodyStr);
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
        if (activityName.equals("MainActivity")) {
            if (result.equals("0")) {
                Toast.makeText(mContext, R.string.cancle_reservation, Toast.LENGTH_SHORT).show();
            } else if (result.equals("1")) {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.cancle, Toast.LENGTH_SHORT).show();
            }
        }
//        else if (activityName.equals("ReservActivity")) {
//            if (result.equals("0")) {
//                Intent intent2 = new Intent(mContext, MainActivity.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                mContext.startActivity(intent2);
//                ((Activity) mContext).finish();
//            } else {
//                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//            }
//        } else if (activityName.equals("ChatActivity")) {
//            if (result.equals("0")) {
//                Toast.makeText(mContext, "식사가 예약되었습니다.", Toast.LENGTH_SHORT).show();
//
////                Intent intent2 = new Intent(mContext, MainActivity.class);
////                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                mContext.startActivity(intent2);
////                ((Activity) mContext).finish();
//            } else {
//                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//            }
//        }
    }

}