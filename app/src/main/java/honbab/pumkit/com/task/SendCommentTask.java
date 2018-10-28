package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.pumkit.com.tete.CommentTalkActivity;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SendCommentTask extends AsyncTask<String, Void, String> {
    private OkHttpClient httpClient;
    private Context mContext;
    private int position;

    public SendCommentTask(Context mContext, OkHttpClient httpClient, int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
//        Log.e("abc", "xxxxviewHolderxxxx1  viewHolder = " + viewHolder);
    }

    @Override
    protected String doInBackground(String... objects) {
        String result = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "send_comment")
                .add("my_id", Statics.my_id)
                .add("feed_id", objects[0])
                .add("comment", objects[1])
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
//                ((CommentTalkActivity) mContext).recyclerView.removeItemDecorationAt(position);
                ((CommentTalkActivity) mContext).mAdapter.notifyDataSetChanged();

//                Intent intent = new Intent(mContext, CommentTalkActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("feedList", 1);
//                mContext.startActivity(intent);
            }

    }

}