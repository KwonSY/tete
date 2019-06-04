package honbab.voltage.com.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginByUidTask extends AsyncTask<String, Void, String> {
    private Context mContext;
    private OkHttpClient httpClient;
    private SessionManager session;

//    private EditText edit_email, edit_password;
    private ProgressDialog progressDialog;

    String result;
    String age;

    public LoginByUidTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
    }

    @Override
    protected void onPreExecute() {
        session = new SessionManager(mContext.getApplicationContext());

        progressDialog = new ProgressDialog(mContext);
    }

    @Override
    protected String doInBackground(String... objects) {
//        String password = objects[1];
//
//        Encryption.setPassword(password);
//        Encryption.encryption(password);
//        password = Encryption.getPassword();
        Log.e("abc", "LoginTask email = " + objects[0]);
        Log.e("abc", "LoginTask uid = " + objects[1]);

        FormBody body = new FormBody.Builder()
                .add("opt", "login_by_uid")
                .add("email", objects[0])
                .add("uid", objects[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "LoginTask = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");

                if (result.equals("0")) {
                    JSONObject obj_user = obj.getJSONObject("user");
                    Statics.my_id = obj_user.getString("sid");
                    Statics.my_username = obj_user.getString("name");
                    String email = obj_user.getString("email");
                    Statics.my_gender = obj_user.getString("gender");
                    age = obj_user.getString("age");
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

                session.createLoginSession(Statics.my_id, Statics.my_username ,Statics.my_gender);

                Intent intent = new Intent(mContext, MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
            } else if (result.equals("1")) {
                Toast.makeText(mContext, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
//                ((MainActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(mContext, "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
//                    }
//                });
            } else {
                Toast.makeText(mContext, "존재하지 않는 이메일입니다. 회원가입을 진행해주세요.", Toast.LENGTH_SHORT).show();
//                ((MainActivity) mContext).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                });
            }

    }

}