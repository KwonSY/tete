package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class SettingActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    RadioButton radio_y, radio_n;

    private String alarm_yn = "y";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(this.getApplicationContext());

        radio_y = (RadioButton) findViewById(R.id.radio_y);
        radio_n = (RadioButton) findViewById(R.id.radio_n);
        radio_y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_yn = "y";
                new UpdateAlarmYnTask().execute(alarm_yn);
            }
        });
        radio_n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarm_yn = "n";
                new UpdateAlarmYnTask().execute(alarm_yn);
            }
        });

        Button btn_change_psw = (Button) findViewById(R.id.btn_change_psw);
        btn_change_psw.setOnClickListener(mOnClickListener);

        Button btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(mOnClickListener);

//        ImageButton btn_back = (ImageButton) findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(mOnClickListener);
//        btn_back.setOnTouchListener(mOnTouchListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new MySettingTask().execute();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_change_psw:
                    Intent intent = new Intent(SettingActivity.this, ChangePswActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_logout:
                    session.logoutUser();
                    FirebaseAuth.getInstance().signOut();

                    Intent intent2 = new Intent(SettingActivity.this, MainActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
            }
        }
    };

    public class MySettingTask extends AsyncTask<Void, Void, Void> {
        String alarm;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "my_setting")
                    .add("my_id", Statics.my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "예약 : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    alarm_yn = obj.getString("alarm");
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
            if (alarm_yn.equals("y")) {
                radio_y.setChecked(true);
            } else if (alarm_yn.equals("n")) {
                radio_n.setChecked(true);
            }
        }
    }

    public class UpdateAlarmYnTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            Log.e("abc", "yn = " + params[0]);
            FormBody body = new FormBody.Builder()
                    .add("opt", "alarm_on_off")
                    .add("my_id", Statics.my_id)
                    .add("yn", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    Log.e("abc", "예약 : " + bodyStr);

                    JSONObject obj = new JSONObject(bodyStr);

                    alarm_yn = obj.getString("alarm");
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
//            if (alarm_yn.equals("y")) {
//                radio_y.setChecked(true);
//            } else if (alarm_yn.equals("n")) {
//                radio_n.setChecked(true);
//            }
        }
    }
}