package honbab.voltage.com.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.fragment.ProfileFragment;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedCancleTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String feed_id;
    private String rest_name;
    private int position;

    private String result;

    public FeedCancleTask(Context mContext, OkHttpClient httpClient,
                          String feed_id, String rest_name, int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.feed_id = feed_id;
        this.rest_name = rest_name;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "feed_cancle")
                .add("my_id", Statics.my_id)
                .add("feed_id", feed_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "FeedCancleTask : " + bodyStr);
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
                ProfileFragment.myFeedListAdapter.removeAt(position);
            } else if (activityName.equals("MyFeedListActivity")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.ask_cancle_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Resources res = mContext.getResources();
                                String text = String.format(res.getString(R.string.cancle_godmuk), rest_name);
                                Toast.makeText(mContext.getApplicationContext(), text, Toast.LENGTH_LONG).show();


                                new MyFeedListTask(mContext, httpClient).execute();
                            }
                        });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }

        }
    }
}