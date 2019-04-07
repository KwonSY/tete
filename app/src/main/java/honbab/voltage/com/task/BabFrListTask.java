package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.BabFriendsAdapter;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.BabFriendsActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BabFrListTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private Fragment fragment;
    private String activityName;

    ArrayList<UserData> userList = new ArrayList<>();
//    HashMap<String, Integer> chatListHash = new HashMap<>();

    private String user_id, user_name, user_img, comment, token, fr_status;

    public BabFrListTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
//        if (mContext != null) {
//            activityName = mContext.getClass().getSimpleName();
////        fragment = (Fragment) ((MainActivity) mContext).pagerAdapter.instantiateItem(((MainActivity) mContext).viewPager, 1);
//            if (activityName.equals("MainActivity")) {
//                fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
//            }
//        }
    }

    @Override
    protected Void doInBackground(String... objects) {
        UserData userData = new UserData();

        FormBody body = new FormBody.Builder()
                .add("opt", "bab_fr_list")
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "BabFrListTask = " + obj);

                JSONArray user_arr = obj.getJSONArray("users");
                for (int i=0; i<user_arr.length(); i++) {
                    JSONObject user_obj = user_arr.getJSONObject(i);
                    user_id = user_obj.getString("sid");
                    user_name = user_obj.getString("name");
                    String age = user_obj.getString("age");
                    String gender = user_obj.getString("gender");
                    token = user_obj.getString("token");
                    user_img = Statics.main_url + user_obj.getString("img_url");

                    userData = new UserData(user_id, user_name, age, gender, token, user_img, null);
                    userList.add(userData);
                }

            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(userData);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName == null) {

        } else if (activityName.equals("BabFriendsActivity")) {
            ((BabFriendsActivity) mContext).mAdapter = new BabFriendsAdapter(mContext, userList);
            ((BabFriendsActivity) mContext).recyclerView_fr.setAdapter(((BabFriendsActivity) mContext).mAdapter);
        } else {

        }
    }

}