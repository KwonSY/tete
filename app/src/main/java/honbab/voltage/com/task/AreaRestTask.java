package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.DelayBefroePickRestActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AreaRestTask extends AsyncTask<String, Void, ArrayList<RestData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private ArrayList<RestData> areaList = new ArrayList<>();
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
    }

    @Override
    protected ArrayList<RestData> doInBackground(String... params) {
        FormBody body = new FormBody.Builder()
                .add("opt", "rest_area")
//                .add("pack", params[0])
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
                    String pack = rest_obj.getString("pack");
                    String area_name = rest_obj.getString("name");

                    RestData restData = new RestData(pack, area_name, null, null, null, null, null, null);
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
    protected void onPostExecute(final ArrayList<RestData> areaList) {
        super.onPostExecute(areaList);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("DelayBefroePickRestActivity")) {
            if (areaList.size() > 0) {
                ((DelayBefroePickRestActivity) mContext).spinnerAdapter = new ArrayAdapter(mContext, R.layout.support_simple_spinner_dropdown_item, areaNameList);
                ((DelayBefroePickRestActivity) mContext).spinner.setAdapter(((DelayBefroePickRestActivity) mContext).spinnerAdapter);

//                cnt_reserved = doOneDayFeedCheckTask();
            } else {

            }
        }
    }

}