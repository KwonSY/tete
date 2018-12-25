package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.task.MyFeedListTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class MyFeedListActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    public TextView txt_no_feed;
    public Button btn_go_reserv;
    public RecyclerView recyclerView_myFeed;
    public MyFeedListAdapter myFeedListAdapter;

    private ArrayList<FeedReqData> feedReqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfeedlist);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(R.string.require_list);

        txt_no_feed = (TextView) findViewById(R.id.txt_no_feed);
        btn_go_reserv = (Button) findViewById(R.id.btn_go_reserv);
        btn_go_reserv.setOnClickListener(mOnClickListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView_myFeed = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView_myFeed.setLayoutManager(layoutManager);
        myFeedListAdapter = new MyFeedListAdapter();
        recyclerView_myFeed.setAdapter(myFeedListAdapter);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyFeedListTask(MyFeedListActivity.this, httpClient).execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
                case R.id.btn_go_reserv:
                    Intent intent = new Intent(MyFeedListActivity.this, ReservActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
            }
        }
    };
}