package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.FeedListAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.tete.FeedMapActivity;
import honbab.voltage.com.tete.LoginActivity;
import honbab.voltage.com.tete.MyFeedListActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedFragment extends Fragment {

    private OkHttpClient httpClient;

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView gridView_feed;
    private FeedListAdapter mAdapter;

    private ArrayList<FeedData> feedList = new ArrayList<>();
    private String my_id = Statics.my_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        if (my_id == null)
            my_id = "-1";
    }



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

    @Override
    public void onResume() {
        super.onResume();

//        if (feedList.size() == 0) {
            new FeedListTask().execute();
//        } else {
//            mAdapter = new FeedListAdapter(getActivity(), feedList);
//            gridView_feed.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//        }
    }

    private void initControls() {
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_feed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearItemList();
                new FeedListTask().execute();
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        gridView_feed = (RecyclerView) getActivity().findViewById(R.id.gridView_feed);
        gridView_feed.setLayoutManager(layoutManager);
        mAdapter = new FeedListAdapter();
        gridView_feed.setAdapter(mAdapter);

        ImageView btn_go_map;
        btn_go_map = (ImageView) getActivity().findViewById(R.id.btn_go_map);
        btn_go_map.setOnClickListener(mOnClickListener);

        Button btn_reserve;
        btn_reserve = (Button) getActivity().findViewById(R.id.btn_reserve_google);
        btn_reserve.setOnClickListener(mOnClickListener);
    }

//    public void fetchTimelineAsync(int page) {
//        // Send the network request to fetch the updated data
//        // `client` here is an instance of Android Async HTTP
//        // getHomeTimeline is an example endpoint.
//        client.getHomeTimeline(new JsonHttpResponseHandler() {
//            public void onSuccess(JSONArray json) {
//                mAdapter.clearItemList();
//                mAdapter.addAll(feedList);
//
//                // Remember to CLEAR OUT old items before appending in the new ones
////                adapter.clear();
//                // ...the data has come back, add new items to your adapter...
////                adapter.addAll(...);
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("abc", "Fetch timeline error: " + e.toString());
//            }
//        });
//    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_map:
                    Intent intent = new Intent(getActivity(), FeedMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("feedList", feedList);
                    startActivity(intent);

                    break;
                case R.id.btn_reserve_google:
                    Intent intent2;
                    if (Statics.my_id == null) {
                        intent2 = new Intent(getActivity(), LoginActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                    } else {
                        new CheckReservTask().execute();
                    }
//                        intent2 = new Intent(getActivity(), ReservActivity.class);

                    break;
            }
        }
    };

    //피드리스트
    public class FeedListTask extends AsyncTask<Void, Void, Void> {
        String my_id;

        @Override
        protected void onPreExecute() {
            feedList.clear();
            mAdapter.clearItemList();

            if (Statics.my_id == null)
                my_id = "0";
            else
                my_id = Statics.my_id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "feed_list")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    JSONArray hash_arr = obj.getJSONArray("feed_list");
                    for (int i = 0; i < hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String feed_id = obj2.getString("sid");

                        //등록자 정보
                        JSONObject host_obj = obj2.getJSONObject("host");
                        String user_id = host_obj.getString("sid");
                        String user_name = host_obj.getString("name");
                        String user_img = host_obj.getString("img_url");
                        String user_age = host_obj.getString("age");
                        String user_gender = host_obj.getString("gender");
                        String user_token = host_obj.getString("token");

                        //음식점 정보
                        JSONObject rest_obj = obj2.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name");
                        String compound_code = rest_obj.getString("compound_code");
                        String vicinity = rest_obj.getString("vicinity");
                        String place_id = rest_obj.getString("place_id");
                        String lat = rest_obj.getString("lat");
                        String lng = rest_obj.getString("lng");
                        Double db_lat = Double.parseDouble(lat);
                        Double db_lng = Double.parseDouble(lng);
                        LatLng latLng = new LatLng(db_lat, db_lng);
                        String rest_phone = rest_obj.getString("phone");
                        String rest_img = rest_obj.getString("img_url");

                        String status = obj2.getString("status");
                        String time = obj2.getString("time");

                        FeedData feedData = new FeedData(feed_id,
                                user_id, user_name, user_age, user_gender, user_img, user_token,
                                rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                status, time);
                        feedList.add(feedData);
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
            mAdapter = new FeedListAdapter(getActivity(), httpClient, feedList);
            gridView_feed.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            swipeContainer.setRefreshing(false);
        }
    }

    public class CheckReservTask extends AsyncTask<Void, Void, Void> {
        private String result;
        private String status;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "check_reserv")
                    .add("my_id", Statics.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");

                    if (!obj.isNull("feed")) {
                        JSONObject feedObj = obj.getJSONObject("feed");
                        status = feedObj.getString("status");
                    }
                } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {
                //예약없다
                Intent intent2 = new Intent(getActivity(), ReservActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
            } else {
                //예약있다
                //이미 예약하신 같먹이 있습니다. dialog
                MyFeedListActivity mActivity = new MyFeedListActivity();
                alertShow(mActivity);
            }
        }
    }

    public void alertShow(final Activity mActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("AlertDialog Title");
        builder.setMessage(R.string.already_reserved_godmuk);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(getActivity(), MyFeedListActivity.class);
                        Intent intent = new Intent(getActivity(), mActivity.getClass());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.setNegativeButton(R.string.reserve_new_godmuk,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.make_new_godmuk, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), ReservActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.show();
    }

}