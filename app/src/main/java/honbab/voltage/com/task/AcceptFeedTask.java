package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import honbab.voltage.com.adapter.ReqFeedeeAdapter;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.DelayHandlerActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AcceptFeedTask extends AsyncTask<String, Void, String> {
    private OkHttpClient httpClient;
    private Context mContext;

    ReqFeedeeAdapter.ViewHolder viewHolder;
    UserData userData;
    int feed_id, rest_id;
    String place_id;
    int position;

    public AcceptFeedTask(Context mContext, OkHttpClient httpClient, ReqFeedeeAdapter.ViewHolder viewHolder,
                          UserData userData, int feed_id, int rest_id, String place_id, int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.viewHolder = viewHolder;

        this.userData = userData;
        this.feed_id = feed_id;
        this.rest_id = rest_id;
        this.place_id = place_id;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        String result = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "accept_feed")
                .add("my_id", Statics.my_id)
                .add("feed_id", objects[0])
                .add("user_id", objects[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "AcceptFeedTask = " + bodyStr);

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
//                View view2 = (View) ((ViewGroup) v.getParent().getParent()).getChildAt(1);
//                int fragment_id = view2.getId();
                View rootView = viewHolder.itemView;

                Pair<View, String> pair1 = Pair.create(rootView.findViewById(R.id.img_feedee), "img_user");
                Pair<View, String> pair2 = Pair.create(rootView.findViewById(R.id.txt_feedee_name), "txt_userName");

                ActivityOptionsCompat optionsCompat;
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, pair1, pair2);
//                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((MyFeedListActivity) mContext, pair1, pair2);

                ((Activity) mContext).finish();
                Intent intent = new Intent(mContext, DelayHandlerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user_name", userData.getUser_name());
                intent.putExtra("user_img", userData.getImg_url());
                intent.putExtra("rest_id", rest_id);
                intent.putExtra("place_id", place_id);
                mContext.startActivity(intent, optionsCompat.toBundle());
            }

    }

}