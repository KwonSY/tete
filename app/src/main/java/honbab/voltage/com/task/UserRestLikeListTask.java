package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UserRestLikeListTask extends AsyncTask<String, Void, ArrayList<RestData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment, fragment2;

    private ArrayList<RestData> restLikeList = new ArrayList<>();
    String result;

    public UserRestLikeListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
//        fragment2 = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
//
        restLikeList.clear();
//        ((RestLikeFragment) fragment).mAdapter.clearItemList();
    }

    @Override
    protected ArrayList<RestData> doInBackground(String... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "user_rest_like_list")
                .add("area_cd", params[0])
                .add("user_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray restsArr = obj.getJSONArray("rests");
                for (int i = 0; i < restsArr.length(); i++) {
                    JSONObject rest_obj = restsArr.getJSONObject(i);
                    String rest_id = rest_obj.getString("sid");
                    String rest_name = rest_obj.getString("name");
                    String compound_code = rest_obj.getString("compound_code");
                    Double db_lat = Double.parseDouble(rest_obj.getString("lat"));
                    Double db_lng = Double.parseDouble(rest_obj.getString("lng"));
                    LatLng latLng = new LatLng(db_lat, db_lng);
                    String place_id = rest_obj.getString("place_id");
                    String rest_phone = rest_obj.getString("phone");
                    String rest_img = rest_obj.getString("img_url");
                    String vicinity = rest_obj.getString("vicinity");
                    String like_yn = rest_obj.getString("like_yn");
                    int sale = rest_obj.getInt("sale");

                    RestData restData = new RestData(rest_id, rest_name,
                            compound_code, latLng, place_id, rest_img, rest_phone, vicinity, sale, 0);
                    restData.setLike_yn(like_yn);
                    restLikeList.add(restData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return restLikeList;
    }

    @Override
    protected void onPostExecute(final ArrayList<RestData> restLikeList) {
        super.onPostExecute(restLikeList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {

        }
    }

}