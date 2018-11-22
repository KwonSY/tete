package honbab.pumkit.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import honbab.pumkit.com.adapter.FeedListAdapter;
import honbab.pumkit.com.data.ReservData;
import honbab.pumkit.com.tete.CommentTalkActivity;
import honbab.pumkit.com.tete.FeedMapActivity;
import honbab.pumkit.com.tete.LoginActivity;
import honbab.pumkit.com.tete.MyFeedListActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.ReservActivity;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class FeedFragment extends Fragment {

    private OkHttpClient httpClient;

    String my_id = Statics.my_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        if (my_id == null)
            my_id = "-1";
    }

    RecyclerView gridView_feed;
    FeedListAdapter mAdapter;

    ArrayList<ReservData> feedList = new ArrayList<>();

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

        if (feedList.size() == 0) {
            new FeedListTask().execute();
        } else {
//            for (int i=0; i<feedList.size(); i++) {
//                String sid = feedList.get(i).getSid();
//
//                //등록자 정보
//                String user_id = feedList.get(i).getUser_id();
//                String user_name = feedList.get(i).getUser_name();
//                String user_img = feedList.get(i).getUser_img();
//                String user_age = feedList.get(i).getUser_age();
//                String user_gender = feedList.get(i).getUser_gender();
//
//                //음식점 정보
//                String rest_name = feedList.get(i).getRest_name();
//                String location = feedList.get(i).getLocation();
//                String place_id = feedList.get(i).getPlace_id();
//                Double db_lat = feedList.get(i).getLatitude();
//                Double db_lng = feedList.get(i).getLongtitue();
//                LatLng latLng = feedList.get(i).getLatLng();
//                String rest_img = feedList.get(i).getRest_img();
//
//                String status = feedList.get(i).getStatus();
//                String time = feedList.get(i).getTime();
//
////                mAdapter.addItem(sid, user_id, user_name, user_img, user_age, user_gender,
////                        rest_name, location, place_id, latLng, rest_img,
////                        status, time);
//            }
            mAdapter = new FeedListAdapter(getActivity(), feedList);
            gridView_feed.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initControls() {
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_map:
                    Intent intent = new Intent(getActivity(), FeedMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("feedList", feedList);
                    Log.e("abc", "FeedFragment -> OneRest feed_id = " + feedList.get(0).getSid());
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
            Log.e("abc", "Statics.my_id = " + Statics.my_id);
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
                    Log.e("abc", "FeeeeeeeeeedList : " + obj);

                    JSONArray hash_arr = obj.getJSONArray("feed_list");
                    for (int i = 0; i < hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String sid = obj2.getString("sid");

                        //등록자 정보
                        JSONObject host_obj = obj2.getJSONObject("host");
                        String user_id = host_obj.getString("sid");
                        String user_name = host_obj.getString("name");
                        String user_img = host_obj.getString("img_url");
                        String user_age = host_obj.getString("age");
                        String user_gender = host_obj.getString("gender");

                        //음식점 정보
                        JSONObject rest_obj = obj2.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name");
                        String location = rest_obj.getString("location");
                        String place_id = rest_obj.getString("place_id");
                        String lat = rest_obj.getString("lat");
                        String lng = rest_obj.getString("lng");
                        Double db_lat = Double.parseDouble(lat);
                        Double db_lng = Double.parseDouble(lng);
                        LatLng latLng = new LatLng(db_lat, db_lng);
                        String rest_img = rest_obj.getString("img_url");

//                        //feedee - 신청자
//                        JSONArray feedee_arr = obj2.getJSONObject("feedee");
//                        for (int j=0; j<feedee_arr.length(); j++) {
//                            JSONObject feedee_obj = feedee_arr.getJSONObject(j);
//                            String feedee_id = feedee_obj.getString("sid");
//                            String feedee_status = feedee_obj.getString("status");
////                            String feedee_img = feedee_obj.getString("img_url");
//                            String feedee_time = feedee_obj.getString("time");
//                        }

                        String status = obj2.getString("status");
                        String time = obj2.getString("time");

//                        mAdapter.addItem(sid, user_id, user_name, user_img, user_age, user_gender,
//                                rest_name, location, place_id, latLng, rest_img,
//                                status, time);
                        ReservData reservData = new ReservData(sid, user_id, user_name, user_img, user_age, user_gender,
                                rest_name, location, place_id, latLng, rest_img,
                                status, time);
                        feedList.add(reservData);
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
            mAdapter = new FeedListAdapter(getActivity(), feedList);
            gridView_feed.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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
                    Log.e("abc", "CheckReserv : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");

                    JSONObject feedObj = obj.getJSONObject("feed");
                    status = feedObj.getString("status");
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
                if (status.equals("n")) {
//                    Activity mActivity = ((Activity) MyFeedListActivity.class);
                    MyFeedListActivity mActivity = new MyFeedListActivity();
                    alertShow(mActivity);
                } else if (status.equals("y")) {
                    CommentTalkActivity mActivity = new CommentTalkActivity();
                    alertShow(mActivity);
                }

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