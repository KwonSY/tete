package honbab.voltage.com.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinCheckTask extends AsyncTask<String, Void, JSONObject> {
    private Context mContext;
    private OkHttpClient httpClient;
    private SessionManager session;

    private EditText edit_email, edit_password;
    private ProgressDialog progressDialog;

    String result;
    String user_name, email, password;

    public JoinCheckTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        session = new SessionManager(mContext);
//        user_name = edit_name.getText().toString();
//        email = edit_email.getText().toString();
//        password = edit_password.getText().toString().trim();
//
//        Encryption.setPassword(password);
//        Encryption.encryption(password);
//        password = Encryption.getPassword();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject obj = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "join_check")
                .add("field", "email")
                .add("param", params[0])
//                .add("email", params[0].trim())
//                .add("facebook", params[0])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                obj = new JSONObject(bodyStr);

                result = obj.getString("result");

                if (result.equals("0") || result.equals("2")) {
                    JSONObject obj_user = obj.getJSONObject("user");
                    Statics.my_id = obj_user.getString("sid");
                    Statics.my_username = obj_user.getString("name");
//                    email = obj_user.getString("email");
                    Statics.my_gender = obj_user.getString("gender");
                }
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return obj;
    }

    @Override
    protected void onPostExecute(JSONObject userObj) {
        super.onPostExecute(userObj);

        if (result.equals("0")) {

        } else if (result.equals("1")) {
            Toast.makeText(mContext.getApplicationContext(), "잘못된 오류입니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
        } else if (result.equals("2")) {
            Log.e("abc", "JoinCheck Statics.my_id = " + Statics.my_id);
            session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

            Toast.makeText(mContext.getApplicationContext(), "같이먹어요에 오신 것을 환영합니다.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }

    }

}