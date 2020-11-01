package honbab.voltage.com.tete;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.data.ChatModel;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.data.UserModel;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.NetworkUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class GroupTalkActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private DatabaseReference mDatabase;

    private LinearLayoutManager layoutManager;

    public DrawerLayout drawerLayout;
    //    public TextView title_topbar, txt_userName, no_chat_txt_userName, txt_explain_no_chat;
    public TextView title_topbar, txt_explain_no_chat;
    public ImageView topbar_img_user, no_chat_img_user;
    public Button btn_call_rest;
    private ImageView icon_more_dots;

    private LinearLayout layout_no_chat;
    public RecyclerView recyclerView;
    //    public ChatAdapter mAdapter;
    public GroupMessageRecyclerViewAdapter mAdapter;
    //채팅 입력창
    private EditText edit_chat;
    private TextView btn_send;

    private String my_id = Statics.my_id;
    //    public String fromId = Statics.my_id;
//    public String toId, toUserName = "상대방", toUserImg, toToken;
    private Map<String, UserModel> users = new HashMap<>();
    private ChatData chatData;
    public RestData restData;
    private String chatRoomCd = "";
    private ArrayList<UserData> groupchatUsersList = new ArrayList<UserData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchat);

        if (!NetworkUtil.isOnline(this)) {
            //인터넷이 안 될 때
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connect_network);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mStartActivity = new Intent(GroupTalkActivity.this, MainActivity.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(GroupTalkActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
            builder.show();
        } else {
            httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            Intent intent = getIntent();
//            Bundle bundle = getIntent().getExtras();
//            chatRoomCd = bundle.getString("chatRoomCd");
//            groupchatUsersList = bundle.getParcelable("groupchatUsersList");

            chatRoomCd = intent.getStringExtra("chatRoomCd");
            groupchatUsersList = intent.getParcelableArrayListExtra("groupchatUsersList");
            Log.e("abc", "groupchatUsersList1 = " + groupchatUsersList);
//            Bundle bundle = getIntent().getExtras();
            Log.e("abc", "groupchatUsersList2 = " + groupchatUsersList);
            Log.e("abc", "groupchatUsersList size = " + groupchatUsersList.size());

            //상단바
            title_topbar = (TextView) findViewById(R.id.title_topbar);
            topbar_img_user = (ImageView) findViewById(R.id.topbar_img_user);
//            txt_userName = (TextView) findViewById(R.id.txt_userName);
            title_topbar.setText("");
            topbar_img_user.setOnClickListener(mOnClickListener);

            loadFirebaseDatabase(chatRoomCd);


            //채팅 없을 때
            layout_no_chat = (LinearLayout) findViewById(R.id.layout_no_chat);
//            no_chat_img_user = (ImageView) findViewById(R.id.no_chat_img_user);
//            no_chat_txt_userName = (TextView) findViewById(R.id.no_chat_txt_userName);
            txt_explain_no_chat = (TextView) findViewById(R.id.txt_explain_no_chat);


            icon_more_dots = (ImageView) findViewById(R.id.icon_more_dots);
            icon_more_dots.setOnClickListener(mOnClickListener);

            edit_chat = (EditText) findViewById(R.id.edit_chat);

            btn_send = (TextView) findViewById(R.id.btn_send_chat);
            btn_send.setOnClickListener(mOnClickListener);

            ButtonUtil.setBackButtonClickListener(this);
        }

//        drawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
//        drawerLayout.addDrawerListener(mDrawerListener);
//        setDrawerReserv();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkUtil.isOnline(this)) {
//            loadFirebaseDatabase(chatRoomCd);

//            new CommonRestTask(GroupTalkActivity.this).execute(toId, null);

            try {
                Badges.setBadge(this, 0);
            } catch (BadgesNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.topbar_img_user:
//                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("user_id", toId);
//                    startActivity(intent);
//
//                    break;
                case R.id.btn_send_chat:
                    sendMessage("t");//t = text

                    break;
                case R.id.btn_call_rest:
                    String uri = "tel:" + restData.getRest_phone();
//                    Log.e("abc", "tel : = " + uri);
                    Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse(uri));
//                    intent1.setData(Uri.parse(uri));
                    startActivity(intent1);
//                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                        // TODO: Consider calling
//                        //    ActivityCompat#requestPermissions
//                        // here to request the missing permissions, and then overriding
//                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                        //                                          int[] grantResults)
//                        // to handle the case where the user grants the permission. See the documentation
//                        // for ActivityCompat#requestPermissions for more details.
//                        return;
//                    }
                    break;
                case R.id.icon_more_dots:
                    PopupMenu popupMenu = new PopupMenu(getApplicationContext(), icon_more_dots);
                    popupMenu.inflate(R.menu.chatmenu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
//                                case R.id.chat_out:
//                                    goOutFirebaseChat(fromId, fromId, toId);
//
//                                    return true;
                                case R.id.report:
                                    Intent intent3 = new Intent(GroupTalkActivity.this, ReportActivity.class);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent3.putExtra("title", "신고하기");
                                    intent3.putExtra("feed_id", "-1");
                                    intent3.putExtra("to_id", "");
                                    startActivity(intent3);

                                    return true;
                            }

                            return true;
                        }
                    });
                    popupMenu.show();

                    break;
                case R.id.btn_eat_with:
                    //같이먹기 시작
                    if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }

                    break;
