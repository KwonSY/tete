package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.ViewPagerAdapter;
import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.task.GetPhotoTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.utils.GoogleMapUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OneFeedActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    public ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    public TextView[] dots;
    public LinearLayout dotsLayout;
    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();
    int chk = 0;

    String feed_id, place_id;
    public ArrayList<MapData> mMapList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_restaurant);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");
        place_id = intent.getStringExtra("place_id");
        Log.e("abc", "feed_id = " + feed_id);
        Log.e("abc", "place_id = " + place_id);

        //같이먹기 버튼
        Button btn_reserv = (Button) findViewById(R.id.btn_reserv);
        btn_reserv.setOnClickListener(mOnClickListener);

        viewPager = (ViewPager) findViewById(R.id.pager_food);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        ButtonUtil.setBackButtonClickListener(this);

        String url = GoogleMapUtil.getDetailUrl(OneFeedActivity.this, place_id);
        new GetPhotoTask(viewPager, dotsLayout).execute(OneFeedActivity.this, url);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new OneFeedTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
//                case R.id.btn_reserv:
//                    if (chk==0) {
//                        chk++;
//                        new PokeFeedTask().execute();
//                    }
//
//                    break;
            }
        }
    };

/*
    public void addBottomDots(int currentPage) {
        dots = new TextView[layouts2.size()];
        Log.e("abc", "dots.length = " + dots.length);

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
//            dots[i].setTextColor(colorsInactive[currentPage]);
            dots[i].setTextColor(ContextCompat.getColor(this, R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(this, R.color.dot_active));
    }
*/
/*
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
//            if (position == layouts.length - 1) {
            if (position == layouts2.size() - 1) {
                // last page. make button text to GOT IT

            } else {
                // still pages are left

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    */
/*
    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts2.get(position), container, false);

            ImageView img_food = (ImageView) view.findViewById(R.id.img_food);

            Log.e("abc", "img_arr.size() = " + img_arr.size());
            if (position < img_arr.size()) {
                Picasso.get().load(img_arr.get(position)).placeholder(R.drawable.icon_noprofile_circle).into(img_food);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts2.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
*/

    //피드 한 개 보기
    public class OneFeedTask extends AsyncTask<Void, Void, Void> {
        private int[] layouts;

        @Override
        protected void onPreExecute() {
            img_arr.clear();
            layouts2.clear();

            viewPager = (ViewPager) findViewById(R.id.pager_food);
            dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

            layouts = new int[]{R.layout.slide_food_image};
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "one_feed")
                    .add("reserv_id", "1")
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "one feed json : " + obj);

                    JSONObject obj2 = obj.getJSONObject("reserv");

                    String sid = obj2.getString("sid");

                    JSONObject user_obj = obj2.getJSONObject("user");
                    String user_id = user_obj.getString("sid");
                    String user_name = user_obj.getString("name");
                    String user_img = user_obj.getString("img_url");

                    JSONObject rest_obj = obj2.getJSONObject("rest");
                    String food_id = rest_obj.getString("sid");
                    String food_name = rest_obj.getString("name");
//                    JSONArray rest_img_arr = rest_obj.getJSONArray("url");
//                    for (int i=0; i<rest_img_arr.length(); i++) {
//                        String rest_img = rest_img_arr.get(i).toString();
//                        if (rest_img != null && !rest_img.isEmpty())
//                            rest_img = Statics.main_url + rest_img;
//                    }
                    String img_url = Statics.main_url + "/image/background/sample_1.jpg";
                    img_arr.add(img_url);
                    layouts2.add(R.layout.slide_food_image);
                    String img_url2 = Statics.main_url + "image/background/sample_2.jpg";
                    img_arr.add(img_url2);
                    layouts2.add(R.layout.slide_food_image);
                    String img_url3 = Statics.main_url + "image/background/sample_3.jpg";
                    img_arr.add(img_url3);
                    layouts2.add(R.layout.slide_food_image);

                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            viewPagerAdapter = new ViewPagerAdapter();
            viewPager.setAdapter(viewPagerAdapter);
//            viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

//            addBottomDots(0);
        }
    }

//    //같이먹기 신청
//    public class PokeFeed2Task extends AsyncTask<Void, Void, Void> {
//        String result;
//
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            FormBody body = new FormBody.Builder()
//                    .add("opt", "poke")
//                    .add("my_id", Statics.my_id)
//                    .add("feed_id", feed_id)
//                    .build();
//
//            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();
//
//            try {
//                okhttp3.Response response = httpClient.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    String bodyStr = response.body().string();
//
//                    JSONObject obj = new JSONObject(bodyStr);
//
//                    result = obj.getString("result");
//                } else {
////                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
//                }
//
//            } catch (Exception e) {
//                Log.e("abc", "Error : " + e.getMessage());
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            if (result.equals("0")) {
//                Intent intent = new Intent(OneFeedActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        }
//    }
}