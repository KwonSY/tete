package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.fragment.RestLikeFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CancleRestLikeTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String result;
    private String rest_name;
    private int position;

    public CancleRestLikeTask(Context mContext, OkHttpClient httpClient, int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... objects) {
        rest_name = objects[1];

        FormBody body = new FormBody.Builder()
                .add("opt", "cancle_rest_like")
                .add("my_id", Statics.my_id)
                .add("rest_id", objects[0])
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
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(result);

        if (result.equals("0")) {
            String activityName = mContext.getClass().getSimpleName();

            if (activityName.equals("MainActivity")) {
                FragmentManager fm = ((MainActivity) mContext).getSupportFragmentManager();
                Fragment fragment = fm.getFragments().get(0);
                ((RestLikeFragment) fragment).mAdapter.removeAt(position);
            }

        }
    }
}