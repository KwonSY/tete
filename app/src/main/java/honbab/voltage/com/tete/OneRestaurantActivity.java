package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import honbab.voltage.com.task.GetPhotoTask;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class OneRestaurantActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    public ViewPager viewPager;
    public TextView[] dots;
    public LinearLayout dotsLayout;

    public TextView txt_rest_address, txt_rating;
    public Button btn_call;

    private Calendar calendar;

    private String feed_id, rest_name,
            compound_code, place_id, rest_phone, feed_time, vicinity,
            feeder_id, feeder_img, feeder_name;
//    int hour, min;
    public String lat, lng, rest_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_restaurant);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        rest_name = intent.getStringExtra("rest_name");
        compound_code = intent.getStringExtra("compound_code");
        rest_phone = intent.getStringExtra("rest_phone");
        feed_time = intent.getStringExtra("feed_time");
        LatLng latLng = intent.getParcelableExtra("latLng");
        if (latLng!=null) {
            Double d_lat = latLng.latitude;
            Double d_lng = latLng.longitude;
            lat = d_lat.toString();
            lng = d_lng.toString();
        }
        place_id = intent.getStringExtra("place_id");
        vicinity = intent.getStringExtra("vicinity");
        feeder_id = intent.getStringExtra("feeder_id");
        feeder_img = intent.getStringExtra("feeder_img");
        feeder_name = intent.getStringExtra("feeder_name");
        String status = intent.getStringExtra("status");

        Log.e("abc", "OneRestaurant feed_id = " + feed_id);
        Log.e("abc", "OneRestaurant rest_name = " + rest_name);
        Log.e("abc", "OneRestaurant compound_code = " + compound_code);
        Log.e("abc", "OneRestaurant rest_phone = " + rest_phone);
        Log.e("abc", "OneRestaurant latLng = " + latLng);
        Log.e("abc", "OneRestaurant feed_time = " + feed_time);
        Log.e("abc", "OneRestaurant feeder_id = " + feeder_id);
        Log.e("abc", "OneRestaurant vicinity = " + vicinity);
        Log.e("abc", "OneRestaurant feeder_id = " + feeder_id);
        Log.e("abc", "OneRestaurant feeder_img = " + feeder_img);

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(rest_name);


//        layout_slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.layout_slidingPanel);
//        layout_slidingPanel.setVisibility(View.GONE);
//        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("abc", "setFadeOnClickListener" + view.getContext());
//                layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
//            }
//        });
//        edit_comment = (EditText) findViewById(R.id.edit_comment);
//        btn_reserv = (Button) findViewById(R.id.btn_reserv);
//        btn_reserv.setOnClickListener(mOnClickListener);

        Button btn_reserve_feed;
        btn_reserve_feed = (Button) findViewById(R.id.btn_reserve_feed);
        btn_reserve_feed.setVisibility(View.GONE);
        btn_reserve_feed.setOnClickListener(mOnClickListener);
//        btn_show_sliding = (ImageView) findViewById(R.id.btn_show_sliding);
//        btn_show_sliding.setOnClickListener(mOnClickListener);
//        btn_show_sliding.setOnTouchListener(mOnTouchListener);

//        btn_poke = (Button) findViewById(R.id.btn_poke);

        if (feed_id == null || feed_id.equals(null)) {
            // 단순 음식정 정보
            // 같이먹기 x //상단 제거
            Log.e("abc", "onerest xxxxxxxxxxxxxxxxxxxx = " + feed_id);
            LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
            layout_profile.setVisibility(View.GONE);
        } else {
//            title2_topbar.setText(feed_time.substring(5, 16));

            btn_reserve_feed.setVisibility(View.GONE);

//            if (Statics.my_id == null) {
//                btn_poke.setVisibility(View.VISIBLE);
//            } else if (Statics.my_id.equals(feeder_id)) {
//                btn_poke.setVisibility(View.GONE);
//            } else {
//                btn_poke.setVisibility(View.VISIBLE);
//            }

            // 같이먹기 o
            ImageView img_feeder = (ImageView) findViewById(R.id.img_feeder);
            Picasso.get().load(feeder_img)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_feeder);

            TextView txt_feeder_name = (TextView) findViewById(R.id.txt_feeder_name);
            txt_feeder_name.setText(feeder_name);
        }

        btn_call = (Button) findViewById(R.id.btn_call);
        txt_rest_address = (TextView) findViewById(R.id.txt_rest_address);
        txt_rating = (TextView) findViewById(R.id.txt_rating);

        viewPager = (ViewPager) findViewById(R.id.pager_food);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);


        ButtonUtil.setBackButtonClickListener(this);

        //뷰페이저, 디테일도 세팅
        String url = GoogleMapUtil.getDetailUrl(OneRestaurantActivity.this, place_id);
        Log.e("abc", "원레스토랑 url = " + url);
        new GetPhotoTask(OneRestaurantActivity.this, viewPager, dotsLayout).execute(url);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

            }
        }
    };

}