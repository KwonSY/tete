package honbab.pumkit.com.tete;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.pumkit.com.widget.Encryption;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    EditText edit_email, edit_password;

    String email, password;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());


        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);

        ImageView btn_show_password = (ImageView) findViewById(R.id.btn_show_password);
        btn_show_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction() ) {
                    case MotionEvent.ACTION_DOWN:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        break;
                    case MotionEvent.ACTION_UP:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        break;
                }
                return true;
            }
        });

        Button btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(mOnClickListener);

        TextView btn_go_join = (TextView) findViewById(R.id.btn_go_join);
        btn_go_join.setOnClickListener(mOnClickListener);

        progressDialog = new ProgressDialog(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_login:
                    new LoginTask().execute();

                    break;
                case R.id.btn_go_join:
                    Intent intent2 = new Intent(LoginActivity.this, JoinActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
            }
        }
    };

    private class LoginTask extends AsyncTask<Void, Void, Void> {
        String result;
        String user_name, age;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            email = edit_email.getText().toString();
            password = edit_password.getText().toString();

            Encryption.setPassword(password);
            Encryption.encryption(password);
            password = Encryption.getPassword();
            Log.e("abc", "email = " + email);
            Log.e("abc", "password = " + password);
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "login")
                    .add("email", email)
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
                        email = obj_user.getString("email");
                        Statics.my_gender = obj_user.getString("gender");
                        age = obj_user.getString("age");
//                        Log.e("abc", "Login myGender = " + my_gender);
                    } else if (result.equals("1")) {
                        Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
////                        progressBar.dismiss();
//
                        Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
////                isNetworkStat(LoginActivity.this);
////                buildDialog(LoginActivity.this).show();
//                Log.e("abc", "isConnected(LoginActivity.this) = " + MainActivity.isConnected(LoginActivity.this));
//                if (MainActivity.isConnected(LoginActivity.this) == false) {
//                    MainActivity.buildDialog(LoginActivity.this).show();
////                    buildDialog(LoginActivity.this).show();
//                } else {
//
//                }

                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {

                progressDialog.setMessage("로그인 중...");
                progressDialog.show();

                session.createLoginSession(Statics.my_id, Statics.my_gender);

                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

//                firebaseAuth.signInWithEmailAndPassword(email, password)
//                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                progressDialog.dismiss();
//                                Log.e("abc", "progressDialog myGender = " + myGender);
//                                session.createLoginSession(my_id, myGender);
//
//                                if(task.isSuccessful()){
//                                    finish();
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////                                    intent.putExtra("type", type); //vvvvvvvvvvvvvvvvvvvvvvvvvvvv
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
//
////                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                }
//
//                            }
//                        });

            } else if (result.equals("1")) {
                //Toast.makeText(getBaseContext(), "비밀번호를 맞지않습니다.", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "아이디 또는 비밀번호가 맞지 않습니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
