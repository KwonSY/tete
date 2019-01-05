package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.fragment.MyFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CancleFeedTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String rest_name;
    private int position;

    private String result;

    public CancleFeedTask(Context mContext, OkHttpClient httpClient, int position) {
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
                ((MyFeedFragment) fragment).mAdapter.removeAt(position);
            } else if (activityName.equals("MyFeedListActivity")) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage(R.string.ask_cancle_godmuk);
//                builder.setPositiveButton(R.string.yes,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Resources res = mContext.getResources();
//                                String text = String.format(res.getString(R.string.cancle_godmuk), rest_name);
//                                Toast.makeText(mContext.getApplicationContext(), text, Toast.LENGTH_LONG).show();
//
//
//                                new MyFeedListTask2(mContext, httpClient).execute();
//                            }
//                        });
//                builder.setNegativeButton(R.string.no,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        });
//                builder.show();
            }

        }
    }
}