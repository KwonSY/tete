package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.FeedListAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.MyFeedListTask;
import honbab.voltage.com.tete.FeedMapActivity;
import honbab.voltage.com.tete.LoginActivity;
import honbab.voltage.com.tete.MyFeedListActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyFeedFragment extends Fragment {

    private OkHttpClient httpClient;

    //마이프로필
    private ImageView img_my;
    private TextView txt_myName, txt_comment;
    //마이피드
    public SwipeRefreshLayout swipeContainer;
    public RecyclerView gridView_feed;
    public FeedListAdapter mAdapter;

    public ArrayList<FeedData> feedList = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.fragment_myfeed, container, false);

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

        new MyFeedListTask(getActivity(), httpClient).execute();

        try {
            UserData myData = new AccountTask(getActivity(), httpClient, 0).execute(my_id).get();

            Picasso.get().load(myData.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_my);
            txt_myName.setText(myData.getUser_name());
            txt_comment.setText(myData.getComment());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initControls() {
        //마이 프로필
        img_my = (ImageView) getActivity().findViewById(R.id.img_my);
        txt_myName = (TextView) getActivity().findViewById(R.id.txt_myName);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);

        img_my.setOnClickListener(mOnClickListener);

//        RelativeLayout layout_feedee = (RelativeLayout) getActivity().findViewById(R.id.layout_feedee);
//        layout_feedee.;
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_myfeed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearItemList();
                new MyFeedListTask(getActivity(), httpClient).execute();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        gridView_feed = (RecyclerView) getActivity().findViewById(R.id.gridView_feed);
        gridView_feed.setLayoutManager(layoutManager);
        mAdapter = new FeedListAdapter();
        gridView_feed.setAdapter(mAdapter);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_my:
                    Intent intent3 = new Intent(getActivity(), ProfileActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent3.putExtra("user_id", Statics.my_id);
                    startActivity(intent3);

                    break;
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
    public class MyFeedListTask2 extends AsyncTask<Void, Void, Void> {
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
                    .add("opt", "my_feed_list")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    JSONArray hash_arr = obj.getJSONArray("feed");
                    for (int i = 0; i < hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);
                        //피드정보
                        String feed_id = obj2.getString("sid");
                        String status = obj2.getString("status");
                        String time = obj2.getString("time");

                        //등록자 정보
                        JSONObject host_obj = obj2.getJSONObject("host");
                        String host_id = host_obj.getString("sid");
                        String host_name = host_obj.getString("name");
                        String host_img = host_obj.getString("img_url");
                        String host_age = host_obj.getString("age");
                        String host_gender = host_obj.getString("gender");
                        String host_token = host_obj.getString("token");

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

                        //참가자
//                        if (obj2.getJSONArray("users").length() > 0) {
                        JSONObject user_obj = obj2.getJSONArray("users").getJSONObject(0);
                        String user_id = user_obj.getString("sid");
                        String user_name = user_obj.getString("name");
                        String user_img = user_obj.getString("img_url");
                        String user_age = user_obj.getString("age");
                        String user_gender = user_obj.getString("gender");
                        String user_token = user_obj.getString("token");

                        FeedData feedData;
                        if (host_id.equals(Statics.my_id)) {
                            feedData = new FeedData(feed_id,
                                    user_id, user_name, user_age, user_gender, user_img, user_token,
                                    rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                    status, time);
                        } else {
                            feedData = new FeedData(feed_id,
                                    host_id, host_name, host_age, host_gender, host_img,host_token,
                                    rest_id, rest_name, compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                                    status, time);
                        }
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