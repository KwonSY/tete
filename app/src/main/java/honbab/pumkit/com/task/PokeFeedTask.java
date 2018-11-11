package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.PokeListActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PokeFeedTask extends AsyncTask<String, Void, String> {

    private Context mContext;
    private OkHttpClient httpClient;

//    String result;
    String process;

    public PokeFeedTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        String result = "1";

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

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
                process = obj.getString("process");
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

        Log.e("abc", "PokeFeed = " + mContext.getClass());
        if (mContext.getClass().equals(OneRestaurantActivity.class)) {
            if (result.equals("0")) {
                ((OneRestaurantActivity) mContext).btn_poke.setClickable(true);

                if (process.equals("insert")) {
                    ((OneRestaurantActivity) mContext).btn_poke.setText(R.string.poke_reserved);
                    ((OneRestaurantActivity) mContext).btn_poke.setBackgroundResource(R.drawable.border_circle_gr2);
                } else if (process.equals("delete")) {
                    ((OneRestaurantActivity) mContext).btn_poke.setText(R.string.poke_reserve);
                    ((OneRestaurantActivity) mContext).btn_poke.setBackgroundResource(R.drawable.border_circle_bk1);
                }
            } else {
                ((OneRestaurantActivity) mContext).btn_poke.setClickable(false);
            }
        } else if (mContext.getClass().equals(PokeListActivity.class)) {
            if (result.equals("0")) {

            } else {

            }
        }
    }

}