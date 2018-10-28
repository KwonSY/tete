package honbab.pumkit.com.tete;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedCommentAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CommentTalkActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    private TextView title_reserve;
    public RecyclerView recyclerView;
    public MyFeedCommentAdapter mAdapter;

    private ArrayList<FeedReqData> feedReqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commenttalk);

//        Intent intent = getIntent();
//        feedReqList = (ArrayList<FeedReqData>) intent.getSerializableExtra("feedList");

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        title_reserve = (TextView) findViewById(R.id.title_reserve);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyFeedCommentAdapter();
        recyclerView.setAdapter(mAdapter);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyFeedCommentTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
            }
        }
    };

    public class MyFeedCommentTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        String result;
//        String host;
        String rest_name;

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
                    Log.e("abc", "my_feed_comment_list = " + obj);

                    result = obj.getString("result");

                    if (result.equals("0")) {
//                        if (!obj.getJSONObject("host").isNull("") ) {
                        JSONArray feedArr = obj.getJSONArray("feed");
                        for (int i = 0; i < feedArr.length(); i++) {
                            ArrayList<UserData> reqUsersList = new ArrayList<>();
                            ArrayList<CommentData> commentsList = new ArrayList<>();

                            JSONObject feedObj = feedArr.getJSONObject(i);

                            String feed_id = feedObj.getString("sid");

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

                            FeedReqData data = new FeedReqData(feed_id, rest_id, rest_name, rest_img, reqUsersList, commentsList);
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
        protected void onPostExecute(ArrayList<FeedReqData> feedReqList) {
            super.onPostExecute(feedReqList);
            Log.e("abc", "onPostExecute feedReqList.size = " + feedReqList.size());

            if (feedReqList.size() > 0) {
                title_reserve.setVisibility(View.VISIBLE);

                mAdapter = new MyFeedCommentAdapter(CommentTalkActivity.this, httpClient, feedReqList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                title_reserve.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

        }

    }
}