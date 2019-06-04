package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.adapter.SelectPublicAreaAdapter;
import honbab.voltage.com.adapter.SelectPublicDateListAdapter;
import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import honbab.voltage.com.widget.SpacesItemDecoration;
import okhttp3.OkHttpClient;

public class SelectPublicTimeActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    public RecyclerView recyclerView_time;
    public RecyclerView recyclerView_area;
    public SelectPublicDateListAdapter mAdapter;
    public SelectPublicAreaAdapter mAdapter_area;

    public String dateTime = "";
    public String areaCd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_public_time);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

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
//        Date today2 = dateFormat1.format(calendar);
        String tomorrow = dateFormat1.format(calendar.getTime());
//        Date tomorrow = formatter1.parse(data.getTime());
//        Date date = formatter1.parse(data.getTime());
//        String str_feed_time = formatter2.format(date);
        Log.e("abc", "today = " + today.toString());
        Log.e("abc", "tomorrow = " + tomorrow.toString());

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
        mAdapter_area = new SelectPublicAreaAdapter(SelectPublicTimeActivity.this, dateList);
        recyclerView_area.setAdapter(mAdapter_area);

        Button btn_go_select_area = (Button) findViewById(R.id.btn_go_select_area);
        btn_go_select_area.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new MySettingTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_select_area:
                    Log.e("abc", "my_id = " + Statics.my_id);
                    if (Integer.parseInt(Statics.my_id) < 0) {
                        Intent intent = new Intent(SelectPublicTimeActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.putExtra("dateTime", dateTime);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(SelectPublicTimeActivity.this, SelectPublicAreaActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("dateTime", dateTime);
                        startActivity(intent);
                    }

                    break;
            }
        }
    };
}