package honbab.pumkit.com.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.FeedListAdapter;
import honbab.pumkit.com.data.ReservData;
import honbab.pumkit.com.tete.FeedMapActivity;
import honbab.pumkit.com.tete.LoginActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.ReservActivity;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.CircleTransform;
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

//    ListView listView_feed;
    RecyclerView gridView_feed;
    FeedListAdapter mAdapter;

    ArrayList<ReservData> feedList = new ArrayList<>();
//    ArrayList<MapData> mapList = new ArrayList<MapData>();

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

        Log.e("abc", "feedList.size() = " + feedList.size());
        if (feedList.size() == 0) {
            new FeedListTask().execute();
        } else {
            for (int i=0; i<feedList.size(); i++) {
                String sid = feedList.get(i).getSid();

                //등록자 정보
                String user_id = feedList.get(i).getUser_id();
                String user_name = feedList.get(i).getUser_name();
                String user_img = feedList.get(i).getUser_img();
                String user_age = feedList.get(i).getUser_age();
                String user_gender = feedList.get(i).getUser_gender();

                //음식점 정보
                String rest_name = feedList.get(i).getRest_name();
                String location = feedList.get(i).getLocation();
                Double db_lat = feedList.get(i).getLatitude();
                Double db_lng = feedList.get(i).getLongtitue();
                LatLng latLng = feedList.get(i).getLatLng();
                String rest_img = feedList.get(i).getRest_img();

                String time = feedList.get(i).getTime();

                mAdapter.addItem(sid, user_id, user_name, user_img, user_age, user_gender,
                        rest_name, location, latLng, rest_img, time);
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    private void initControls() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        gridView_feed = (RecyclerView) getActivity().findViewById(R.id.gridView_feed);
        gridView_feed.setLayoutManager(layoutManager);
        mAdapter = new FeedListAdapter(getActivity());
        gridView_feed.setAdapter(mAdapter);

        ImageView btn_go_map;
        btn_go_map = (ImageView) getActivity().findViewById(R.id.btn_go_map);
        btn_go_map.setOnClickListener(mOnClickListener);

        Button btn_reserve;
        btn_reserve = (Button) getActivity().findViewById(R.id.btn_reserve_google);
        btn_reserve.setOnClickListener(mOnClickListener);

        img_topbar_profile = (ImageView) getActivity().findViewById(R.id.img_topbar_profile);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_map:
                    Intent intent = new Intent(getActivity(), FeedMapActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    Log.e("abc", "getLatLng = " + feedList.get(0).getLatLng());
                    Log.e("abc", "FeedFragment mOnClickListener = " + feedList.size());
                    intent.putExtra("feedList", feedList);
                    startActivity(intent);

                    break;
                case R.id.btn_reserve_google:
                    Intent intent2;
                    if (Statics.my_id == null)
                        intent2 = new Intent(getActivity(), LoginActivity.class);
                    else
                        intent2 = new Intent(getActivity(), ReservActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
            }
        }
    };

    //피드리스트
    ImageView img_topbar_profile;
    public class FeedListTask extends AsyncTask<Void, Void, Void> {
        String my_img_url;

        @Override
        protected void onPreExecute() {
            feedList.clear();
            mAdapter.clearItemList();


        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "feed_list")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "FeeeeeeeeeedList : " + obj);

                    //vvvvvvvvvvvvv 나중에 ProfileFragment에서 처리하도록
                    JSONObject myObj = obj.getJSONObject("my");
                    my_img_url = Statics.main_url + myObj.getString("img_url");

                    JSONArray hash_arr = obj.getJSONArray("feed_list");
                    for (int i = 0; i < hash_arr.length(); i++) {
                        JSONObject obj2 = hash_arr.getJSONObject(i);

                        String sid = obj2.getString("sid");

                        //등록자 정보
                        JSONObject user_obj = obj2.getJSONObject("user");
                        String user_id = user_obj.getString("sid");
                        String user_name = user_obj.getString("name");
                        String user_img = user_obj.getString("img_url");
                        String user_age = user_obj.getString("age");
                        String user_gender = user_obj.getString("gender");

                        //음식점 정보
                        JSONObject rest_obj = obj2.getJSONObject("rest");
                        String rest_id = rest_obj.getString("sid");
                        String rest_name = rest_obj.getString("name");
                        String location = rest_obj.getString("location");
                        rest_obj.getString("location");
                        String lat = rest_obj.getString("lat");
                        String lng = rest_obj.getString("lng");
                        Double db_lat = Double.parseDouble(lat);
                        Double db_lng = Double.parseDouble(lng);
                        LatLng latLng = new LatLng(db_lat, db_lng);
                        String rest_img = rest_obj.getString("img_url");

                        String time = obj2.getString("time");

                        mAdapter.addItem(sid, user_id, user_name, user_img, user_age, user_gender,
                                rest_name, location, latLng, rest_img, time);
                        ReservData reservData = new ReservData(sid, user_id, user_name, user_img, user_age, user_gender,
                                rest_name, location, latLng, rest_img,
                                time);
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
            mAdapter.notifyDataSetChanged();

            Picasso.get().load(my_img_url)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_topbar_profile);
//            if (Statics.my_id != null)
//            new AccountTask().execute();
        }
    }

//    public class AccountTask extends AsyncTask<Void, Void, Void> {
//        ImageView img_topbar_profile;
//        String user_profile;
//
//        @Override
//        protected void onPreExecute() {
//            img_topbar_profile = (ImageView) getActivity().findViewById(R.id.img_topbar_profile);
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "account")
//                    .add("user_id", Statics.my_id)
//                    .build();
//
//            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();
//
//            try {
//                okhttp3.Response response = httpClient.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
//                    Log.e("abc", "AccountTask : " + obj);
//
//                    //등록자 정보
//                    JSONObject user_obj = obj.getJSONObject("user");
//                    String user_id = user_obj.getString("sid");
//                    String user_name = user_obj.getString("name");
//                    user_profile = user_obj.getString("img_url");
//                    String user_age = user_obj.getString("age");
//                    String user_gender = user_obj.getString("gender");
//                } else {
////                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
//                }
//
//            } catch (Exception e) {
//                Log.e("abc", "Error : " + e.getMessage());
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Log.e("abc", "user_profile = "+ user_profile);
//            Picasso.get().load(Statics.main_url + user_profile)
//                    .placeholder(R.drawable.icon_noprofile_circle)
//                    .transform(new CircleTransform())
//                    .into(img_topbar_profile);
//        }
//    }
}