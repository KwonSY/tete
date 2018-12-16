package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.SettingActivity;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChangePswTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;

    private String pre_psw, new_psw;
    private String result;

    public ChangePswTask(Context mContext, OkHttpClient httpClient,
                         String pre_psw, String new_psw) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.pre_psw = pre_psw;
        this.new_psw = new_psw;
    }

    @Override
    protected void onPreExecute() {
        Log.e("abc", "onPreExecute ");
    }

    @Override
    protected Void doInBackground(Void... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "change_psw")
                .add("my_id", Statics.my_id)
                .add("pre_psw", pre_psw)
                .add("new_psw", new_psw)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "Change Pwd : " + obj);

                result = obj.getString("result");
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

        if (result.equals("0")) {
            Intent intent = new Intent(mContext, SettingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        } else {
            Toast.makeText(mContext, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
        }
    }

}