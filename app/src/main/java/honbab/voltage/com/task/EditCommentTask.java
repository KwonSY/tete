package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class EditCommentTask extends AsyncTask<Void, Void, String> {

    private OkHttpClient httpClient;
    private Context mContext;

    int seq;
    String comment;

    public EditCommentTask(Context mContext, OkHttpClient httpClient,
                           String comment, int seq) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.comment = comment;
        this.seq = seq;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Void... objects) {
        String result = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "update_profile")
                .add("my_id", Statics.my_id)
                .add("comment", comment)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "EditCommentTask = " + bodyStr);

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

            if (result.equals("0")) {
                //코멘트 다시 로드
            }
    }

}