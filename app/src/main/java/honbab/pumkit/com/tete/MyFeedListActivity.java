package honbab.pumkit.com.tete;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedListAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedListActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    private TextView txt_no_feed;
    private Button btn_go_reserv;
    private RecyclerView recyclerView_myFeed;
    private MyFeedListAdapter myFeedListAdapter;

    ArrayList<FeedReqData> feedReqList = new ArrayList<>();
//    int cnt_my = 0, cnt_yours = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfeedlist);

//        Intent intent = getIntent();
//        feedReqList = (ArrayList<FeedReqData>) intent.getSerializableExtra("feedList");

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        txt_no_feed = (TextView) findViewById(R.id.txt_no_feed);
        btn_go_reserv = (Button) findViewById(R.id.btn_go_reserv);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_myFeed = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView_myFeed.setLayoutManager(layoutManager);
        myFeedListAdapter = new MyFeedListAdapter();
        recyclerView_myFeed.setAdapter(myFeedListAdapter);
//        if (feedReqList.size() > 0) {
//            myFeedListAdapter = new MyFeedListAdapter(MyFeedListActivity.this, httpClient, feedReqList);
//            recyclerView_myFeed.setAdapter(myFeedListAdapter);
//            myFeedListAdapter.notifyDataSetChanged();
//        } else {
//            recyclerView_myFeed.setVisibility(View.GONE);
//        }

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyFeedListTask().execute();
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

    public class MyFeedListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        String result;
        String host;
        String rest_name;

//        ArrayList<FeedReqData> feedReqList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
//            feedReqList.clear();
        }

        @Override
        protected ArrayList<FeedReqData> doInBackground(Void... params) {
            ArrayList<FeedReqData> feedReqList = new ArrayList<>();

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_feed_list")
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
//                        if (!obj.getJSONObject("host").isNull("") ) {
                        JSONArray feedArr = obj.getJSONArray("feed");
                        for (int i = 0; i < feedArr.length(); i++) {
                            ArrayList<UserData> reqUsersList = new ArrayList<>();
                            ArrayList<CommentData> commentsList = new ArrayList<>();

                            JSONObject feedObj = feedArr.getJSONObject(i);

                            String feed_id = feedObj.getString("sid");

//                            JSONObject hostObj = feedObj.getJSONObject("host");
//                            host = hostObj.getString("sid");

                            JSONObject restObj = feedObj.getJSONObject("rest");
                            String rest_id = restObj.getString("sid");
                            rest_name = restObj.getString("name");
                            Double lat = Double.parseDouble(restObj.getString("lat"));
                            Double lng = Double.parseDouble(restObj.getString("lng"));
                            String rest_img = restObj.getString("img_url");

                            if (feedObj.has("users")) {
                                JSONArray usersArr = feedObj.getJSONArray("users");

                                for (int j = 0; j < usersArr.length(); j++) {
                                    JSONObject userObj = usersArr.getJSONObject(j);

                                    String user_id = userObj.getString("sid");
                                    String user_name = userObj.getString("name");
                                    String user_img = Statics.main_url + userObj.getString("img_url");
                                    String age = userObj.getString("age");
                                    String gender = userObj.getString("gender");
//                                    String status = userObj.getString("status");
                                    String time = userObj.getString("time");

                                    UserData userData = new UserData(user_id, user_name, age, gender, user_img, null);
                                    reqUsersList.add(userData);
                                }
                            }

                            String status = feedObj.getString("status");

                            FeedReqData data = new FeedReqData(feed_id, status, rest_id, rest_name, rest_img, reqUsersList, commentsList);
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
                txt_no_feed.setVisibility(View.GONE);
                btn_go_reserv.setVisibility(View.GONE);
                recyclerView_myFeed.setVisibility(View.VISIBLE);

                myFeedListAdapter = new MyFeedListAdapter(MyFeedListActivity.this, httpClient, feedReqList);
                recyclerView_myFeed.setAdapter(myFeedListAdapter);
                myFeedListAdapter.notifyDataSetChanged();
            } else {
                txt_no_feed.setVisibility(View.VISIBLE);
                btn_go_reserv.setVisibility(View.VISIBLE);
                recyclerView_myFeed.setVisibility(View.GONE);
            }

        }

    }
}