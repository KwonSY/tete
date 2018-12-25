package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ServerValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import honbab.voltage.com.adapter.ChatAdapter;
import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.data.FcmData;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChatActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private DatabaseReference mDatabase;

    private ChatData chatData;
//    private ArrayList<ChatData> arrayList = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    public TextView title_topbar;
    public RecyclerView recyclerView;
    public ChatAdapter mAdapter;
    //    private FirebaseRecyclerAdapter<ChatData, ChatViewHolder> mAdapter;
    private EditText edit_chat;

//    private ArrayList<Message> messages;
    private String fromId = Statics.my_id, toId = "5", toUserName = "상대방", toUserImg, toToken;
    private String rest_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();
        fromId = intent.getStringExtra("fromId");
        toId = intent.getStringExtra("toId");
        toUserName = intent.getStringExtra("toUserName");
        toUserImg = intent.getStringExtra("toUserImg");
        toToken = intent.getStringExtra("toToken");
        rest_phone = intent.getStringExtra("rest_phone");
        Log.e("abc", "fromId = " + fromId + ", toId = " + toId);
        Log.e("abc", "toUserName = " + toUserName);

        title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(toUserName);

        TextView txt_rest_phone;
        txt_rest_phone = (TextView) findViewById(R.id.txt_rest_phone);
        txt_rest_phone.setText(rest_phone);

        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatAdapter(getApplicationContext());
        recyclerView.setAdapter(mAdapter);

//        messages = new ArrayList<>();


        edit_chat = (EditText) findViewById(R.id.edit_chat);

        TextView btn_send = (TextView) findViewById(R.id.btn_send_chat);
        btn_send.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFirebaseDatabase();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_send_chat:
                    sendMessage();

                    break;
            }
        }
    };



    private void loadFirebaseDatabase() {
        mDatabase.child("user-messages").child(fromId).child(toId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatabase.child("messages").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatData = dataSnapshot.getValue(ChatData.class);
                        chatData.setToUserName(toUserName);
                        chatData.setToUserImg(toUserImg);
//                        arrayList.add(chatData);

//                        recyclerView.setLayoutManager(layoutManager);
//                        mAdapter = new ChatAdapter(getApplicationContext());
//                        mAdapter = new ChatAdapter(ChatActivity.this, arrayList);
                        mAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(),
                                chatData.getTimestampLong(),
                                chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), toUserImg);
                        recyclerView.setAdapter(mAdapter);
//                        recyclerView.scrollToPosition(mAdapter.newList.size()-1);
                        mAdapter.notifyDataSetChanged();
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

    private void sendMessage() {
        if (edit_chat.getText().toString().length() > 0) {
            final String message = edit_chat.getText().toString();
            final HashMap<String, Object> timestamp = new HashMap<>();
            timestamp.put("time", ServerValue.TIMESTAMP);

            ChatData chatData = new ChatData(fromId, toId, message, timestamp);

            DatabaseReference id_message = mDatabase.child("messages").push();
            mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);

            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put(id_message.getKey(), 1);

            mDatabase.child("user-messages").child(fromId).child(toId).updateChildren(taskMap);
            mDatabase.child("user-messages").child(toId).child(fromId).updateChildren(taskMap);

            chatData.setToUserName(toUserName);

            recyclerView.setLayoutManager(layoutManager);
            mAdapter.notifyDataSetChanged();
            edit_chat.setText("");

            FcmData params = new FcmData(toToken, message);
            new ChatFCMTask().execute(params);
        }
    }

    private class ChatFCMTask extends AsyncTask<FcmData, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(FcmData... params) {
            if (params[0].token == null || params[0].token.equals("null")) {

            } else {
                FormBody body = new FormBody.Builder()
                        .add("token", params[0].token)
                        .add("message", params[0].message)
                        .build();

                Request request = new Request.Builder().url(Statics.main_url+"fcm/push_chat.php").post(body).build();

                try {
                    okhttp3.Response response = httpClient.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String bodyStr = response.body().string();
                        Log.e("FCM", "FCM_obj 444 = " + bodyStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}