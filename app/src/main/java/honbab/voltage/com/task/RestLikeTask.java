package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestLikeTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private ArrayList<FeedData> feedList = new ArrayList<>();

    public RestLikeTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        FormBody body = new FormBody.Builder()
                .add("opt", "rest_like")
                .add("rest_id", params[0])
                .add("user_id", Statics.my_id)
                .add("like_yn", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        Log.e("abc", "result : " + result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("GodTinderActivity")) {
            if (result.equals("")) {
                //통과
            } else {
                //미통과 다시
            }
        }
    }

}