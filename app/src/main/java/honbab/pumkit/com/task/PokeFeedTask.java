package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PokeFeedTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private OkHttpClient httpClient;

//    String feed_id;

    String result;

    public PokeFeedTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
//        this.feed_id = feed_id;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "poke")
                .add("my_id", Statics.my_id)
                .add("feed_id", objects[0])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "bodyStr = " + bodyStr);

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
//                Intent intent = new Intent(OneFeedActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                mContext.startActivity(intent);
                ((OneRestaurantActivity) mContext).btn_poke.setText("예약완료");
                ((OneRestaurantActivity) mContext).btn_poke.setClickable(true);
            }
    }

}