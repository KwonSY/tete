package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.DialogRestListAdapter;
import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.data.CityData;
import honbab.voltage.com.data.OneTimeRestLikeListData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.PickRestLikeActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class OneFeedRestLikeListTask extends AsyncTask<String, Void, OneTimeRestLikeListData> {
    private Context mContext;
    private OkHttpClient httpClient;
//    private String activityName;

//    private Fragment fragment, fragment2;

    private ArrayList<OneTimeRestLikeListData> oneTimeRestLikeList = new ArrayList<>();
    private ArrayList<CityData> cityList = new ArrayList<>();
    private ArrayList<RestData> restList = new ArrayList<>();

    public OneFeedRestLikeListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

//        activityName = mContext.getClass().getSimpleName();
//        if (activityName.equals("PickRestLikeActivity")) {
//            ((PickRestLikeActivity) mContext).mAdapter_rest.clearItemList();
//        }

    }

    @Override
    protected void onPreExecute() {
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
//        fragment2 = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
//
        cityList.clear();
//        ((RestLikeFragment) fragment).mAdapter.clearItemList();
    }

    @Override
    protected OneTimeRestLikeListData doInBackground(String... params) {
        Log.e("abc", "OneFeedRestTask = " + params[0] + ", " + params[1]);
        OneTimeRestLikeListData data = new OneTimeRestLikeListData();

        FormBody body = new FormBody.Builder()
                .add("opt", "one_time_rest_like_list")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("timelike_id", params[0])
                .add("area_cd", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "tab1.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "OneFeedRestLikeListTask = " + obj);

                String result = obj.getString("result");

                JSONArray cityArr = obj.getJSONArray("cities");
                for (int i = 0; i < cityArr.length(); i++) {
                    ArrayList<AreaData> areaList = new ArrayList<>();

                    JSONObject cityObj = cityArr.getJSONObject(i);
                    String city = cityObj.getString("city");
                    String city_name = cityObj.getString("name");

                    JSONArray areaArr = cityObj.getJSONArray("areas");
                    for (int j = 0; j < areaArr.length(); j++) {
                        JSONObject areaObj = areaArr.getJSONObject(j);
                        String area_cd = areaObj.getString("area_cd");
                        String area_name = areaObj.getString("area_name");
                        int cnt = areaObj.getInt("cnt");

                        AreaData areaData = new AreaData(area_cd, area_name, cnt);
                        areaList.add(areaData);
                    }

                    CityData cityData = new CityData(city, city_name, areaList);
                    cityList.add(cityData);
                }

                JSONArray restArr = obj.getJSONArray("rests");
                for (int k=0; k<restArr.length(); k++) {
                    JSONObject restObj = restArr.getJSONObject(k);

                    String rest_id = restObj.getString("sid");
                    String rest_type = restObj.getString("type");
                    String rest_name = restObj.getString("name");
                    String compound_code = restObj.getString("compound_code");
                    Double db_lat = Double.parseDouble(restObj.getString("lat"));
                    Double db_lng = Double.parseDouble(restObj.getString("lng"));
                    LatLng latLng = new LatLng(db_lat, db_lng);
                    String place_id = restObj.getString("place_id");
                    String rest_phone = restObj.getString("phone");
                    String rest_img = restObj.getString("img_url");
                    String vicinity = restObj.getString("vicinity");
                    String like_yn = restObj.getString("like_yn");
                    int sale = restObj.getInt("sale");
                    int cnt = restObj.getInt("cnt");
                    Log.e("abc", "OneFeedRestLikeListTask cnt = " + cnt);

                    RestData restData = new RestData(rest_id, rest_name,
                            compound_code, latLng, place_id, rest_img, rest_phone, vicinity, sale, cnt);
                    restData.setLike_yn(like_yn);
//                    restData.setType(rest_type);
                    restList.add(restData);
                }

                data = new OneTimeRestLikeListData(cityList, restList);

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onPostExecute(final OneTimeRestLikeListData oneTimeRestLikeListData) {
        super.onPostExecute(oneTimeRestLikeListData);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {

        } else if (activityName.equals("PickRestLikeActivity")) {
            ((PickRestLikeActivity) mContext).progressBar_rest.setVisibility(View.GONE);

            ((PickRestLikeActivity) mContext).mAdapter_rest.clearItemList();
            ((PickRestLikeActivity) mContext).mAdapter_rest = new DialogRestListAdapter(mContext, restList);
            ((PickRestLikeActivity) mContext).recyclerView_rest.setAdapter(((PickRestLikeActivity) mContext).mAdapter_rest);
            ((PickRestLikeActivity) mContext).mAdapter_rest.notifyDataSetChanged();
        }
    }

}