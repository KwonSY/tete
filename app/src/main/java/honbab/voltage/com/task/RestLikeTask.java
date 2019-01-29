package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RestLikeTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private ArrayList<FeedData> feedList = new ArrayList<>();
    private Fragment fragment;

    private String area_cd, feed_time;
    private ArrayList<RestData> restList = new ArrayList<>();

    public RestLikeTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

        feed_time = ((SelectFeedFragment) fragment).feed_time;
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        FormBody body = new FormBody.Builder()
                .add("opt", "rest_like")
                .add("my_id", Statics.my_id)
                .add("rest_id", params[0])
                .add("like_yn", params[1])
                .add("datetime", feed_time)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "RestLikeTask = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
                area_cd = obj.getString("area_cd");

//                JSONArray rest_arr = obj.getJSONArray("rest");
//                Log.e("abc", "// rest_arr = " + rest_arr.length());
//                for (int i=0; i<rest_arr.length(); i++) {
//                    JSONObject rest_obj = rest_arr.getJSONObject(i);
//                    String rest_id = rest_obj.getString("sid");
//                    String rest_name = rest_obj.getString("name");
//                    String place_id = rest_obj.getString("place_id");
//                    String rest_img = rest_obj.getString("img_url");
//                    String like_yn = rest_obj.getString("like_yn");
//
//                    RestData restData = new RestData(rest_id, rest_name, null, null, place_id, rest_img, null, null);
//                    restData.setLike_yn(like_yn);
//                    restList.add(restData);
//                }


            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        Log.e("abc", "result : " + result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            ((SelectFeedFragment) fragment).area_cd = area_cd;

//            ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, restList, area_cd);
//            ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
        } else if (activityName.equals("GodTinderActivity")) {

        }
    }

}