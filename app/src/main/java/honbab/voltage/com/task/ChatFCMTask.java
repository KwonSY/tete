package honbab.voltage.com.task;

import android.os.AsyncTask;
import android.util.Log;

import honbab.voltage.com.data.FcmData;
import honbab.voltage.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ChatFCMTask extends AsyncTask<FcmData, Void, Void> {
//    private Context mContext;
    private OkHttpClient httpClient;

    public ChatFCMTask(OkHttpClient httpClient) {
//        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected Void doInBackground(FcmData... params) {
        if (params[0].token == null || params[0].token.equals("null")) {

        } else {
            FormBody body = new FormBody.Builder()
                    .add("token", params[0].token)
                    .add("user_name", params[0].user_name)
                    .add("message", params[0].message)
                    .build();

            Request request = new Request.Builder().url(Statics.main_url + "fcm/push_chat.php").post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("FCM", "FCM_obj = " + bodyStr);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return null;
    }

}