package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PickDateTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    private String result, status, datetime;

    public PickDateTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @Override
    protected String doInBackground(String... params) {
        String feed_time = params[0];
//        datetime = params[1];
        Log.e("abc", "PickDateTask datetime = " + params[0]);
        FormBody body = new FormBody.Builder()
                .add("opt", "pick_date")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("datetime", params[0])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab1.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "PickDateTask bodyStr = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
                if (result.equals("0"))
                    status = obj.getString("status");
            } else {
                Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return feed_time;
    }

    @Override
    protected void onPostExecute(String feed_time) {
        super.onPostExecute(feed_time);

        String activityName = mContext.getClass().getSimpleName();
        if (activityName.equals("MainActivity")) {
            if (result.equals("0")) {
//                Log.e("abc", "datetime = " + datetime);
//                String time = datetime.substring(11, 13);
//                int i_time = Integer.parseInt(time);
//                if (i_time > 15)
//                    time = "점심";
//                else
//                    time = "저녁";
//                String text = String.format(mContext.getResources().getString(R.string.reservated_this_time), time);
//                Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();

//                if(((SelectFeedFragment) fragment).recyclerView_date.getAdapter()==null)
//                    ((SelectFeedFragment) fragment).recyclerView_date.setAdapter(((SelectFeedFragment) fragment).mAdapter_date);
//                else {
//                    ((SelectFeedFragment) fragment).mAdapter_date.updateData(myNewData);  //update adapter's data
//                    ((SelectFeedFragment) fragment).mAdapter_date.notifyDataSetChanged(); //notifies any View reflecting data to refresh
//                }

//                ((SelectFeedFragment) fragment).feed_time = feed_time;

                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                        ((SelectFeedFragment) fragment).area_cd,
                        ((SelectFeedFragment) fragment).feed_rest_id,
                        "");//readBelowRest
            } else if (result.equals("1")) {
                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, R.string.cancle, Toast.LENGTH_SHORT).show();
            }
        }
    }

}