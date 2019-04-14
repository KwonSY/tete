package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PickAreaTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    private String result;

    public PickAreaTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @Override
    protected String doInBackground(String... params) {
//        datetime = params[1];
        Log.e("abc", "PickDateTask datetime = " + params[0]);
        FormBody body = new FormBody.Builder()
                .add("opt", "pick_area")
                .add("my_id", Statics.my_id)
                .add("timelike_id", params[0])
                .add("area_cd", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab1/index.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "PickAreaTask bodyStr = " + bodyStr);
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

            }
        }
    }

}