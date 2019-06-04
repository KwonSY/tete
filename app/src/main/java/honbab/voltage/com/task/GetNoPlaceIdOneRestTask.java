package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;

import honbab.voltage.com.adapter.ViewPagerAdapter;
import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.network.OnTaskCompleted;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.utils.GoogleMapUtil;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GetNoPlaceIdOneRestTask extends AsyncTask<Object, String, RestData> {
//    private String googlePlacesData;
//    private String url;

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

    public GetNoPlaceIdOneRestTask(Context mContext, ViewPager viewPager, LinearLayout dotsLayout) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.viewPager = viewPager;
        this.dotsLayout = dotsLayout;
//        this.mCallback = (OnTaskCompleted) mContext;
    }

//    public GetNoPlaceIdOneRestTask(Context mContext, OnTaskCompleted onTaskCompleted,
//                                   String rest_id) {
//        this.mContext = mContext;
//        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
//        this.mCallback = onTaskCompleted;
//
//        this.rest_id = rest_id;
//    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected RestData doInBackground(Object... objects) {
        RestData restData = new RestData();
        rest_id = (String) objects[0];
        Log.e("abc", "Encryption.voltAuth() = " + Encryption.voltAuth());
        FormBody body = new FormBody.Builder()
                .add("opt", "one_rest_page")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("rest_id", rest_id)
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "rest.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(bodyStr);
                Log.e("abc", "element = " + element);
                JsonObject obj = element.getAsJsonObject();

                String rest_id = obj.get("sid").getAsString();
                String rest_type = obj.get("type").getAsString();
                String rest_name = obj.get("name").getAsString();
                String compound_code = obj.get("compound_code").getAsString();
                String place_id = obj.get("place_id").getAsString();
                double lat = obj.get("lat").getAsDouble();
                double lng = obj.get("lng").getAsDouble();
                LatLng latLng = new LatLng(lat, lng);
                String phone = obj.get("phone").getAsString();
                String img_url = obj.get("img_url").getAsString();

                JsonArray imgArr = obj.get("imgs").getAsJsonArray();
//                ArrayList<String> imgList = new ArrayList<>();
                for (int i=0; i<imgArr.size(); i++) {
                    String img_url2 = imgArr.get(i).getAsString();

                    if (i<4) {
                        img_arr.add(img_url2);
                        layouts2.add(R.layout.slide_food_image);
                    }

                    imgAllArr.add(img_url2);
                }
                String vicinity = obj.get("vicinity").getAsString();
                int sale = obj.get("sale").getAsInt();

                restData = new RestData(rest_id, rest_name,
                        compound_code, latLng, place_id, img_url, phone, vicinity, sale, 0);
                restData.setType(rest_type);
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return restData;
    }

    @Override
    protected void onPostExecute(RestData data) {
        super.onPostExecute(data);

        String activityName = mContext.getClass().getSimpleName();

//        HashMap<String, Object> placeDetailList = null;
//        PhotoParser parser = new PhotoParser();
//        placeDetailList = parser.parse(googlePlacesData);

        if (activityName.equals("OneRestaurantActivity")) {
//            showPlacePhotos(placeDetailList);

            mAdapter = new ViewPagerAdapter(mContext, layouts2, img_arr, imgAllArr);
            ((OneRestaurantActivity) mContext).viewPager.setAdapter(mAdapter);
            ((OneRestaurantActivity) mContext).viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

            ((OneRestaurantActivity) mContext).layout_star.setVisibility(View.GONE);
            ((OneRestaurantActivity) mContext).txt_rest_address.setText(data.getVicinity());
            ((OneRestaurantActivity) mContext).btn_call.setText("전화하기 " + data.getRest_phone());
        } else {

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
//        ((OneRestaurantActivity) mContext).txt_rest_address.setText(Html.fromHtml(placeDetailList.get("adr_address").toString()));
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