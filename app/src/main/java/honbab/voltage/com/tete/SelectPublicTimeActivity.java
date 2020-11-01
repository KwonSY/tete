package honbab.voltage.com.tete;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.SelectPublicAreaAdapter;
import honbab.voltage.com.adapter.SelectPublicDateListAdapter;
import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.data.ChatModel;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.CityListTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import honbab.voltage.com.widget.SpacesItemDecoration;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

public class SelectPublicTimeActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;
    private DatabaseReference mDatabase;

    public RecyclerView recyclerView_time;
    public RecyclerView recyclerView_area;
    public SelectPublicDateListAdapter mAdapter;
    public SelectPublicAreaAdapter mAdapter_area;

    public String dateTime = "";
    public String areaCd = "";
    public String chatRoomCd = "";
    private ArrayList<AreaData> areaList;
    public ArrayList<UserData> groupchatUsersList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_select_public_time);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(R.string.select_date_and_area);

        //날짜선택
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
//        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd 21:00:00");
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd 21:00:00");
//        SimpleDateFormat formatter2 = new SimpleDateFormat("d일");
//        calendar.setTime(formatter1.parse(clone().));

        String today = dateFormat1.format(date);
        String tomorrow = dateFormat1.format(calendar.getTime());

        //String timelike_id, String time, String timeName, String day_of_week, ArrayList<RestData> restList, int cnt, String status
        ArrayList<SelectDateData> dateList = new ArrayList<>();
        SelectDateData dateData = new SelectDateData(null, today.toString(), "오늘", "", null, 0, null);
        SelectDateData dateData2 = new SelectDateData(null, tomorrow.toString(), "내일", "", null, 0, null);
        dateList.add(dateData);
        dateList.add(dateData2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_time = (RecyclerView) findViewById(R.id.recyclerView_time);
        recyclerView_time.setLayoutManager(layoutManager);
        mAdapter = new SelectPublicDateListAdapter(SelectPublicTimeActivity.this, dateList);
        recyclerView_time.setAdapter(mAdapter);
        while (recyclerView_time.getItemDecorationCount() > 0) {
            recyclerView_time.removeItemDecorationAt(0);
        }
        recyclerView_time.addItemDecoration(new SpacesItemDecoration(18));


        //장소선택
        GridLayoutManager layoutManager2 = new GridLayoutManager(this, 2);
        recyclerView_area = (RecyclerView) findViewById(R.id.recyclerView_area);
        recyclerView_area.setLayoutManager(layoutManager2);
        mAdapter_area = new SelectPublicAreaAdapter();
//        mAdapter_area = new SelectPublicAreaAdapter(SelectPublicTimeActivity.this, dateList);
        recyclerView_area.setAdapter(mAdapter_area);
        while (recyclerView_area.getItemDecorationCount() > 0) {
            recyclerView_area.removeItemDecorationAt(0);
        }
        recyclerView_area.addItemDecoration(new SpacesItemDecoration(18));

        Button btn_go_select_area = (Button) findViewById(R.id.btn_go_select_area);
        btn_go_select_area.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            areaList = new CityListTask(SelectPublicTimeActivity.this).execute().get();

            if (chatRoomCd.equals("") || chatRoomCd == null) {
                chatRoomCd = areaList.get(0).getArea_cd();

                mDatabase.child("groupchats").child(chatRoomCd).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                UserData userData = new AccountTask(SelectPublicTimeActivity.this).execute(userSnapshot.getKey()).get();
//                                Log.e("abc", "userSnapshot.getValue(); = " + userSnapshot.getKey());

                                groupchatUsersList.add(userData);
                            }

                            Log.e("abc", "사전에 저장 = " + groupchatUsersList.size());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

//                        Map<String, Object> userObj;
//                        Map<String, Object> userObj2 = dataSnapshot.getValue(userObj);
//                        userObj.g
//                        Log.e("abc", "dataSnapshot.getValue() = " + dataSnapshot.getValue());
//                        Map<String, Object> userObj = mDatabase.child("groupchats").child(chatRoomCd).child("users").child(dataSnapshot.getKey()).;
//                        Log.e("abc", "dataSnapshot.getKey() = " + dataSnapshot.getKey());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            mAdapter_area = new SelectPublicAreaAdapter(SelectPublicTimeActivity.this, areaList);
            recyclerView_area.setAdapter(mAdapter_area);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    ChatModel chatModel = new ChatModel();

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_select_area:
                    if (Integer.parseInt(Statics.my_id) < 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPublicTimeActivity.this);
                        builder.setMessage("로그인이 필요합니다. 로그인하시겠습니까?");
                        builder.setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        new DelFrTask(SelectPublicTimeActivity.this).execute(user_id);
                                        Intent intent = new Intent(SelectPublicTimeActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectPublicTimeActivity.this);
                        builder.setMessage("<모여서먹어요>에 참여하시겠습니까?");
                        builder.setPositiveButton(R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
//                                        new DelFrTask(SelectPublicTimeActivity.this).execute(user_id);

                                        // before
//                                        chatModel.users.put(Statics.my_id, true);
//                                        FirebaseDatabase.getInstance().getReference().child("groupchats").child("gangnam1").setValue(chatModel);

                                        chatModel.users.put(Statics.my_id, true);
                                        mDatabase.child("groupchats").child(chatRoomCd).child("users").updateChildren(chatModel.users);

                                        finish();
                                        Intent intent = new Intent(SelectPublicTimeActivity.this, GroupTalkActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("chatRoomCd", chatRoomCd);
                                        intent.putParcelableArrayListExtra("groupchatUsersList", groupchatUsersList);
                                        startActivity(intent);
                                    }
                                });
                        builder.setNegativeButton(R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.show();


                    }

                    break;
            }
        }
    };
}