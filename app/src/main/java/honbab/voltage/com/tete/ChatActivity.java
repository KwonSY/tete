package honbab.voltage.com.tete;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;
import com.firebase.client.ServerValue;
import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.ChatAdapter;
import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.data.FcmData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.ChatFCMTask;
import honbab.voltage.com.task.CommonRestTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.NetworkUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.CustomTimePickerDialog;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

public class ChatActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private DatabaseReference mDatabase;

    private LinearLayoutManager layoutManager;

    public DrawerLayout drawerLayout;
    public TextView title_topbar, txt_userName, no_chat_txt_userName, no_chat_explain;
    public ImageView topbar_img_user, no_chat_img_user;
    public Button btn_call_rest;
    private ImageView icon_more_dots;

    private LinearLayout layout_no_chat;
    public RecyclerView recyclerView;
    public ChatAdapter mAdapter;
    //채팅 입력창
    private EditText edit_chat;
    private TextView btn_send;

    public String fromId = Statics.my_id;
    public String toId, toUserName = "상대방", toUserImg, toToken;
    private ChatData chatData;
    public RestData restData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_chat);

        if (!NetworkUtil.isOnline(this)) {
            //인터넷이 안 될 때
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.connect_network);
            builder.setPositiveButton(R.string.yes,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent mStartActivity = new Intent(ChatActivity.this, MainActivity2.class);
                            int mPendingIntentId = 123456;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(ChatActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
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
            toId = intent.getStringExtra("toId");
            Statics.to_id = toId;
            if (intent.getParcelableExtra("restData") != null)
                restData = (RestData) intent.getParcelableExtra("restData");
            else
                restData = new RestData();


            //상단바
            title_topbar = (TextView) findViewById(R.id.title_topbar);
            topbar_img_user = (ImageView) findViewById(R.id.topbar_img_user);
            txt_userName = (TextView) findViewById(R.id.txt_userName);
            title_topbar.setText("");
            topbar_img_user.setOnClickListener(mOnClickListener);

            //채팅 없을 때
            layout_no_chat = (LinearLayout) findViewById(R.id.layout_no_chat);
            no_chat_img_user = (ImageView) findViewById(R.id.no_chat_img_user);
            no_chat_txt_userName = (TextView) findViewById(R.id.no_chat_txt_userName);
            no_chat_explain = (TextView) findViewById(R.id.no_chat_explain);

            if (fromId == null) {
                session = new SessionManager(getApplicationContext());
                HashMap<String, String> user = session.getUserDetails();
                Statics.my_id = user.get("my_id");
                Statics.my_username = user.get("my_username");
                Statics.my_gender = user.get("my_gender");
                fromId = Statics.my_id;

                Intent intent1 = new Intent(ChatActivity.this, MainActivity2.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {

                no_chat_img_user.setOnClickListener(mOnClickListener);
            }
            Log.e("abc", "fromId = " + fromId + ", toId = " + toId);
            Log.e("abc", "toUserName = " + toUserName);
            Log.e("abc", "toUserImg = " + toUserImg);
            Log.e("abc", "getRest_name = " + restData.getRest_name());

            Picasso.get().load(toUserImg)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(no_chat_img_user);



            layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            layoutManager.setStackFromEnd(true);
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new ChatAdapter(getApplicationContext());
            recyclerView.setAdapter(mAdapter);

            btn_call_rest = (Button) findViewById(R.id.btn_call_rest);
            btn_call_rest.setText(restData.getRest_phone());
            btn_call_rest.setOnClickListener(mOnClickListener);
            if (restData.getRest_id() == null || restData.getRest_id().equals(null)) {
                btn_call_rest.setVisibility(View.GONE);
            }

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
            loadFirebaseDatabase(fromId, toId);

            new CommonRestTask(ChatActivity.this).execute(toId, null);

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
                case R.id.topbar_img_user:
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("user_id", toId);
                    startActivity(intent);

                    break;
                case R.id.no_chat_img_user:
                    Log.e("abc", " no_chat_img_user 클릭클릭");
                    Intent intent2 = new Intent(ChatActivity.this, ProfileActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("user_id", toId);
                    startActivity(intent2);

                    break;
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
                                case R.id.chat_out:
                                    goOutFirebaseChat(fromId, fromId, toId);

                                    return true;
                                case R.id.report:
                                    Intent intent3 = new Intent(ChatActivity.this, ReportActivity.class);
                                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent3.putExtra("title", "신고하기");
                                    intent3.putExtra("feed_id", "-1");
                                    intent3.putExtra("to_id", toId);
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
                case R.id.txt_date:
                    DatePickerDialog dialog = new DatePickerDialog(ChatActivity.this, dateSetListener, year, month - 1, day);
                    dialog.show();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(ChatActivity.this, timeSetListener,
                            hour, min, false);
//                dialog.updateTime();
                    dialog2.show();

                    break;
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

    private void loadFirebaseDatabase(String fromId, String toId) {
        mAdapter.clearItemList();
        final String[] chatoutYn = {"e"};

        mDatabase.child("user-messages").child(fromId).child(toId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //읽음 처리
                mDatabase.child("user-messages").child(fromId).child(toId).child(dataSnapshot.getKey()).setValue(0);

                mDatabase.child("messages").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatData = dataSnapshot.getValue(ChatData.class);
                        chatData.setToUserName(toUserName);
                        chatData.setToUserImg(toUserImg);

                        mAdapter.addItem(chatData.getType(), chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(),
                                chatData.getTimestampLong(),
                                chatData.getImageUrl(), chatData.getImageWidth(), chatData.getImageHeight(), toUserImg);
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();

                        layout_no_chat.setVisibility(View.GONE);

                        if (chatData.getType().equals("a")) {
                            chatoutYn[0] = "logout";

                            btn_send.setBackgroundResource(R.color.whitegrey);
                            btn_send.setEnabled(false);
                        } else if (chatoutYn[0].equals("logout") && chatData.getToId().equals(Statics.my_id)) {
                            chatoutYn[0] = "n";
                        } else if (chatoutYn[0].equals("logout") && chatData.getFromId().equals(Statics.my_id)) {
                            chatoutYn[0] = "y";
                        }

                        Log.e("abc", "chatoutYn[0] = " + chatoutYn[0]);
                        if (chatoutYn[0].equals("y")) {
                            btn_send.setBackgroundResource(R.color.whitegrey);
                            btn_send.setEnabled(false);
                        } else if (chatoutYn[0].equals("n")) {
                            btn_send.setBackgroundResource(R.drawable.btn_send_or);
                            btn_send.setEnabled(true);
                        }
//                        if (mAdapter.getItemCount() == )
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
                btn_send.setBackgroundResource(R.color.whitegrey);
                btn_send.setEnabled(false);
//                mDatabase.child("messages").child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void goOutFirebaseChat(String fromId, String fromUserName, String toId) {

        if (mAdapter.getLastType().equals("a")) {
            Query query = mDatabase.child("user-messages").child(fromId).child(toId);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        i++;

                        mDatabase.child("messages").child(child.getKey()).removeValue();

                        if (i >= dataSnapshot.getChildrenCount()) {
                            mDatabase.child("user-messages").child(fromId).child(toId).removeValue();

                            finish();
                            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            String type = "a";
            String message = "상대방이 채팅방을 나갔습니다.";
            HashMap<String, Object> timestamp = new HashMap<>();
            timestamp.put("time", ServerValue.TIMESTAMP);

            ChatData chatData = new ChatData(type, fromId, toId, message, timestamp);

            DatabaseReference id_message = mDatabase.child("messages").push();
            mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);

            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put(id_message.getKey(), 1);

            mDatabase.child("user-messages").child(String.valueOf(toId)).child(String.valueOf(fromId)).updateChildren(taskMap);
            mDatabase.child("user-messages").child(fromId).child(toId).removeValue();
        }

        finish();
        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendMessage(String type) {
        String message = "";

        if (type.equals("t")) {
            if (edit_chat.getText().toString().length() > 0) {
                message = edit_chat.getText().toString();
                final HashMap<String, Object> timestamp = new HashMap<>();
                timestamp.put("time", ServerValue.TIMESTAMP);

                ChatData chatData = new ChatData(type, fromId, toId, message, timestamp);

                DatabaseReference id_message = mDatabase.child("messages").push();
                mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);

                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put(id_message.getKey(), 1);

                mDatabase.child("user-messages").child(String.valueOf(fromId)).child(String.valueOf(toId)).updateChildren(taskMap);
                mDatabase.child("user-messages").child(String.valueOf(toId)).child(String.valueOf(fromId)).updateChildren(taskMap);

                chatData.setToUserName(toUserName);

                recyclerView.setLayoutManager(layoutManager);
                mAdapter.notifyDataSetChanged();
                edit_chat.setText("");

                FcmData params = new FcmData(toToken, Statics.my_id, Statics.my_username, message);
                new ChatFCMTask().execute(params);
            }
        } else if (type.equals("a")) {

            message = toUserName + "님과 " +
                    month + "월 " + day + "일 " + hour + "시 " +
//                    min + "분 " +
                    restData.getRest_name() + "에서 식사가 예약되었습니다.";
            final HashMap<String, Object> timestamp = new HashMap<>();
            timestamp.put("time", ServerValue.TIMESTAMP);

            ChatData chatData = new ChatData(type, fromId, toId, message, timestamp);

            DatabaseReference id_message = mDatabase.child("messages").push();
            mDatabase.child("messages").child(id_message.getKey()).setValue(chatData);

            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put(id_message.getKey(), 1);

            mDatabase.child("user-messages").child(String.valueOf(fromId)).child(String.valueOf(toId)).updateChildren(taskMap);
            mDatabase.child("user-messages").child(String.valueOf(toId)).child(String.valueOf(fromId)).updateChildren(taskMap);

            chatData.setToUserName(toUserName);

            recyclerView.setLayoutManager(layoutManager);
            mAdapter.notifyDataSetChanged();
            edit_chat.setText("");

            FcmData params = new FcmData(toToken, Statics.my_id, Statics.my_username, message);
            new ChatFCMTask().execute(params);
        }
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

//                new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int r = 0;
//
//                for (int i=0; i<restList.size(); i++) {
//                    if (restList.get(i).getRest_name().equals(spinner_restName.getItemIdAtPosition(position)))
//                        r = i;
//                }
//                RestData pickData = restList.get(r);
//                restData = new RestData(pickData.getRest_id(), pickData.getRest_name(),
//                        pickData.getCompound_code(), pickData.getLatLng(), pickData.getPlace_id(), pickData.getRest_img(), pickData.getRest_phone(), pickData.getVicinity());
//
//                Picasso.get().load(restData.getRest_img())
//                        .placeholder(R.drawable.icon_no_image)
//                        .error(R.drawable.icon_no_image)
//                        .into(img_rest);
//            }
//        });

        try {
            restList = new CommonRestTask(ChatActivity.this).execute(toId, restData.getRest_id()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Date currentTime = new Date();
//        calendar = GregorianCalendar.getInstance();
        calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 2);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        int ampm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

        String str_date, str_time;
        if (ampm == 0) {
            str_time = "오전 ";
        } else {
            str_time = "오후 ";
        }

        if (hour > 12)
            str_time += String.valueOf(hour - 12) + "시 ";
        else
            str_time += String.valueOf(hour) + "시 ";

//        if (min < 30) {
//            calendar.set(Calendar.MINUTE, 30);
//            min = 30;
//            str_time += "30분";
//        } else {
//            calendar.set(Calendar.MINUTE, 0);
//            min = 0;
//            str_time += "00분";
//        }
        calendar.set(Calendar.MINUTE, 0);
        min = 0;
        str_date = String.valueOf(month) + "/" + String.valueOf(day);

        txt_date.setText(str_date);
        txt_date.setOnClickListener(mOnClickListener);
        txt_clock.setText(str_time);
        txt_clock.setOnClickListener(mOnClickListener);
        btn_reserv.setOnClickListener(mOnClickListener);
    }

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1 + 1;
            day = i2;

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            txt_date.setText(month + "/" + i2);
        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String str_time;

            hour = hourOfDay;
            min = minute;

            if (hourOfDay < 12) {
                calendar.set(Calendar.AM_PM, 0);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                str_time = "오전 " + hourOfDay + "시 " + minute + "분";
            } else {
                calendar.set(Calendar.AM_PM, 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
                }

                str_time = "오후 " + hourOfDay + "시 " + minute + "분";
            }

            txt_clock.setText(str_time);
        }
    };
}