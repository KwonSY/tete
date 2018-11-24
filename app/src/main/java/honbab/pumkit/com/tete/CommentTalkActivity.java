package honbab.pumkit.com.tete;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedCommentAdapter;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.task.MyFeedCommentTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class CommentTalkActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    public TextView title_reserve;
    public TextView txt_no_comment;
    public RecyclerView recyclerView;
    public MyFeedCommentAdapter mAdapter;

    private ArrayList<FeedReqData> feedReqList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commenttalk);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        title_reserve = (TextView) findViewById(R.id.title_reserve);
        txt_no_comment = (TextView) findViewById(R.id.txt_no_comment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new MyFeedCommentAdapter();
        recyclerView.setAdapter(mAdapter);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MyFeedCommentTask(CommentTalkActivity.this, httpClient).execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
            }
        }
    };
}