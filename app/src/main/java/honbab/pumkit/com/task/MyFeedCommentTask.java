package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedCommentAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.tete.CommentTalkActivity;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedCommentTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private ArrayList<FeedReqData> feedReqList = new ArrayList<>();
    private String result;
    private String rest_name;

    public MyFeedCommentTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        feedReqList.clear();
    }

    @Override
    protected ArrayList<FeedReqData> doInBackground(Void... params) {
        ArrayList<FeedReqData> feedReqList = new ArrayList<>();

        FormBody body = new FormBody.Builder()
                .add("opt", "my_feed_comment_list")
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "my_feed_list = " + obj);

                result = obj.getString("result");

                if (result.equals("0")) {
                    JSONArray feedArr = obj.getJSONArray("feed");
                    for (int i = 0; i < feedArr.length(); i++) {
                        ArrayList<UserData> reqUsersList = new ArrayList<>();
                        ArrayList<CommentData> commentsList = new ArrayList<>();

                        JSONObject feedObj = feedArr.getJSONObject(i);

                        String feed_id = feedObj.getString("sid");
                        String status = feedObj.getString("status");
                        String feed_time = feedObj.getString("time");

                        JSONObject hostObj = feedObj.getJSONObject("host");
                        String host_id = hostObj.getString("sid");
                        String host_name = hostObj.getString("name");
                        String host_img = hostObj.getString("img_url");

                        JSONObject restObj = feedObj.getJSONObject("rest");
                        String rest_id = restObj.getString("sid");
                        rest_name = restObj.getString("name");
                        Double lat = Double.parseDouble(restObj.getString("lat"));
                        Double lng = Double.parseDouble(restObj.getString("lng"));
                        String rest_img = restObj.getString("img_url");

                        if (feedObj.has("comments")) {
                            JSONArray usersArr = feedObj.getJSONArray("comments");

                            for (int k = 0; k < usersArr.length(); k++) {
                                JSONObject userObj = usersArr.getJSONObject(k);

                                String comment_id = userObj.getString("sid");
                                String comment = userObj.getString("comment");
                                String comment_time = userObj.getString("time");

                                JSONObject c_user_obj = userObj.getJSONObject("user");
                                String c_user_id = c_user_obj.getString("sid");
                                String c_user_name = c_user_obj.getString("name");
                                String c_img_url = Statics.main_url + c_user_obj.getString("img_url");

                                CommentData commentData = new CommentData(comment_id, c_user_id, c_user_name, c_img_url, comment, comment_time);
                                commentsList.add(commentData);
                            }
                        }

                        FeedReqData data = new FeedReqData(feed_id, status, feed_time, rest_id, rest_name, rest_img, reqUsersList, commentsList);
                        feedReqList.add(data);
                    }
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return feedReqList;
    }

    @Override
    protected void onPostExecute(final ArrayList<FeedReqData> feedReqList) {
        super.onPostExecute(feedReqList);

        if (feedReqList.size() > 0) {
            ((CommentTalkActivity) mContext).title_reserve.setVisibility(View.VISIBLE);

            ((CommentTalkActivity) mContext).mAdapter = new MyFeedCommentAdapter(mContext, httpClient, feedReqList);
            ((CommentTalkActivity) mContext).recyclerView.setAdapter(((CommentTalkActivity) mContext).mAdapter);
            ((CommentTalkActivity) mContext).mAdapter.notifyDataSetChanged();
        } else {
            ((CommentTalkActivity) mContext).recyclerView.setVisibility(View.GONE);
            ((CommentTalkActivity) mContext).txt_no_comment.setVisibility(View.VISIBLE);
        }
    }

}