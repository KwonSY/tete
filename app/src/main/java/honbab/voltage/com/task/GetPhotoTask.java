package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import honbab.voltage.com.adapter.ViewPagerAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.network.OnTaskCompleted;
import honbab.voltage.com.tete.DelayHandlerActivity;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.DownloadUrl;
import honbab.voltage.com.widget.PhotoParser;
import okhttp3.OkHttpClient;

public class GetPhotoTask extends AsyncTask<Object, String, String> {
    private String googlePlacesData;
    private String url;
    public Context mContext;
    private OkHttpClient httpClient;

    private OnTaskCompleted mCallback;

    private ViewPager viewPager;
    private ViewPagerAdapter mAdapter;
    private LinearLayout dotsLayout;

    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();
    private ArrayList<String> imgAllArr = new ArrayList<>();

    public String formatted_phone_number = "";
    public String rest_id = "";

    public GetPhotoTask(Context mContext, ViewPager viewPager, LinearLayout dotsLayout) {
        this.mContext = mContext;
        this.viewPager = viewPager;
        this.dotsLayout = dotsLayout;
//        this.mCallback = (OnTaskCompleted) mContext;
    }

    public GetPhotoTask(Context mContext, OkHttpClient httpClient, OnTaskCompleted onTaskCompleted,
                        String rest_id) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.mCallback = onTaskCompleted;

        this.rest_id = rest_id;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Object... objects) {
//        mContext = (Context) objects[0];
        url = (String) objects[0];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String googlePlacesData) {
        super.onPostExecute(googlePlacesData);
        Log.e("abc", "googlePlacesData = " + googlePlacesData);

        String activityName = mContext.getClass().getSimpleName();

        HashMap<String, Object> placeDetailList = null;
        PhotoParser parser = new PhotoParser();
        placeDetailList = parser.parse(googlePlacesData);
        if (activityName.equals("OneRestaurantActivity")) {
            showPlacePhotos(placeDetailList);

//            return null;
        } else if (activityName.equals("DelayHandlerActivity")) {
//            showPlacePhotos2(placeDetailList);
            String formatted_phone_number = showPlacePhotos2(placeDetailList);

            ((DelayHandlerActivity) mContext).formatted_phone_number = formatted_phone_number;

            new UpdateRestPhoneTask(mContext, httpClient).execute(rest_id, formatted_phone_number);

            if (mCallback != null)
                mCallback.onTaskCompleted(formatted_phone_number);
        } else {
//            return null;
        }
    }

    private void showPlacePhotos(HashMap<String, Object> placeDetailList) {
        layouts2.clear();
        img_arr.clear();

        ArrayList<FeedData> photosList = (ArrayList<FeedData>) placeDetailList.get("photos");

        for (int i = 0; i < photosList.size(); i++) {
            if (i<4) {
                if(i==0)
                    ((OneRestaurantActivity) mContext).rest_img = GoogleMapUtil.getPlacePhoto(mContext, photosList.get(i).getRest_img());

                img_arr.add(GoogleMapUtil.getPlacePhoto(mContext, photosList.get(i).getRest_img()));
                layouts2.add(R.layout.slide_food_image);
            }

            imgAllArr.add(GoogleMapUtil.getPlacePhoto(mContext, photosList.get(i).getRest_img()));
        }

        mAdapter = new ViewPagerAdapter(mContext, layouts2, img_arr, imgAllArr);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        addBottomDots(0);

        Log.e("abc", "placeDetailList === " + placeDetailList);
        Log.e("abc", "formatted_phone_number)" + placeDetailList.get("formatted_phone_number").toString());
        Log.e("abc", "fullAddress)" + placeDetailList.get("fullAddress").toString());
        Log.e("abc", "adr_address)" + placeDetailList.get("adr_address").toString());
        ((OneRestaurantActivity) mContext).btn_call.setText("전화하기 " + placeDetailList.get("formatted_phone_number").toString());
        ((OneRestaurantActivity) mContext).txt_rest_address.setText(Html.fromHtml(placeDetailList.get("adr_address").toString()));
        ((OneRestaurantActivity) mContext).txt_rating.setText("평점 " + placeDetailList.get("rating").toString());
    }

    private String showPlacePhotos2(HashMap<String, Object> placeDetailList) {
        formatted_phone_number = placeDetailList.get("formatted_phone_number").toString();

        return formatted_phone_number;
    }

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

    public void addBottomDots(int currentPage) {
//        TextView[] dots = ((OneRestaurantActivity) mContext).dots;
        TextView[] dots;

        String activityName = mContext.getClass().getSimpleName();
        //vvvvvvvvvvvvvvv 다시 짜기
        if (activityName.equals("OneRestaurantActivity")) {
            dots = ((OneRestaurantActivity) mContext).dots;
        } else {
//            dots = ((OneRestaurantActivity) mContext).dots;
        }

        dots = new TextView[layouts2.size()];

        int[] colorsActive = mContext.getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = mContext.getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(mContext);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
//            dots[i].setTextColor(colorsInactive[currentPage]);
            dots[i].setTextColor(ContextCompat.getColor(mContext, R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(ContextCompat.getColor(mContext, R.color.dot_active));
    }
}