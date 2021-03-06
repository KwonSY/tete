package honbab.voltage.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LeaveTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;
    private SessionManager session;

    private Fragment fragment;
//    private String activityName;

    public LeaveTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.session = new SessionManager(mContext);
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "leave_godmuk")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "login.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                String result = obj.getString("result");
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
//        super.onPostExecute(aVoid);

        String activityName = mContext.getClass().getSimpleName();

        if (activityName.equals("ReportActivity")) {
            Intent intent = new Intent(mContext, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        } else {
            session.logoutUser();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(mContext, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }

    }

}