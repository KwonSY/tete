package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AreaRestTask extends AsyncTask<String, Void, ArrayList<AreaData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;

    private ArrayList<AreaData> areaList = new ArrayList<>();
    private ArrayList<String> areaNameList = new ArrayList<>();
    String result;

    public AreaRestTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        areaList.clear();
        areaNameList.clear();

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @Override
    protected ArrayList<AreaData> doInBackground(String... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "area_cd_list")//vvvv->area_cd
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONArray rest_arr = obj.getJSONArray("area");
                for (int i = 0; i < rest_arr.length(); i++) {
                    JSONObject rest_obj = rest_arr.getJSONObject(i);
                    String area_cd = rest_obj.getString("area_cd");
                    String area_name = rest_obj.getString("name");

                    AreaData restData = new AreaData(area_cd, area_name);
                    areaList.add(restData);
                    areaNameList.add(area_name);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return areaList;
    }

    @Override
    protected void onPostExecute(final ArrayList<AreaData> areaList) {
        super.onPostExecute(areaList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("MainActivity")) {
            if (areaList.size() > 0) {
                ((SelectFeedFragment) fragment).areaAllList = areaList;
//                ((SelectFeedFragment) fragment).areaNameList = areaNameList;

//                ((SelectFeedFragment) fragment).spinnerAdapter = new ArrayAdapter(mContext, R.layout.item_row_spinner, areaNameList);
//                ((SelectFeedFragment) fragment).spinner.setAdapter(((SelectFeedFragment) fragment).spinnerAdapter);
//                if (((SelectFeedFragment) fragment).area_cd.equals("SUGNS1")) {
//
//                } else {

//                for (AreaData areaData : areaList) {
//                    if (areaData.getArea_cd().equals(((SelectFeedFragment) fragment).area_cd)) {
//                        int seq = areaNameList.indexOf(areaData.getArea_name());
//                    }
//                }


//                int seq = areaNameList.indexOf(new AreaData(((SelectFeedFragment) fragment).area_cd));
//                    ((SelectFeedFragment) fragment).spinner.setSelection(seq);
//                }
            } else {

            }
        }
    }

}