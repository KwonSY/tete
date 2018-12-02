package honbab.pumkit.com.tete;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import honbab.pumkit.com.task.GetPhotoTask;
import honbab.pumkit.com.task.PokeFeedTask;
import honbab.pumkit.com.task.ReservFeedTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.utils.GoogleMapUtil;
import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.CustomTimePickerDialog;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class OneRestaurantActivity extends AppCompatActivity {
    private OkHttpClient httpClient;

    public ViewPager viewPager;
    public TextView[] dots;
    public LinearLayout dotsLayout;

    public SlidingUpPanelLayout layout_slidingPanel;
    public EditText edit_comment;
    private Button btn_reserv;

    public ImageView btn_show_sliding;
    public Button btn_poke;
    public TextView txt_comment, txt_rest_phone, txt_rest_address, txt_rating;
    private TextView txt_date, txt_clock;

    private String feed_id, feed_rest_name, place_id, feeder_id, feeder_img, feeder_name, poke_yn = "n", status;
//    int hour, min;
    public String lat, lng, rest_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_restaurant);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        feed_rest_name = intent.getStringExtra("feed_rest_name");
        place_id = intent.getStringExtra("place_id");
        LatLng latLng = intent.getParcelableExtra("latLng");
        if (latLng!=null) {
            Double d_lat = latLng.latitude;
            Double d_lng = latLng.longitude;
            lat = d_lat.toString();
            lng = d_lng.toString();
            Log.e("abc", "lat,lng = (" + lat + "," + lng);
        }
        feeder_id = intent.getStringExtra("feeder_id");
        feeder_img = intent.getStringExtra("feeder_img");
        feeder_name = intent.getStringExtra("feeder_name");
        status = intent.getStringExtra("status");

        TextView title_topbar = (TextView) findViewById(R.id.title_topbar);
        title_topbar.setText(feed_rest_name);

        layout_slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.layout_slidingPanel);
        layout_slidingPanel.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "setFadeOnClickListener" + view.getContext());
                layout_slidingPanel.setPanelState(PanelState.COLLAPSED);
            }
        });
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        btn_reserv = (Button) findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

        btn_show_sliding = (ImageView) findViewById(R.id.btn_show_sliding);
        btn_show_sliding.setOnClickListener(mOnClickListener);
        btn_show_sliding.setOnTouchListener(mOnTouchListener);

        btn_poke = (Button) findViewById(R.id.btn_poke);

        if (feed_id == null || feed_id.equals("")) {
            // 단순 음식정 정보
            // 같이먹기 x //상단 제거
            LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
            layout_profile.setVisibility(View.GONE);

            btn_poke.setVisibility(View.GONE);
        } else {
            Log.e("abc", "Statics.my_id = " + Statics.my_id);
            LinearLayout layout_show_sliding = (LinearLayout) findViewById(R.id.layout_show_sliding_reserv);
            layout_show_sliding.setVisibility(View.GONE);

            if (Statics.my_id == null || Statics.my_id.equals(feeder_id)) {

                btn_poke.setVisibility(View.GONE);
            }

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

        setDateTime();


        ButtonUtil.setBackButtonClickListener(this);

        //뷰페이저, 디테일도 세팅
        String url = GoogleMapUtil.getDetailUrl(OneRestaurantActivity.this, place_id);
        Log.e("abc", "원레스토랑 url = " + url);
        new GetPhotoTask(viewPager, dotsLayout).execute(OneRestaurantActivity.this, url);
    }

    int year, month, day, hourOfDay, minute;
    int hour = 0, min = 0;// Task post로 넘길 값
    private void setDateTime() {
        txt_date = (TextView) findViewById(R.id.txt_date);
        txt_clock = (TextView) findViewById(R.id.txt_clock);

        Date currentTime = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(currentTime);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        String str_date, str_time;
        if (hourOfDay < 12) {
            hour = 12;
            min = 0;
            str_time = "오후 " + 12 + "시 " + "00분";
        } else {
            if (hourOfDay < 18) {
                hour = 19;
                min = 0;
                str_time = "오후 " + 7 + "시 " + "00분";
            } else {
                if (hourOfDay > 22 && minute >= 30)
                    day = day + 1;

                hour = hourOfDay + 1;
                str_time = "오후 " + (hour-12) + "시 ";

                if (minute < 30) {
                    min = 30;
                    str_time += "30분";
                } else {
                    min = 0;
                    str_time += "00분";
                }
            }
        }
        str_date = String.valueOf(month) + "/" + String.valueOf(day);
        txt_date.setText(str_date);
        txt_date.setOnClickListener(mOnClickListener);

        txt_clock = (TextView) findViewById(R.id.txt_clock);
        txt_clock.setText(str_time);
        txt_clock.setOnClickListener(mOnClickListener);
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
                case R.id.btn_show_sliding:
                    layout_slidingPanel.setPanelState(PanelState.ANCHORED);

                    break;
                case R.id.txt_date:
                    DatePickerDialog dialog = new DatePickerDialog(OneRestaurantActivity.this, dateSetListener, year, month-1, day);
                    dialog.show();

                    break;
                case R.id.txt_clock:
                    CustomTimePickerDialog dialog2 = new CustomTimePickerDialog(OneRestaurantActivity.this, timeSetListener, hourOfDay, minute, false);
                    dialog2.show();

                    break;
                case R.id.btn_reserv:
                    String[] date = {String.valueOf(year), String.valueOf(month), String.valueOf(day), String.valueOf(hour), String.valueOf(min)};
                    Log.e("abc", "date" + Arrays.deepToString(date));
                    String[] rest = {feed_rest_name, place_id, lat, lng, rest_img};
                    Log.e("abc", "rest" + Arrays.deepToString(rest));

                    new ReservFeedTask(OneRestaurantActivity.this, httpClient, date, rest).execute();

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    btn_show_sliding.setBackgroundResource(R.drawable.icon_check_y);

                    break;
                case MotionEvent.ACTION_UP:
                    btn_show_sliding.setBackgroundResource(R.drawable.icon_check_y);

                    break;
            }
            return false;
        }
    };

    public DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            String str_date;

            Log.e("abc", i+ "년" + i1 + "월" + i2+ "일");

            year = i;
            month = i1 + 1;
            day = i2;

            txt_date.setText(month + "/" + i2);
        }
    };

    public TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // 설정버튼 눌렀을 때
//            Log.e("abc", "hourOfDay = " + hourOfDay);
            String str_time;

            hour = hourOfDay;
            min = minute;

            if (hourOfDay < 12) {
                str_time = "오전 " + hourOfDay + "시 " + minute + "분";
            } else {
                if (hourOfDay == 12) {

                } else {
                    hourOfDay = hourOfDay - 12;
//                    hour = hourOfDay - 12;
                }

                str_time = "오후 " + hourOfDay + "시 " + minute + "분";
            }
            txt_clock.setText(str_time);
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