package honbab.pumkit.com.tete;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyPokeListAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PokeListActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    public RecyclerView recyclerView;
    public MyPokeListAdapter mAdapter;

    ArrayList<FeedReqData> feedReqList = new ArrayList<>();
//    int cnt_my = 0, cnt_yours = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokelist);

//        Intent intent = getIntent();
//        feedReqList = (ArrayList<FeedReqData>) intent.getSerializableExtra("feedList");

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new MyPokeListAdapter();
//        recyclerView.setAdapter(mAdapter);

//        if (feedReqList.size() > 0) {
//            mAdapter = new MyPokeListAdapter(PokeListActivity.this, httpClient, feedReqList);
//            recyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//        } else {
//            recyclerView.setVisibility(View.GONE);
//        }

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyPokeListTask().execute();
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

    public class MyPokeListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        String result;
        String rest_name;
//        String my_status;

        @Override
        protected void onPreExecute() {
//            feedReqList.clear();
        }

        @Override
        protected ArrayList<FeedReqData> doInBackground(Void... params) {
            ArrayList<FeedReqData> feedReqList = new ArrayList<>();

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_poke_list")
                    .add("my_id", Statics.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "MyPokeListTask = " + obj);

                    result = obj.getString("result");

                    if (result.equals("0")) {
//                        if (!obj.getJSONObject("host").isNull("") ) {
                        JSONArray feedArr = obj.getJSONArray("feed");
                        for (int i = 0; i < feedArr.length(); i++) {
                            ArrayList<UserData> reqUsersList = new ArrayList<>();
                            ArrayList<CommentData> commentsList = new ArrayList<>();


                            JSONObject feedObj = feedArr.getJSONObject(i);

                            String feed_id = feedObj.getString("sid");
                            String status = feedObj.getString("status");

                            JSONObject hostObj = feedObj.getJSONObject("host");
                            String host_id = hostObj.getString("sid");
                            String host_name = hostObj.getString("name");
                            String host_img = Statics.main_url + hostObj.getString("img_url");

                            JSONObject restObj = feedObj.getJSONObject("rest");
                            String rest_id = restObj.getString("sid");
                            rest_name = restObj.getString("name");
                            Double lat = Double.parseDouble(restObj.getString("lat"));
                            Double lng = Double.parseDouble(restObj.getString("lng"));
                            String rest_img = restObj.getString("img_url");

                            JSONArray usersArr = feedObj.getJSONArray("users");
                            for (int j=0; j<usersArr.length(); j++) {
                                String user_id = hostObj.getString("sid");
                                String user_name = hostObj.getString("name");
                                String user_img = Statics.main_url + hostObj.getString("img_url");

                                UserData userData = new UserData(user_id, user_name, null, null, user_img, "n");
                                reqUsersList.add(userData);
                            }

                            FeedReqData data = new FeedReqData(feed_id, status, host_id, host_name, host_img,
                                    rest_id, rest_name, rest_img, reqUsersList);
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
                recyclerView.setVisibility(View.VISIBLE);

                mAdapter = new MyPokeListAdapter(PokeListActivity.this, httpClient, feedReqList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
            }

        }

    }
}