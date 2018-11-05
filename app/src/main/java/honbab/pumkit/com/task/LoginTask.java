package honbab.pumkit.com.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.pumkit.com.tete.MainActivity;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.Encryption;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginTask extends AsyncTask<String, Void, String> {

    private OkHttpClient httpClient;
    private Context mContext;
    private SessionManager session;

    EditText edit_email, edit_password;
    private ProgressDialog progressDialog;

    String result;
//    String email, password;
    String user_name, age;

    public LoginTask(Context mContext, OkHttpClient httpClient) {
        this.mContext = mContext;
        this.httpClient = httpClient;
    }

    @Override
    protected void onPreExecute() {
//        email = edit_email.getText().toString();
//        password = edit_password.getText().toString();
//
//        Encryption.setPassword(password);
//        Encryption.encryption(password);
//        password = Encryption.getPassword();

        session = new SessionManager(mContext.getApplicationContext());

        progressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected String doInBackground(String... objects) {
        String password = objects[1];

        Encryption.setPassword(password);
        Encryption.encryption(password);
        password = Encryption.getPassword();

        FormBody body = new FormBody.Builder()
                .add("opt", "login")
                .add("email", objects[0])
                .add("password", password)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "Login root = " + obj);

                result = obj.getString("result");




                if (result.equals("0")) {
                    JSONObject obj_user = obj.getJSONObject("user");
                    Statics.my_id = obj_user.getString("sid");
                    user_name = obj_user.getString("user_name");
                    String email = obj_user.getString("email");
                    Statics.my_gender = obj_user.getString("gender");
                    age = obj_user.getString("age");
                } else if (result.equals("1")) {
                    Toast.makeText(mContext.getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                } else {
////                        progressBar.dismiss();
//
                    Toast.makeText(mContext.getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
            if (result.equals("0")) {
                progressDialog.setMessage("로그인 중...");
                progressDialog.show();
                //vvvvvvvvv
                //1초정도 로그인 화면 보여주기

                session.createLoginSession(Statics.my_id, Statics.my_gender);

                //vvvvvvvvvvvv
//                finish();
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }

    }

}