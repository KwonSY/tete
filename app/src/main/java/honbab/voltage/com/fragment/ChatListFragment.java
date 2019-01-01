package honbab.voltage.com.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.ChatListAdapter;
import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.tete.PokeListActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.SettingActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.VerticalItemDecoration;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChatListFragment extends Fragment {
    private OkHttpClient httpClient;
    private DatabaseReference mDatabase;

    private String my_id = Statics.my_id;
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<FeedReqData> mParam1;

    private ImageButton btn_setting;
    private ImageView image_myProfile;
    private TextView txt_comment;

    public static TextView badge_poke_cnt;
    public static RecyclerView recyclerView;
    public static ChatListAdapter mAdapter;

    private ChatData chatData;
    private String toId, token;
    private ArrayList<FeedReqData> pokeList = new ArrayList<>();


    public ChatListFragment() {

    }

    public static ChatListFragment newInstance(ArrayList<FeedReqData> param1) {
        ChatListFragment fragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = (ArrayList<FeedReqData>) getArguments().getSerializable(ARG_PARAM1);
            Log.e("abc", "0 mParam1 = " + mParam1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chatlist, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        if (getArguments() != null) {
            //xxxxxxxxxxx 안 돌아감
            mParam1 = (ArrayList<FeedReqData>) getArguments().getSerializable(ARG_PARAM1);
            Log.e("abc", "1 mParam1 = " + mParam1);

            MyFeedListAdapter mAdapter= new MyFeedListAdapter(getActivity(), httpClient, mParam1);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

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

        try {
            UserData myData = new AccountTask(getActivity(), httpClient, 0).execute(my_id).get();
            Log.e("abc", "ddddddddddddddd " + myData.getImg_url());

            Picasso.get().load(myData.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(image_myProfile);
            txt_comment.setText(myData.getComment());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initControls() {
        //마이탭 프로필
        image_myProfile = (ImageView) getActivity().findViewById(R.id.image_myProfile);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);
        image_myProfile.setOnClickListener(mOnClickListener);
        /*
        btn_setting = (ImageButton) getActivity().findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(mOnClickListener);
        btn_setting.setOnTouchListener(mOnTouchListener);

        //3가지 버튼
        layout_go_pokelist = (RelativeLayout) getActivity().findViewById(R.id.layout_go_pokelist);
        layout_go_pokelist.setOnClickListener(mOnClickListener);
        badge_poke_cnt = (TextView) getActivity().findViewById(R.id.badge_poke_cnt);
        */

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView_chat);
        recyclerView.setLayoutManager(layoutManager);
        if (recyclerView.getItemDecorationCount() == 0)
            recyclerView.addItemDecoration(new VerticalItemDecoration(20, false));
        mAdapter = new ChatListAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

//        loadFirebaseChat();
    }

    public void loadFirebaseChat() {
        //채팅리스트
        mDatabase.child("user-messages").child(my_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                toId = dataSnapshot.getKey();
                Log.e("abc", "Chay mDatabase toId = " + toId);

                mDatabase.child("user-messages").child(my_id).child(String.valueOf(toId)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        Map<String, Object> map = (HashMap<String,Object>) dataSnapshot2.getValue();

                        for ( String id_message : map.keySet() ) {
                            // 2. 메세지의 내용을 찾아보자
                            Log.e("abc", "id_message = " + id_message);
                            mDatabase.child("messages").child(id_message).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {
                                    chatData = dataSnapshot2.getValue(ChatData.class);
                                    Log.e("abc", "firebase .getFromId() = " + chatData.getFromId());
                                    Log.e("abc", "firebase  getText = " + chatData.getText());
                                    if (my_id.equals(chatData.getFromId())) {
                                        toId = chatData.getToId();
                                    } else {
                                        toId = chatData.getFromId();
                                    }

                                    new ChatAccountTask().execute(String.valueOf(toId), chatData.getText());

//                                    mAdapter.addItem(my_id, toId, "홍길동", chatData.getText(),
//                                            chatData.getTimestampLong(),
//                                            '', 0, 0, '');

//                                    chatListAdapter.changeUserName(dataSnapshot3.getKey(), userVo.getUsername(), userVo.getImg_url());
//                                    listView.setAdapter(chatListAdapter);
//
//                                    chatListAdapter.notifyDataSetChanged();




//                                    mDatabase.child("users").child(blind_FireId).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot3) {
////                                    partner_id = (String) dataSnapshot.getValue();
//                                            UserData userVo = dataSnapshot3.getValue(UserData.class);
//
////                                    new AccountTask().execute(userVo.getmy_id(), dataSnapshot3.getKey());
//
////                                    Log.e("abc", "userVo.getUsername() = " + userVo.getUsername() + ", dataSnapshot3.getKey() = " +dataSnapshot3.getKey());
//                                            chatListAdapter.changeUserName(dataSnapshot3.getKey(), userVo.getUsername(), userVo.getImg_url());
//                                            listView.setAdapter(chatListAdapter);
//
//                                            chatListAdapter.notifyDataSetChanged();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                            new AccountTask().execute(partner_id);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_myProfile:
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_setting:
                    Intent intent2 = new Intent(getActivity(), SettingActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
                case R.id.layout_go_pokelist:
                    Intent intent4 = new Intent(getActivity(), PokeListActivity.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent4.putExtra("feedList", pokeList);
                    startActivity(intent4);

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    btn_setting.setImageAlpha(70);

                    break;
                case MotionEvent.ACTION_UP:
                    btn_setting.setImageAlpha(255);

                    break;
            }
            return false;
        }
    };

    // load account
    public class ChatAccountTask extends AsyncTask<String, Void, UserData> {
        String lastMessage = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected UserData doInBackground(String... params) {
            UserData userData = new UserData();

            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("user_id", params[0])
                    .build();
            lastMessage = params[1];

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    JSONObject user_obj = obj.getJSONObject("user");
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String age = user_obj.getString("age");
                    String gender = user_obj.getString("gender");
                    String img_url = Statics.main_url + user_obj.getString("img_url");
                    String comment = user_obj.getString("comment");
                    if (comment.equals("null"))
                        comment = "";
                    String user_token = user_obj.getString("token");

                    userData = new UserData(user_id, user_name, age, gender, token, img_url, "");

                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return userData;
        }

        @Override
        protected void onPostExecute(UserData userData) {
            super.onPostExecute(userData);

            Log.e("abc", "ChatFragment account : " + my_id);
            Log.e("abc", "ChatFragment userData.getUser_id() : " + userData.getUser_id());
            Log.e("abc", "ChatFragment userData.getUser_name() : " + userData.getUser_name());
            Log.e("abc", "ChatFragment lastMessage : " + lastMessage);

            ChatData chatData = new ChatData("t", my_id, String.valueOf(userData.getUser_id()), userData.getUser_name(), lastMessage,
                    "", 0, 0, userData.getImg_url());

            mAdapter.addItem(chatData);
//            mAdapter.changeUserName(dataSnapshot3.getKey(), userVo.getUsername(), userVo.getImg_url());
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();




//            new MyFeedListTask(getActivity(), httpClient).execute();

//            if (!token.equals(user_token))
//                new UpdateToken().execute(token);
        }
    }

    public class UpdateToken extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_token")
                    .add("my_id", my_id)
                    .add("token", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
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

        }
    }

//    public void setSwipeRefresh(boolean refresh) {
//        swipeContainer.setRefreshing(refresh);
//    }
}