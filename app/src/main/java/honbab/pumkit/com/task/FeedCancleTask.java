package honbab.pumkit.com.task;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.pumkit.com.tete.MyFeedListActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedCancleTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String feed_id;
    private String rest_name;
    private String result;

    public FeedCancleTask(Context mContext, OkHttpClient httpClient, String feed_id, String rest_name) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.feed_id = feed_id;
        this.rest_name = rest_name;
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

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "FeedCancleTask : " + obj);

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
        Log.e("abc", "onPostE = " + result);
        Log.e("abc", "mContext = " + mContext);

        if (result.equals("0")) {
            String activityName = mContext.getClass().getSimpleName();

            if (activityName.equals("MyFeedListActivity")) {
//                MyFeedListActivity mActivity = new MyFeedListActivity();

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.ask_cancle_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Resources res = mContext.getResources();
                                String text = String.format(res.getString(R.string.cancle_godmuk), rest_name);
                                Toast.makeText(mContext.getApplicationContext(), text, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(mContext, MyFeedListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                mContext.startActivity(intent);
//                                new MyFeedListTask(mContext, httpClient).execute();
                            }
                        });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

//                                Intent intent = new Intent(mContext, ReservActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                mContext.startActivity(intent);
                            }
                        });
                builder.show();

//                new MyFeedListTask(mContext, httpClient).execute();
            } else if (activityName.equals("CommentTalkActivity")) {
                new MyFeedCommentTask(mContext, httpClient).execute();
//                ((CommentTalkActivity) mContext).mAdapter = new MyFeedCommentAdapter(mContext, httpClient, feedReqList);
//                ((CommentTalkActivity) mContext).recyclerView.setAdapter(user_name);
//                ((CommentTalkActivity) mContext).mAdapter.notifyDataSetChanged();
            }
//            new MyFeedListTask(mContext, httpClient, feed_id).execute();

        }
    }

}