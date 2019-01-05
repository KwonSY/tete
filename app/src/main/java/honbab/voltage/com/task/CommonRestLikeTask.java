package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CommonRestLikeTask extends AsyncTask<String, Void, ArrayList<RestData>> {
    private Context mContext;
    private OkHttpClient httpClient;

    private ArrayList<RestData> restList = new ArrayList<>();
    private ArrayList<String> restNameList = new ArrayList<>();
//    private String result;
    private int defalutPos = 0;
    private String pick_rest_id;

    public CommonRestLikeTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
        restNameList.clear();
    }

    @Override
    protected ArrayList<RestData> doInBackground(String... params) {
        pick_rest_id = params[2];
        Log.e("abc", "pick_rest_id = " + pick_rest_id);

        FormBody body = new FormBody.Builder()
                .add("opt", "common_rest_like_list")
                .add("my_id", params[0])
                .add("user_id", params[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                int cnt_common_rest = obj.getInt("cnt");
                JSONArray rest_arr = obj.getJSONArray("rest");
                for (int i = 0; i < rest_arr.length(); i++) {
                    //음식점 정보
                    JSONObject rest_obj = rest_arr.getJSONObject(i);
                    String rest_id = rest_obj.getString("sid");
                    String type = rest_obj.getString("type");
                    String rest_name = rest_obj.getString("name");
                    String compound_code = rest_obj.getString("compound_code");
                    Double db_lat = Double.parseDouble(rest_obj.getString("lat"));
                    Double db_lng = Double.parseDouble(rest_obj.getString("lng"));
                    LatLng latLng = new LatLng(db_lat, db_lng);
                    String place_id = rest_obj.getString("place_id");
                    String rest_img = rest_obj.getString("img_url");
                    String rest_phone = rest_obj.getString("phone");
                    String vicinity = rest_obj.getString("vicinity");

                    if (pick_rest_id == null) {
                        defalutPos = 0;
                    } else if (pick_rest_id.equals(rest_id)) {
                        defalutPos = restList.size();
                    }
                    RestData restData = new RestData(rest_id, rest_name,
                            compound_code, latLng, place_id, rest_img, rest_phone, vicinity);
                    restList.add(restData);
                    restNameList.add(rest_name);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return restList;
    }

    @Override
    protected void onPostExecute(ArrayList<RestData> aVoid) {
        super.onPostExecute(aVoid);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("ChatActivity")) {
//            Log.e("abc", "restNameList.size() = " + restNameList.size());
            Log.e("abc", "defalutPos = " + defalutPos);

            SpinnerAdapter spinnerAdapter = new ArrayAdapter(mContext, R.layout.support_simple_spinner_dropdown_item, restNameList);
            ((ChatActivity) mContext).spinner_restName.setAdapter(spinnerAdapter);

            if (pick_rest_id == null || pick_rest_id.equals(null)) {

            } else {
                ((ChatActivity) mContext).spinner_restName.setSelection(defalutPos);
            }


//            if (restNameList.size() > 0) {
//
//            } else {
//                ((ChatActivity) mContext).spinner_restName.setVisibility(View.INVISIBLE);
//            }

//            ((ChatActivity) mContext).spinner_restName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    int r = 0;
//
//                    for (int i=0; i<restList.size(); i++) {
//                        if (restList.get(i).getRest_name().equals(((ChatActivity) mContext).spinner_restName.getItemIdAtPosition(position)))
//                            r = i;
//                    }
//                    RestData pickData = restList.get(r);
//                    ((ChatActivity) mContext).restData = new RestData(pickData.getRest_id(), pickData.getRest_name(),
//                            pickData.getCompound_code(), pickData.getLatLng(), pickData.getPlace_id(), pickData.getRest_img(), pickData.getRest_phone(), pickData.getVicinity());
//
//                    Picasso.get().load(pickData.getRest_img())
//                            .placeholder(R.drawable.icon_no_image)
//                            .error(R.drawable.icon_no_image)
//                            .into(((ChatActivity) mContext).img_rest);
//                }
//            });
        }
    }

}