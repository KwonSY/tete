package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AddFrTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

//    private Fragment fragment;
//    private String fr_status = "";

    private String result;

    public AddFrTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
    }

    @Override
    protected Void doInBackground(String... params) {
//        String fr_status = params[0];

        FormBody body = new FormBody.Builder()
                .add("opt", "req_friend")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("from_id", Statics.my_id)
                .add("to_id", params[0])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "babfr.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "AddFrTask bodyStr = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");
//                status = obj.getString("status");
            } else {
                Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(result);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("ProfileActivity")) {
//            if (result.equals("0")) {
//                ((ProfileActivity) mContext).btn_add_fr.setEnabled(false);
//            } else if (result.equals("1")) {
////                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
//            } else {
////                Toast.makeText(mContext, R.string.cancle, Toast.LENGTH_SHORT).show();
//            }
        }
    }

}