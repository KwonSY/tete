package honbab.voltage.com.tete;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.card.CardStackAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.task.RestLikeTask;
import honbab.voltage.com.task.RestaurantListTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.OkHttpClient;

public class GodTinderActivity extends AppCompatActivity {
    private OkHttpClient httpClient;
    private SessionManager session;

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private CardStackView cardStackView;

    private ArrayList<FeedData> feedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_godtinder);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText("");
        ButtonUtil.setBackButtonClickListener(this);

        manager = new CardStackLayoutManager(GodTinderActivity.this, mCardStackListener);
        manager.setStackFrom(StackFrom.Top);
        manager.setMaxDegree(20f);
        adapter = new CardStackAdapter();
        cardStackView = findViewById(R.id.card_stack_view);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);

        FloatingActionButton skip_button = (FloatingActionButton) findViewById(R.id.skip_button);
        FloatingActionButton like_button = (FloatingActionButton) findViewById(R.id.like_button);
        skip_button.setOnClickListener(mOnClickListener);
        like_button.setOnClickListener(mOnClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            feedList = new RestaurantListTask(GodTinderActivity.this, httpClient).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        CardStackAdapter adapter = new CardStackAdapter(feedList);
        cardStackView.setAdapter(adapter);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.skip_button:
                    SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .setDuration(200)
                            .setInterpolator(new AccelerateInterpolator())
                            .build();
                    manager.setSwipeAnimationSetting(setting);
                    cardStackView.swipe();

                    break;
                case R.id.like_button:
                    SwipeAnimationSetting setting2 = new SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Right)
                            .setDuration(200)
                            .setInterpolator(new AccelerateInterpolator())
                            .build();
                    manager.setSwipeAnimationSetting(setting2);
                    cardStackView.swipe();

                    break;
            }
        }
    };

    private CardStackListener mCardStackListener = new CardStackListener() {
        String like_yn = "";

        @Override
        public void onCardDragging(Direction direction, float ratio) {
//            Log.e("abc", "스와이프1 = ");
        }

        @Override
        public void onCardSwiped(Direction direction) {
            Log.e("abc", "스와이프2 = " + direction);

            if (direction.equals(Direction.Left)) {
                like_yn = "n";
            } else if (direction.equals(Direction.Right)) {
                like_yn = "y";


            }
        }

        @Override
        public void onCardRewound() {
            Log.e("abc", "스와이프3 = ");
        }

        @Override
        public void onCardCanceled() {
            Log.e("abc", "스와이프4 = ");
        }

        @Override
        public void onCardAppeared(View view, int position) {
            Log.e("abc", "스와이프5 = " + position);
            if (position!=0) {
                String rest_id = String.valueOf(feedList.get(position - 1).getRest_id());

                if (!like_yn.equals(""))
                    new RestLikeTask(GodTinderActivity.this, httpClient).execute(rest_id, like_yn);
            }
        }

        @Override
        public void onCardDisappeared(View view, int position) {

        }
    };
}