package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import honbab.pumkit.com.task.GetPhotoTask;
import honbab.pumkit.com.task.PokeFeedTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.utils.GoogleMapUtil;
import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.*;

public class OneRestaurantActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    public ViewPager viewPager;
    public TextView[] dots;
    public LinearLayout dotsLayout;

    public SlidingUpPanelLayout layout_slidingPanel;

    public ImageView btn_reserv;
    public Button btn_poke;
    public TextView txt_comment, txt_rest_phone, txt_rest_address, txt_rating;

    private String feed_id, feed_rest_name, place_id, feeder_img, feeder_name, poke_yn = "n", status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_restaurant);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        feed_rest_name = intent.getStringExtra("feed_rest_name");
        place_id = intent.getStringExtra("place_id");
        feeder_img = intent.getStringExtra("feeder_img");
        feeder_name = intent.getStringExtra("feeder_name");
        status = intent.getStringExtra("status");

        TextView title_topbar;
        title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(feed_rest_name);

        layout_slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.layout_slidingPanel);
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "setFadeOnClickListener" + view.getContext());
                layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
            }
        });
        ImageView btn_reserv;
        btn_reserv = (ImageView) findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

        btn_poke = (Button) findViewById(R.id.btn_poke);

        if (feed_id == null || feed_id.equals("")) {
            // 단순 음식정 정보
            // 같이먹기 x //상단 제거
            LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
            layout_profile.setVisibility(View.GONE);

            btn_poke.setVisibility(View.GONE);
        } else {
            // 같이먹기 o
            ImageView img_feeder = (ImageView) findViewById(R.id.img_feeder);
            Picasso.get().load(feeder_img)
//                    .resize(100,100)
//                    .centerCrop()
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_feeder);

            TextView txt_feeder_name = (TextView) findViewById(R.id.txt_feeder_name);
            txt_feeder_name.setText(feeder_name);

            if (status.equals("y")) {
                btn_poke.setText(R.string.poke_compelete);
                btn_poke.setBackgroundResource(R.drawable.border_circle_gr2);
                btn_poke.setClickable(false);
            } else {
                btn_poke.setOnClickListener(mOnClickListener);
            }
        }

        txt_comment = (TextView) findViewById(R.id.txt_comment);
        txt_rest_phone = (TextView) findViewById(R.id.txt_rest_phone);
        txt_rest_address = (TextView) findViewById(R.id.txt_rest_address);
        txt_rating = (TextView) findViewById(R.id.txt_rating);

        viewPager = (ViewPager) findViewById(R.id.pager_food);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        ButtonUtil.setBackButtonClickListener(this);

        //뷰페이저, 디테일도 세팅
        String url = GoogleMapUtil.getDetailUrl(OneRestaurantActivity.this, place_id);
        new GetPhotoTask(viewPager, dotsLayout).execute(OneRestaurantActivity.this, url);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (feed_id == null || feed_id.equals("")) {

        } else {
            new CheckMyPokeTask().execute();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_poke:
                    if (status.equals("n")) {
                        if (Statics.my_id == null) {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            new PokeFeedTask(OneRestaurantActivity.this, httpClient).execute(feed_id);

                            if (poke_yn.equals("n")) {
                                Toast.makeText(OneRestaurantActivity.this, "같이먹기를 신청하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OneRestaurantActivity.this, "같이먹기가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    break;
                case R.id.btn_reserv:
                    layout_slidingPanel.setPanelState(PanelState.ANCHORED);

                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (layout_slidingPanel != null) {
            if (layout_slidingPanel.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_toggle: {
                if (layout_slidingPanel != null) {
                    if (layout_slidingPanel.getPanelState() != PanelState.HIDDEN) {
                        layout_slidingPanel.setPanelState(PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                return true;
            }
            case R.id.action_anchor: {
                if (layout_slidingPanel != null) {
                    if (layout_slidingPanel.getAnchorPoint() == 1.0f) {
                        layout_slidingPanel.setAnchorPoint(0.7f);
                        layout_slidingPanel.setPanelState(PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        layout_slidingPanel.setAnchorPoint(1.0f);
                        layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class CheckMyPokeTask extends AsyncTask<Void, Void, Void> {
        String result;
        String comment;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "one_feed_feedeelist")
                    .add("feed_id", feed_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "one_feed_feedeelist json = " + obj);

                    result = obj.getString("result");
                    status = obj.getString("status");
                    comment = obj.getString("comment");

                    if (!obj.isNull("users")) {
                        JSONArray arr = obj.getJSONArray("users");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj2 = arr.getJSONObject(i);

                            String user_id = obj2.getString("sid");
                            String user_status = obj2.getString("status");

                            Log.e("abc", "xxx my_id = " + Statics.my_id);
                            if (user_id.equals(Statics.my_id)) {
                                poke_yn = "y";
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(feedReqList);
            Log.e("abc", "r = " + result);
            Log.e("abc", "s = " + status);
            Log.e("abc", "poke_yn = " + poke_yn);
            Log.e("abc", "comment.length() = " + comment.length());

            if (comment.equals("null")) {
                txt_comment.setVisibility(View.GONE);
            } else {
                txt_comment.setVisibility(View.VISIBLE);
                txt_comment.setText(comment);
            }

            //미수락 상태에서, poke_yn check
            if (status.equals("n")) {
                if (poke_yn.equals("n")) {
                    btn_poke.setText(R.string.poke_reserve);
                    btn_poke.setBackgroundResource(R.drawable.border_circle_bk1);
                } else {
                    btn_poke.setText(R.string.poke_reserved);
                    btn_poke.setBackgroundResource(R.drawable.border_circle_gr2);
                }
            }
        }

    }

}