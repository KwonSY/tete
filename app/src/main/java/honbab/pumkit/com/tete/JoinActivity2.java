package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONObject;

import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinActivity2 extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    String gender, age, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());


        RadioButton radio_male = (RadioButton) findViewById(R.id.radio_male);
        RadioButton radio_female = (RadioButton) findViewById(R.id.radio_female);
        radio_male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "m";
            }
        });
        radio_female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = "f";
            }
        });
        radio_male.setChecked(true);

        Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new JoinTask().execute();
                finish();
                Intent intent = new Intent(JoinActivity2.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private class UpdateProfileTask extends AsyncTask<Void, Void, Void> {

        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            user_name = edit_name.getText().toString();
//            email = edit_email.getText().toString();
//            password = edit_password.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_profile")
                    .add("gender", gender)
                    .add("age", age)
                    .add("comment", comment)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Join2 root = " + obj);

                    result = obj.getString("result");

//                    if (result.equals("0")) {
//                        JSONObject obj_user = obj.getJSONObject("user");
//                        Statics.my_id = obj_user.getString("sid");
//                        email = obj.getString("email");
//                        Statics.my_gender = obj.getString("gender");
//                    }
                }
            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {

//                progressDialog.setMessage("가입 중...");
//                progressDialog.show();

                session.createLoginSession(Statics.my_id, Statics.my_gender);

                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (result.equals("1")) {
                Toast.makeText(JoinActivity2.this.getApplicationContext(), "네트워크 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}