package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.tete.CommentTalkActivity;
import honbab.pumkit.com.tete.MyFeedListActivity;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedCancleTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String feed_id;
    private String result;

    public FeedCancleTask(Context mContext, OkHttpClient httpClient, String feed_id) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.feed_id = feed_id;
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

        if (result.equals("0")) {
            if (mContext.equals(MyFeedListActivity.class)) {
                new MyFeedListTask(mContext, httpClient).execute();
//                ((MyFeedListActivity) mContext).recyclerView_myFeed.setAdapter(user_name);
//                ((MyFeedListActivity) mContext).myFeedListAdapter.notifyDataSetChanged();
            } else if (mContext.equals(CommentTalkActivity.class)) {
                new MyFeedCommentTask(mContext, httpClient).execute();
//                ((CommentTalkActivity) mContext).mAdapter = new MyFeedCommentAdapter(mContext, httpClient, feedReqList);
//
//                ((CommentTalkActivity) mContext).recyclerView.setAdapter(user_name);
//                ((CommentTalkActivity) mContext).mAdapter.notifyDataSetChanged();
            }
//            new MyFeedListTask(mContext, httpClient, feed_id).execute();

        }
    }

}