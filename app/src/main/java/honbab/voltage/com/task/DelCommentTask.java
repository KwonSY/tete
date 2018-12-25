package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class DelCommentTask extends AsyncTask<String, Void, String> {

    private OkHttpClient httpClient;
    private Context mContext;
    private int position;

    public DelCommentTask(Context mContext, OkHttpClient httpClient, int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        String result = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "del_comment")
                .add("my_id", Statics.my_id)
                .add("feed_id", objects[0])
                .add("comment_id", objects[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "DelCommentTask = " + bodyStr);

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
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("position", 1);
//                mContext.startActivity(intent);
            }

    }

}