//                case R.id.txt_date:
//                    DatePickerDialog dialog = new DatePickerDialog(GroupTalkActivity.this, dateSetListener, year, month - 1, day);
//                    dialog.show();
//
//                    break;
//                case R.id.txt_clock:
//                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(GroupTalkActivity.this, timeSetListener,
//                            hour, min, false);
////                dialog.updateTime();
//                    dialog2.show();
//
//                    break;
//                case R.id.btn_reserv:
//                    sendMessage("a");
//
//                    String[] date = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
//                    RestData r_data = restData;
////                    String[] rest = {restData.getRest_name(), restData.getCompound_code(),
////                            String.valueOf(restData.getLatitude()), String.valueOf(restData.getLongtitue()),
////                            restData.getPlace_id(), restData.getRest_img(), restData.getRest_phone(), restData.getVicinity()};
////                    Log.e("abc", "ChatAct lat = " + restData.getLatitude() + restData.getLongtitue());
////                    RestData restData2 = restData;
//
//                    Calendar curCal = Calendar.getInstance();
//                    long time_setting = calendar.getTimeInMillis();
//                    long time_current = curCal.getTimeInMillis();
//
//                    if (time_setting > time_current) {
//                        new ReservFeedTask2(ChatActivity.this, httpClient, date, r_data).execute(toId);
//                    } else {
//                        Toast.makeText(getApplicationContext(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//                    }
//
//                    break;
            }
        }
    };

