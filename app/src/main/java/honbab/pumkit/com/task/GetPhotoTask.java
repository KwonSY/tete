package honbab.pumkit.com.task;

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

import honbab.pumkit.com.adapter.ViewPagerAdapter;
import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.utils.GoogleMapUtil;
import honbab.pumkit.com.widget.DownloadUrl;
import honbab.pumkit.com.widget.PhotoParser;

public class GetPhotoTask extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    String url;
    public Context mContext;

    ViewPager viewPager;
    ViewPagerAdapter mAdapter;
    LinearLayout dotsLayout;

    private ArrayList<Integer> layouts2 = new ArrayList<>();
    private ArrayList<String> img_arr = new ArrayList<>();
    private ArrayList<String> imgAllArr = new ArrayList<>();

    public GetPhotoTask(ViewPager viewPager, LinearLayout dotsLayout) {
        this.viewPager = viewPager;
        this.dotsLayout = dotsLayout;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Object... objects) {
        mContext = (Context) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e("abc", "s = " + s);

        HashMap<String, Object> placeDetailList = null;
        PhotoParser parser = new PhotoParser();
        placeDetailList = parser.parse(s);
        showPlacePhotos(placeDetailList);
    }

    private void showPlacePhotos(HashMap<String, Object> placeDetailList) {
        layouts2.clear();
        img_arr.clear();


        ArrayList<MapData> photosList = (ArrayList<MapData>) placeDetailList.get("photos");

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
//        Log.e("abc", "rating)" + placeDetailList.get("rating").toString());
        ((OneRestaurantActivity) mContext).txt_rest_phone.setText("전화하기 " + placeDetailList.get("formatted_phone_number").toString());
//        ((OneRestaurantActivity) mContext).txt_rest_address.setText("주소 " + placeDetailList.get("fullAddress").toString());
        ((OneRestaurantActivity) mContext).txt_rest_address.setText(Html.fromHtml(placeDetailList.get("adr_address").toString()));
        ((OneRestaurantActivity) mContext).txt_rating.setText("평점 " + placeDetailList.get("rating").toString());
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