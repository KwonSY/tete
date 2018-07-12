package honbab.pumkit.com.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import honbab.pumkit.com.data.ReservData;
import honbab.pumkit.com.tete.OneReservActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.FeedListAdapter;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedFragment extends Fragment {

    private OkHttpClient httpClient;

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    ListView listView_feed;
    FeedListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {
        listView_feed = (ListView) getView().findViewById(R.id.listView_feed);
        mAdapter = new FeedListAdapter();
        listView_feed.setAdapter(mAdapter);
        listView_feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                ReservData reservData = (ReservData) parent.getItemAtPosition(i);

//                Intent intent = new Intent(getActivity(), OnePublicDate.class);
                Intent intent = new Intent(getActivity(), OneReservActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("reservId", reservData.getSid());
                startActivity(intent);
            }
        });

        new FeedListTask().execute();

    }

    public class FeedListTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            mAdapter.clearItemList();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "feed_list")
                    .add("my_id", "1")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
//                    Log.e("abc", "마이해쉬돌아감 : " + obj);

                    JSONArray hash_arr = obj.getJSONArray("feed_list");
                    for (int i=0; i<hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String sid = obj2.getString("sid");

                        JSONObject user_obj = obj2.getJSONObject("user");
                        String user_id = user_obj.getString("sid");
                        String user_name = user_obj.getString("name");
                        String user_profile = user_obj.getString("img_url");

                        JSONObject rest_obj = obj2.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name_k");
                        String name_e = rest_obj.getString("name_e");
                        String type = rest_obj.getString("type");
                        String location = rest_obj.getString("location");
                        String gps = rest_obj.getString("gps");

                        String style = obj2.getString("style");
                        String time = obj2.getString("time");

                        mAdapter.addItem(sid, user_id, user_name, user_profile, rest_name, gps, location, style, time);
                    }

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
            mAdapter.notifyDataSetChanged();
        }
    }
}