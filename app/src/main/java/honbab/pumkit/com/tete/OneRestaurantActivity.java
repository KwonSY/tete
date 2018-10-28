package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.pumkit.com.task.GetPhotoTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.utils.GoogleMapUtil;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class OneRestaurantActivity extends AppCompatActivity {

    private OkHttpClient httpClient;

    public ViewPager viewPager;
//    public ViewPagerAdapter viewPagerAdapter;
    public TextView[] dots;
    public LinearLayout dotsLayout;

    String placeId;
    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_feed);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        Intent intent = getIntent();
        placeId = intent.getStringExtra("placeId");

        LinearLayout layout_profile = (LinearLayout) findViewById(R.id.layout_profile);
        layout_profile.setVisibility(View.GONE);
        Button btn_reserv = (Button) findViewById(R.id.btn_reserv);
        btn_reserv.setVisibility(View.GONE);

        viewPager = (ViewPager) findViewById(R.id.pager_food);
//        viewPagerAdapter = new ViewPagerAdapter(OneRestaurantActivity.this, layouts2, img_arr);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        ButtonUtil.setBackButtonClickListener(this);

        Log.e("abc", "viewPager from OneRestaurant = " + viewPager);
        getPhotoList(placeId);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        new OneRestTask().execute();
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

    public void getPhotoList(String placeId) {
//        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
////        googlePlaceUrl.append("language=ko");
//        googlePlaceUrl.append("placeid=" + placeId);
//        googlePlaceUrl.append("&fields=" + "name,rating,formatted_phone_number,photo");
//        googlePlaceUrl.append("&key=" + getString(R.string.google_maps_api_key));
//        Log.e("abc", "detail = " + googlePlaceUrl);

        String url = GoogleMapUtil.getDetailUrl(OneRestaurantActivity.this, placeId);

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = OneRestaurantActivity.this;
//        dataTransfer[1] = googlePlaceUrl.toString();
        dataTransfer[1] = url;

//        GetPhotoTask getPhotoTask = new GetPhotoTask(viewPager, dotsLayout);
//        getPhotoTask.execute(dataTransfer);
        new GetPhotoTask(viewPager, dotsLayout).execute(dataTransfer);
    }

}