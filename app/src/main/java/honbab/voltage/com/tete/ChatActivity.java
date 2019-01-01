package honbab.voltage.com.tete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.ServerValue;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import honbab.voltage.com.adapter.ChatAdapter;
import honbab.voltage.com.data.ChatData;
import honbab.voltage.com.data.FcmData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.ReservFeedTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.CustomTimePickerDialog;
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
    private LinearLayoutManager layoutManager;

    public DrawerLayout drawerLayout;
    public TextView title_topbar;
    public RecyclerView recyclerView;
    public ChatAdapter mAdapter;
    private EditText edit_chat;

    private String fromId = Statics.my_id, toId;
    private String toUserName = "상대방", toUserImg, toToken;
    private RestData restData;
//    private String rest_name, rest_phone;

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
//        restData = (RestData) intent.getSerializableExtra("restData");
        restData = (RestData) intent.getParcelableExtra("restData");
        Log.e("abc", "fromId = " + fromId + ", toId = " + toId);
        Log.e("abc", "toUserName = " + toUserName);
        Log.e("abc", "getRest_name = " + restData.getRest_name());
        Log.e("abc", "ChatActivity getLatLng = " + restData.getLatLng());
//        Log.e("abc", "ChatActivity getLat = " + restData.getLat());
//        Log.e("abc", "ChatActivity getLng = " + restData.getLng());
        Log.e("abc", "ChatActivity rest_phone = " + restData.getRest_phone());

        title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(toUserName);

        TextView txt_rest_phone;
        txt_rest_phone = (TextView) findViewById(R.id.txt_rest_phone);
//        txt_rest_phone.setText(restData.getRest_phone());



        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatAdapter(getApplicationContext());
        recyclerView.setAdapter(mAdapter);

//        messages = new ArrayList<>();
        Button btn_eat_with;
        btn_eat_with = (Button) findViewById(R.id.btn_eat_with);
        btn_eat_with.setOnClickListener(mOnClickListener);

        edit_chat = (EditText) findViewById(R.id.edit_chat);

        TextView btn_send = (TextView) findViewById(R.id.btn_send_chat);
        btn_send.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);


        drawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        drawerLayout.addDrawerListener(mDrawerListener);
        setDateTime();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFirebaseDatabase(fromId, toId);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_send_chat:
                    sendMessage("t");//t = text

                    break;
                case R.id.btn_eat_with:
                    //같이먹기 시작
                    if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }

                    break;
                case R.id.txt_date:
                    DatePickerDialog dialog = new DatePickerDialog(ChatActivity.this, dateSetListener, year, month-1, day);
                    dialog.show();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(ChatActivity.this, timeSetListener,
                            hour, min, false);
//                dialog.updateTime();
                    dialog2.show();

                    break;
                case R.id.btn_reserv:
                    sendMessage("a");

                    String[] date = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
                    String[] rest = {restData.getRest_name(), restData.getCompound_code(),
                            String.valueOf(restData.getLatitude()), String.valueOf(restData.getLongtitue()),
                            restData.getPlace_id(), restData.getRest_img(), restData.getRest_phone(), restData.getVicinity()};
                    Log.e("abc", "ChatAct lat = " + restData.getLatitude() +  restData.getLongtitue());

                    Calendar curCal = Calendar.getInstance();
                    long time_setting = calendar.getTimeInMillis();
                    long time_current = curCal.getTimeInMillis();

                    if (time_setting > time_current) {
                        new ReservFeedTask(ChatActivity.this, httpClient, date, rest).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View view, float v) {

        }

        @Override
        public void onDrawerOpened(@NonNull View view) {

        }

        @Override
        public void onDrawerClosed(@NonNull View view) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {
            String state;
            switch (newState) {
                case DrawerLayout.STATE_IDLE:
                    state = "STATE_IDLE";
                    break;
                case DrawerLayout.STATE_DRAGGING:
                    state = "STATE_DRAGGING";
                    break;
                case DrawerLayout.STATE_SETTLING:
                    state = "STATE_SETTLING";
                    break;
                default:
                    state = "unknown!";
            }

//            txtPrompt2.setText(state);
        }
    };

    private void loadFirebaseDatabase(String fromId, String toId) {
        mDatabase.child("user-messages").child(fromId).child(toId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                mDatabase.child("messages").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatData = dataSnapshot.getValue(ChatData.class);
                        chatData.setToUserName(toUserName);
                        chatData.setToUserImg(toUserImg);

//                        recyclerView.setLayoutManager(layoutManager);
//                        mAdapter = new ChatAdapter(getApplicationContext());
//                        mAdapter = new ChatAdapter(ChatActivity.this, arrayList);
                        mAdapter.addItem(chatData.getType(), chatData.getFromId(), chatData.getToId(), chatData.getToUserName(), chatData.getText(),
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

    private void sendMessage(String type) {
        if (type.equals("t")) {
            if (edit_chat.getText().toString().length() > 0) {
                final String message = edit_chat.getText().toString();
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

                FcmData params = new FcmData(toToken, message);
                new ChatFCMTask().execute(params);
            }
        } else if (type.equals("a")) {

            final String message = toUserName + "님과 " +
                    month + "월 " + day + "일 " + hour + "시 "+ min + "분 " +
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

                Request request = new Request.Builder().url(Statics.main_url + "fcm/push_chat.php").post(body).build();

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

    private TextView txt_date, txt_clock;
    public EditText edit_comment;
    private Button btn_reserv;
    private Calendar calendar;
    int year, month, day, hour, min;// Calender 에서 얻는 값
    private void setDateTime() {
        NavigationView nav_view = (NavigationView) findViewById(R.id.nav_view);
        View view = nav_view.getHeaderView(0);

        TextView txt_restName = (TextView) view.findViewById(R.id.txt_restName) ;
        txt_date = (TextView) view.findViewById(R.id.txt_date);
        txt_clock = (TextView) view.findViewById(R.id.txt_clock);
        edit_comment = (EditText) view.findViewById(R.id.edit_comment);
        btn_reserv = (Button) view.findViewById(R.id.btn_reserv);

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
            str_time += String.valueOf(hour-12) + "시 ";
        else
            str_time += String.valueOf(hour) + "시 ";

        if (min < 30) {
            calendar.set(Calendar.MINUTE, 30);
            min = 30;
            str_time += "30분";
        } else {
            calendar.set(Calendar.MINUTE, 0);
            min = 0;
            str_time += "00분";
        }
        str_date = String.valueOf(month) + "/" + String.valueOf(day);

        txt_restName.setText(restData.getRest_name());
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