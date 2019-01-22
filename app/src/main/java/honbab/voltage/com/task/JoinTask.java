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

import honbab.voltage.com.tete.JoinActivity2;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinTask extends AsyncTask<String, Void, Void> {
    private Context mContext;
    private OkHttpClient httpClient;
    private SessionManager session;

    private EditText edit_email, edit_password;
    private ProgressDialog progressDialog;

    String result;
    String user_name, email, password;

    public JoinTask(Context mContext) {
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
    protected Void doInBackground(String... params) {
        Encryption.setPassword(params[2]);
        Encryption.encryption(params[2]);
        password = Encryption.getPassword();

        FormBody body = new FormBody.Builder()
                .add("opt", "join")
                .add("user_name", params[0].trim())
                .add("email", params[1].trim())
                .add("password", password)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");

                if (result.equals("0")) {
                    JSONObject obj_user = obj.getJSONObject("user");
                    Statics.my_id = obj_user.getString("sid");
                    Statics.my_username = obj_user.getString("name");
                    email = obj.getString("email");
                    Statics.my_gender = obj.getString("gender");
                }
            }
        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

//    @Override
    protected void onPostExecute(Void aVoid) {
//        super.onPostExecute(result);

        if (result.equals("0")) {
            session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

            Toast.makeText(mContext, "환영합니다. 우리 이제 같이먹어요!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, JoinActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        } else if (result.equals("1")) {
            Toast.makeText(mContext.getApplicationContext(), "잘못된 오류입니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
        } else if (result.equals("2")) {
            Toast.makeText(mContext.getApplicationContext(), "이미 가입한 이메일입니다.", Toast.LENGTH_SHORT).show();
        }

    }

}