//    DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
//        @Override
//        public void onDrawerSlide(@NonNull View view, float v) {
//
//        }
//
//        @Override
//        public void onDrawerOpened(@NonNull View view) {
//
//        }
//
//        @Override
//        public void onDrawerClosed(@NonNull View view) {
//
//        }
//
//        @Override
//        public void onDrawerStateChanged(int newState) {
//            String state;
//            switch (newState) {
//                case DrawerLayout.STATE_IDLE:
//                    state = "STATE_IDLE";
//                    break;
//                case DrawerLayout.STATE_DRAGGING:
//                    state = "STATE_DRAGGING";
//                    break;
//                case DrawerLayout.STATE_SETTLING:
//                    state = "STATE_SETTLING";
//                    break;
//                default:
//                    state = "unknown!";
//            }
//
////            txtPrompt2.setText(state);
//        }
//    };

    private void loadFirebaseDatabase(String chatRoomCd) {
//        mAdapter.clearItemList();
//        final String[] chatoutYn = {"e"};

        FirebaseDatabase.getInstance().getReference().child("groupchats").child(chatRoomCd).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                mAdapter = new GroupMessageRecyclerViewAdapter();
                recyclerView.setAdapter(mAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupTalkActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

//    private void goOutFirebaseChat(String fromId, String fromUserName, String toId) {
//
//        if (mAdapter.getLastType().equals("a")) {
//            Query query = mDatabase.child("user-messages").child(fromId).child(toId);
//            query.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    int i = 0;
//
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                        i++;
//
//                        mDatabase.child("messages").child(child.getKey()).removeValue();
//
//                        if (i >= dataSnapshot.getChildrenCount()) {
//                            mDatabase.child("user-messages").child(fromId).child(toId).removeValue();
//
//                            finish();
//                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        } else {
//            String type = "a";
//            String message = "상대방이 채팅방을 나갔습니다.";
//            HashMap<String, Object> timestamp = new HashMap<>();
//            timestamp.put("time", ServerValue.TIMESTAMP);
//
////            ChatData chatData = new ChatData(type, fromId, toId, message, timestamp);
//
//            DatabaseReference id_message = mDatabase.child("messages").push();
//            mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);
//
//            Map<String, Object> taskMap = new HashMap<String, Object>();
//            taskMap.put(id_message.getKey(), 1);
//
//            mDatabase.child("user-messages").child(String.valueOf(toId)).child(String.valueOf(fromId)).updateChildren(taskMap);
//            mDatabase.child("user-messages").child(fromId).child(toId).removeValue();
//        }
//
//        finish();
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    private void sendMessage(String type) {
//        String message = "";

        ChatModel.Chats comment = new ChatModel.Chats();
        comment.uid = my_id;
        comment.message = edit_chat.getText().toString();
        comment.timestamp = ServerValue.TIMESTAMP;

        edit_chat.setText("");

        FirebaseDatabase.getInstance().getReference().child("groupchats").child(chatRoomCd).child("messages").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {

//                sendGcm(users.get(item).pushToken);
//                edit_chat.setText("");
                Log.e("abc", "sendMessage onComplete = ");

                FirebaseDatabase.getInstance().getReference().child("groupchats").child(chatRoomCd).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.e("abc", "dataSnapshot.getValue() = " + dataSnapshot.getValue());
//                        Log.e("abc", "dataSnapshot.getKey() = " + (Map<String, Boolean>) dataSnapshot.getValue());
//                        if (dataSnapshot.getKey() != null) {
//                            Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();
//                            Log.e("abc", "map.keySet() = " + map.keySet());
//
//                            for (String item : map.keySet()) {
//                                Log.e("abc", "item = " + item + ", my_id = " + my_id);
//                                if (item.equals(my_id)) {
//                                    continue;
//                                }
////                            sendGcm(users.get(item).pushToken);
//                            }


//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    public Spinner spinner_restName;
    public ImageView img_rest;
    private TextView txt_date, txt_clock;
    public EditText edit_comment;
    private Button btn_reserv;
    private Calendar calendar;
    int year, month, day, hour, min;// Calender 에서 얻는 값
    private ArrayList<RestData> restList = new ArrayList<>();

    private void setDrawerReserv() {
        spinner_restName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int r = 0;

                for (int i = 0; i < restList.size(); i++) {
                    Log.e("abc", "rrr restList.get(i).getRest_name() : " + restList.get(i).getRest_name());
//                    spinner_restName.getItemIdAtPosition(position))
                    if (restList.get(i).getRest_name().equals(spinner_restName.getSelectedItem().toString()))
                        r = i;
                }
//                Log.e("abc", "rrr getItemIdAtPosition(position) : " + spinner_restName.getItemIdAtPosition(position));
                Log.e("abc", "rrr getSelectedItem().toString() : " + spinner_restName.getSelectedItem().toString());

                RestData pickData = restList.get(r);
                restData = new RestData(pickData.getRest_id(), pickData.getRest_name(),
                        pickData.getCompound_code(), pickData.getLatLng(), pickData.getPlace_id(), pickData.getRest_img(), pickData.getRest_phone(), pickData.getVicinity(), pickData.getSale(), 0);
                Log.e("abc", "rrrrr restData : " + pickData.getRest_img());

                Picasso.get().load(restData.getRest_img())
                        .placeholder(R.drawable.icon_no_image)
                        .error(R.drawable.icon_no_image)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(img_rest);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Picasso.get().load(restData.getRest_img())
                        .placeholder(R.drawable.icon_no_image)
                        .error(R.drawable.icon_no_image)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(img_rest);
            }
        });


    }


    private DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    //    List<ChatModel.Comment> comments = new ArrayList<>();
    private List<ChatModel.Chats> chats = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    int peopleCount = 0;

    private class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupMessageRecyclerViewAdapter() {
            Log.e("abc", "채팅 리프레쉬");
            getMessageList();
        }

        void getMessageList() {
//            databaseReference = mDatabase.child("groupchats").child(chatRoomCd).child("messages");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("groupchats").child(chatRoomCd).child("messages");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chats.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        String key = item.getKey();
                        ChatModel.Chats comment_origin = item.getValue(ChatModel.Chats.class);
                        chats.add(comment_origin);
                    }

                    Log.e("abc", "comments.size() = " + chats.size());
                    if (chats.size() > 0) {
                        txt_explain_no_chat.setVisibility(View.GONE);
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(chats.size() - 1);
                    } else {
                        txt_explain_no_chat.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_groupchat, parent, false);

            return new GroupMessageViewHodler(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHodler messageViewHolder = ((GroupMessageViewHodler) holder);
            ChatModel.Chats data = chats.get(position);

            long unixTime = (long) chats.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);

            messageViewHolder.bindToPost(messageViewHolder, data);
        }

