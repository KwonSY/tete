package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UpdateTokenTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;

    public UpdateTokenTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {
        Log.e("abc", "UpdateTokenTask params[0] = " + params[0]);
        FormBody body = new FormBody.Builder()
                .add("opt", "update_token")
                .add("my_id", Statics.my_id)
                .add("token", params[0])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "UpdateTokenTask : ");
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
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

    }

}