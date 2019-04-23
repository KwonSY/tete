package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SearchFrTask extends AsyncTask<String, Void, ArrayList<UserData>> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private Fragment fragment;
//    private String fr_status = "";

    private String result;
    private ArrayList<UserData> userList = new ArrayList<>();
    private UserData userData = new UserData();

    public SearchFrTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
    }

    @Override
    protected ArrayList<UserData> doInBackground(String... params) {
//        String fr_status = params[0];

        FormBody body = new FormBody.Builder()
                .add("opt", "search_friend")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("user_name", params[0])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "babfr.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "Search bodyStr = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                JSONArray userArr = obj.getJSONArray("users");
                for (int i=0; i<userArr.length(); i++) {
                    JSONObject userObj = userArr.getJSONObject(i);
                    String user_id = userObj.getString("sid");
                    String user_name = userObj.getString("name");
                    String user_img = userObj.getString("img_url");
                    String user_age = userObj.getString("age");
                    String user_gender = userObj.getString("gender");

                    userData = new UserData(user_id, user_name, user_age, user_gender, "", user_img, "", "");
                    userList.add(userData);
                }
            } else {
                Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return userList;
    }

    @Override
    protected void onPostExecute(ArrayList<UserData> userList) {
//        super.onPostExecute(result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("BabFriendsActivity")) {
//            ((BabFriendsActivity) mContext).layout_row_babfr.setVisibility(View.VISIBLE);
//            ((BabFriendsActivity) mContext).layout_row_babfr.setBackgroundColor(ContextCompat.getColor(mContext, R.color.whitegrey));
//            Picasso.get().load(userData.getImg_url())
//                    .transform(new CircleTransform())
//                    .into(((BabFriendsActivity) mContext).img_user);
//            ((BabFriendsActivity) mContext).txt_userName.setText(userData.getUser_name());
        }
    }

}