//        void setReadCounter(final int position, final TextView textView) {
//            if (peopleCount == 0) {
//
//
//                FirebaseDatabase.getInstance().getReference().child("groupchats").child(chatRoomCd).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();
//                        peopleCount = users.size();
//                        int count = peopleCount - chats.get(position).readUsers.size();
//                        if (count > 0) {
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setText(String.valueOf(count));
//                        } else {
//                            textView.setVisibility(View.INVISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//            }else{
//                int count = peopleCount - chats.get(position).readUsers.size();
//                if (count > 0) {
//                    textView.setVisibility(View.VISIBLE);
//                    textView.setText(String.valueOf(count));
//                } else {
//                    textView.setVisibility(View.INVISIBLE);
//                }
//            }
//
//        }

        @Override
        public int getItemCount() {
            return chats.size();
        }

        public class GroupMessageViewHodler extends RecyclerView.ViewHolder {
            UserData userData = new UserData();

            public LinearLayout messageItem_linearlayout_main;
            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public GroupMessageViewHodler(View view) {
                super(view);

                messageItem_linearlayout_main = view.findViewById(R.id.messageItem_linearlayout_main);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);
            }

            public void bindToPost(GroupMessageViewHodler holder, final ChatModel.Chats data) {
                holder.textview_name.setText("하하");

                if (data.uid.equals(my_id)) {
                    // 내가보낸 메세지
                    holder.messageItem_linearlayout_main.setGravity(Gravity.RIGHT);
                    holder.textView_message.setGravity(Gravity.RIGHT);

                    holder.textView_message.setText(data.message);
                    holder.textView_message.setBackgroundResource(R.drawable.border_round_drgr1);
                    holder.linearLayout_destination.setVisibility(View.INVISIBLE);
//                messageViewHolder.textView_message.setTextSize(25);
//                    linearLayout_main.setGravity(Gravity.RIGHT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_left);

                    holder.imageView_profile.setVisibility(View.GONE);
                } else {
                    // 상대방이 보낸 메세지
                    int k = 0;

                    for (int i = 0; i < groupchatUsersList.size(); i++) {
                        if (groupchatUsersList.get(i).getUser_id().equals(data.uid))
                            k = i;
                    }

                    userData = groupchatUsersList.get(k);

                    holder.messageItem_linearlayout_main.setGravity(Gravity.LEFT);
                    holder.textView_message.setGravity(Gravity.LEFT);

                    holder.imageView_profile.setVisibility(View.VISIBLE);
                    Log.e("abc", "사진 URL = " + userData.getImg_url());
                    if(userData.getImg_url()!=null && !userData.getImg_url().isEmpty()) {
                        Picasso.get().load(userData.getImg_url())
                                .placeholder(R.drawable.icon_noprofile_circle)
                                .error(R.drawable.icon_noprofile_circle)
                                .transform(new CircleTransform())
                                .into(holder.imageView_profile);
                    } else {
                        Picasso.get().load(R.drawable.icon_noprofile_circle)
                                .placeholder(R.drawable.icon_noprofile_circle)
                                .error(R.drawable.icon_noprofile_circle)
                                .transform(new CircleTransform())
                                .into(holder.imageView_profile);
//                        holder.imageView_profile.setImageDrawable(ContextCompat.getDrawable(GroupTalkActivity.this, R.drawable.icon_noprofile_circle));
                    }
                    holder.textview_name.setText(userData.getUser_name());
                    holder.linearLayout_destination.setVisibility(View.VISIBLE);
                    holder.textView_message.setBackgroundResource(R.drawable.border_round_drgr1);
                    holder.textView_message.setText(data.message);
//                textView_message.setTextSize(25);
                    holder.linearLayout_main.setGravity(Gravity.LEFT);
//                setReadCounter(position, messageViewHolder.textView_readCounter_right);
                }


            }
        }

        public void clearItemList() {
//            listViewItemList.clear();
            notifyDataSetChanged();
        }
    }
}