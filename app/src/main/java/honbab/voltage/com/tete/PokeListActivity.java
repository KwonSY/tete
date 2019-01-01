package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.MyPokeListAdapter;
import honbab.voltage.com.data.CommentData;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
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

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(R.string.my_poke_list);

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
        Button btn_go_main = (Button) findViewById(R.id.btn_go_main);
        btn_go_main.setOnClickListener(mOnClickListener);

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
                case R.id.btn_go_main:
                    Intent intent = new Intent(PokeListActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
            }
        }
    };

    public class MyPokeListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        LinearLayout layout_no_poke;

        String result;
        String rest_name;
//        String my_status;

        @Override
        protected void onPreExecute() {
//            feedReqList.clear();
            layout_no_poke = (LinearLayout) findViewById(R.id.layout_no_poke);
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

                            int feed_id = feedObj.getInt("sid");
                            String status = feedObj.getString("status");
                            String feed_time = feedObj.getString("time");

                            JSONObject hostObj = feedObj.getJSONObject("host");
                            int host_id = hostObj.getInt("sid");
                            String host_name = hostObj.getString("name");
                            String host_img = Statics.main_url + hostObj.getString("img_url");

                            JSONObject restObj = feedObj.getJSONObject("rest");
                            int rest_id = restObj.getInt("sid");
                            rest_name = restObj.getString("name");
                            String place_id = restObj.getString("place_id");
                            String compound_code = restObj.getString("compound_code");
                            String vicinity = restObj.getString("vicinity");
                            Double lat = Double.parseDouble(restObj.getString("lat"));
                            Double lng = Double.parseDouble(restObj.getString("lng"));
                            LatLng latLng = new LatLng(lat, lng);
                            String rest_phone = restObj.getString("phone");
                            String rest_img = restObj.getString("img_url");

                            JSONArray usersArr = feedObj.getJSONArray("users");
                            for (int j = 0; j < usersArr.length(); j++) {
                                String user_id = hostObj.getString("sid");
                                String user_name = hostObj.getString("name");
                                String user_img = Statics.main_url + hostObj.getString("img_url");

                                UserData userData = new UserData(user_id, user_name, null, null, null, user_img, "n");
                                reqUsersList.add(userData);
                            }

                            FeedReqData data = new FeedReqData(feed_id, status, feed_time,
                                    place_id, compound_code, vicinity, latLng,
                                    host_id, host_name, host_img,
                                    rest_id, rest_name, rest_phone, rest_img,
                                    reqUsersList);
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
                layout_no_poke.setVisibility(View.GONE);

                mAdapter = new MyPokeListAdapter(PokeListActivity.this, httpClient, feedReqList);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
                layout_no_poke.setVisibility(View.VISIBLE);
            }

        }

    }
}