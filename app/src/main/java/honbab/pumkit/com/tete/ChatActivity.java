package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import honbab.pumkit.com.adapter.ChatAdapter;
import honbab.pumkit.com.data.ChatData;
import honbab.pumkit.com.data.Message;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class ChatActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private DatabaseReference mDatabase;

    LinearLayoutManager layoutManager;

    public TextView title_topbar;
    //    public TextView txt_no_comment;
    public RecyclerView recyclerView;
    public ChatAdapter mAdapter;
    //    private FirebaseRecyclerAdapter<ChatData, ChatViewHolder> mAdapter;
    private EditText edit_chat;

    private ArrayList<Message> messages;
    private String fromId = Statics.my_id, toId = "5", toUserName = "상대방", toUserImg;

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

        title_topbar = (TextView) findViewById(R.id.title_topbar);
//        txt_no_comment = (TextView) findViewById(R.id.txt_no_comment);

        layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatAdapter();
//        mAdapter = FirebaseRecyclerAdapter();
        recyclerView.setAdapter(mAdapter);

        messages = new ArrayList<>();


        edit_chat = (EditText) findViewById(R.id.edit_chat);

        TextView btn_send = (TextView) findViewById(R.id.btn_send_chat);
        btn_send.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new MyFeedCommentTask(ChatActivity.this, httpClient).execute();
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

    ChatData chatData;
    ArrayList<ChatData> arrayList = new ArrayList<>();

    private void loadFirebaseDatabase() {
        mDatabase.child("user-messages").child(fromId).child(toId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatabase.child("messages").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatData = dataSnapshot.getValue(ChatData.class);
                        chatData.setToUserName(toUserName);
                        chatData.setPic1(toUserImg);
                        arrayList.add(chatData);

                        recyclerView.setLayoutManager(layoutManager);
                        mAdapter = new ChatAdapter(ChatActivity.this, httpClient, arrayList);
//                        mAdapter.addItem(chatData.getFromId(), chatData.getToId(), chatData.getToUserName(),
//                                chatData.getText(), chatData.getTimestampLong(),
//                                chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), toUserImg);
                        recyclerView.setAdapter(mAdapter);
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

//            String toUserName = "상대방이름";
            chatData.setToUserName(toUserName);

            recyclerView.setLayoutManager(layoutManager);
            mAdapter.notifyDataSetChanged();
            edit_chat.setText("");
        }
    }
}