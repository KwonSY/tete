package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CityListTask extends AsyncTask<String, Void, ArrayList<AreaData>> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private Fragment fragment;

    private ArrayList<AreaData> cityList = new ArrayList<>();
//    private ArrayList<String> areaNameList = new ArrayList<>();
    String result;

    public CityListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        cityList.clear();

//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @Override
    protected ArrayList<AreaData> doInBackground(String... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "city_list")
                .add("auth", "happy")
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "public.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray cityArr = obj.getJSONArray("city");
                for (int i = 0; i < cityArr.length(); i++) {
                    JSONObject cityObj = cityArr.getJSONObject(i);
                    String cityCd = cityObj.getString("name");
                    String cityName = cityObj.getString("name_k");
                    String cityImg = cityObj.getString("img");

                    AreaData cityData = new AreaData(cityCd, cityName, 0);
                    cityData.setArea_image(cityImg);
                    cityList.add(cityData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return cityList;
    }

    @Override
    protected void onPostExecute(final ArrayList<AreaData> cityList) {
        super.onPostExecute(cityList);

        String activityName = mContext.getClass().getSimpleName();

//        if (activityName.equals("MainActivity")) {
//            if (areaList.size() > 0) {
//
//            } else {
//
//            }
//        }
    }

}