package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CancleFeedTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private int page;
    private int position;

    private String result;
    private String rest_name;

    public CancleFeedTask(Context mContext, int page, int position) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
        this.page = page;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... objects) {
        rest_name = objects[1];

        FormBody body = new FormBody.Builder()
                .add("opt", "cancle_feed")
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
            } else {
//                    Log.e(TAG, "Error : " + response.code() + ", " + response.message());
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
        Log.e("abc", "CancleFeedTask = " + page);

        if (result.equals("0")) {
            String activityName = mContext.getClass().getSimpleName();

            if (activityName.equals("MainActivity")) {
                if (page == 0) {

                } else if (page == 1) {
                    Fragment fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
                    ((MyFeedFragment) fragment).mAdapter.removeAt(position);

                    if (((MyFeedFragment) fragment).mAdapter.getItemCount() == 0)
                        new MyFeedListTask(mContext).execute();
                }
            }
        }

    }
}