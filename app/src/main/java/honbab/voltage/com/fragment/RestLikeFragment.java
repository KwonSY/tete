package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.ChatListAdapter;
import honbab.voltage.com.adapter.RestLikeListAdapter;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.RestLikeListTask;
import honbab.voltage.com.tete.DelayBefroePickRestActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class RestLikeFragment extends Fragment {
    private OkHttpClient httpClient;

    public SwipeRefreshLayout swipeContainer;
    public TextView txt_feedTime;
    public RecyclerView recyclerView, recyclerView_cb;
    public RestLikeListAdapter mAdapter;
    public ChatListAdapter mAdapter_cb;

    public ArrayList<FeedReqData> feedList = new ArrayList<>();
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
        View rootView = inflater.inflate(R.layout.fragment_restlikelist, container, false);

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

        new RestLikeListTask(getActivity(), httpClient).execute();

        loadFirebaseChatList();
    }

    private void initControls() {
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_feed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearItemList();
                new RestLikeListTask(getActivity(), httpClient).execute();
            }
        });

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_cb = (RecyclerView) getActivity().findViewById(R.id.recyclerView_chat_before);
        recyclerView_cb.setLayoutManager(layoutManager2);
        mAdapter_cb = new ChatListAdapter(getActivity());
        recyclerView_cb.setAdapter(mAdapter_cb);

        //Rest Like
        txt_feedTime = (TextView) getActivity().findViewById(R.id.txt_feedTime);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView_feed);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RestLikeListAdapter();
        recyclerView.setAdapter(mAdapter);

        //좋아요 레스토랑이 없을 때
        Button btn_go_pick_rest = (Button) getActivity().findViewById(R.id.btn_go_pick_rest);
        btn_go_pick_rest.setOnClickListener(mOnClickListener);
    }

    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_pick_rest:
                    Intent intent = new Intent(getActivity(), DelayBefroePickRestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
//                case R.id.btn_go_map:
//                    Intent intent = new Intent(getActivity(), FeedMapActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("feedList", feedList);
//                    startActivity(intent);
//
//                    break;
//                case R.id.btn_reserve_google:
//                    Intent intent2;
//                    if (Statics.my_id == null) {
//                        intent2 = new Intent(getActivity(), LoginActivity.class);
//                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent2);
//                    } else {
//                        new CheckReservTask().execute();
//                    }
////                        intent2 = new Intent(getActivity(), ReservActivity.class);
//
//                    break;
            }
        }
    };

//    //피드리스트
//    public class RestLikeListTaskxxxx extends AsyncTask<Void, Void, Void> {
//        String my_id;
//
//        @Override
//        protected void onPreExecute() {
//            feedList.clear();
//            mAdapter.clearItemList();
//
//            if (Statics.my_id == null)
//                my_id = "0";
//            else
//                my_id = Statics.my_id;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "rest_like_list")
//                    .add("my_id", my_id)
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
//
//                    JSONArray hash_arr = obj.getJSONArray("rest_like_list");
//                    for (int i=0; i<hash_arr.length(); i++) {
//                        JSONObject obj2 = hash_arr.getJSONObject(i);
//
//                        //음식점 정보
//                        JSONObject rest_obj = obj2.getJSONObject("rest");
//                        String rest_id = rest_obj.getString("sid");
//                        String rest_name = rest_obj.getString("name");
//                        String compound_code = rest_obj.getString("compound_code");
//                        String lat = rest_obj.getString("lat");
//                        String lng = rest_obj.getString("lng");
//                        Double db_lat = Double.parseDouble(lat);
//                        Double db_lng = Double.parseDouble(lng);
//                        LatLng latLng = new LatLng(db_lat, db_lng);
//                        String place_id = rest_obj.getString("place_id");
//                        String rest_phone = rest_obj.getString("phone");
//                        String rest_img = rest_obj.getString("img_url");
//                        String vicinity = rest_obj.getString("vicinity");
//
//                        ArrayList<UserData> usersList = new ArrayList<>();
//                        //좋아요 누른 User
//                        JSONArray users_arr = obj2.getJSONArray("users");
//                        for (int j=0; j<users_arr.length(); j++) {
//                            JSONObject user_obj = users_arr.getJSONObject(j);
//
//                            String user_id = user_obj.getString("sid");
//                            String user_name = user_obj.getString("name");
//                            String age = user_obj.getString("age");
//                            String gender = user_obj.getString("gender");
//                            String token = user_obj.getString("token");
//                            String user_img = Statics.main_url + user_obj.getString("img_url");
//
//                            UserData userData = new UserData(user_id, user_name,
//                                    age, gender, token, user_img, null);
//                            usersList.add(userData);
//                        }
//
//                        FeedReqData feedData = new FeedReqData(rest_id, rest_name, rest_img, rest_phone,
//                                compound_code, latLng, place_id, vicinity,
//                                usersList);
//                        feedList.add(feedData);
//                    }
//
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
////            mAdapter = new RestLikeListAdapter(getActivity(), httpClient, feedList);
//            recyclerView.setAdapter(mAdapter);
//            mAdapter.notifyDataSetChanged();
//
//            swipeContainer.setRefreshing(false);
//        }
//    }

//    public class CheckReservTask extends AsyncTask<Void, Void, Void> {
//        private String result;
//        private String status;
//
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "check_reserv")
//                    .add("my_id", Statics.my_id)
//                    .build();
//
//            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();
//
//            try {
//                okhttp3.Response response = httpClient.newCall(request).execute();
//
//                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
//
//                    result = obj.getString("result");
//
//                    if (!obj.isNull("feed")) {
//                        JSONObject feedObj = obj.getJSONObject("feed");
//                        status = feedObj.getString("status");
//                    }
//                } else {
//                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
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
//            if (result.equals("0")) {
//                //예약없다
//                Intent intent2 = new Intent(getActivity(), ReservActivity.class);
//                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent2);
//            } else {
//                //예약있다
//                //이미 예약하신 같먹이 있습니다. dialog
//                MyFeedListActivity mActivity = new MyFeedListActivity();
//                alertShow(mActivity);
//            }
//        }
//    }

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

    private DatabaseReference mDatabase;
    public void loadFirebaseChatList() {
        final int i = 0;
        HashMap<String, Integer> chatListHash = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-messages").child(Statics.my_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                i = 0;
                String toId = dataSnapshot.getKey();
                chatListHash.put(toId, 0);
                mDatabase.child("user-messages").child(Statics.my_id).child(toId).limitToLast(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Object o = dataSnapshot.getValue();
                        int plus = Integer.parseInt(o.toString());

                        if (plus > 0)
                            chatListHash.put(toId, plus);

                        if (chatListHash.size() > mAdapter_cb.getItemCount()) {
                            try {
                                UserData userData = new AccountTask(getActivity(), httpClient, 0).execute(toId).get();
                                userData.setStatus(chatListHash.get(toId).toString());

                                mAdapter_cb.addItem(userData);
                                recyclerView_cb.setAdapter(mAdapter_cb);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}