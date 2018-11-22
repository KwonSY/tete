package honbab.pumkit.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.utils.StringFilter;
import honbab.pumkit.com.widget.Encryption;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    private EditText edit_email, edit_name, edit_password;

//    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());

        TextView link_privacy = (TextView) findViewById(R.id.link_privacy);
        TextView link_personal = (TextView) findViewById(R.id.link_personal);
        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };
        Pattern pattern1 = Pattern.compile("");
        Pattern pattern2 = Pattern.compile("");
        Linkify.addLinks(link_privacy, pattern1, Statics.main_url + "law/privacy/", null, mTransform);
        Linkify.addLinks(link_personal, pattern2, Statics.main_url + "law/personal/", null, mTransform);
//        Linkify.addLinks(link_privacy, Linkify.WEB_URLS);
//        link_privacy.setMovementMethod(LinkMovementMethod.getInstance());

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_password = (EditText) findViewById(R.id.edit_password);
        StringFilter stringFilter = new StringFilter(this);
        InputFilter[] allowAlphanumeric = new InputFilter[1];
        allowAlphanumeric[0] = stringFilter.allowAlphanumeric;
        edit_password.setFilters(allowAlphanumeric);

        ImageView btn_show_password = (ImageView) findViewById(R.id.btn_show_password);
        btn_show_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT);
                        edit_password.setSelection(edit_password.getText().length());
                        break;
                    case MotionEvent.ACTION_UP:
                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edit_password.setSelection(edit_password.getText().length());
                        break;
                }
                return true;
            }
        });

        Button btn_join = (Button) findViewById(R.id.btn_join);
        btn_join.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_back:
                    onBackPressed();

                    break;
                case R.id.btn_join:
                    String user_name = edit_name.getText().toString().trim();
                    String str_email = edit_email.getText().toString().trim();
                    String password = edit_password.getText().toString().trim();
                    Log.e("abc", "user_name = " + user_name);
                    if (user_name.equals("") || user_name == null) {
                        Toast.makeText(JoinActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (str_email.equals("") || str_email == null) {
                        Toast.makeText(JoinActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (password.equals("") || password == null) {
                        Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 8 || password.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
                        Toast.makeText(JoinActivity.this, "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.", Toast.LENGTH_SHORT).show();
                    } else if (isValidEmail(str_email)) {
                        new JoinTask().execute();
                    } else {
                        Toast.makeText(JoinActivity.this.getApplicationContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    private class JoinTask extends AsyncTask<Void, Void, Void> {

        String user_name, email, password;
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user_name = edit_name.getText().toString();
            email = edit_email.getText().toString();
            password = edit_password.getText().toString();

            Encryption.setPassword(password);
            Encryption.encryption(password);
            password = Encryption.getPassword();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "join")
                    .add("user_name", user_name.trim())
                    .add("email", email.trim())
                    .add("password", password)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "Join root = " + obj);

                    result = obj.getString("result");

                    if (result.equals("0")) {
                        JSONObject obj_user = obj.getJSONObject("user");
                        Statics.my_id = obj_user.getString("sid");
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

        @Override
        protected void onPostExecute(Void aVoid) {
            if (result.equals("0")) {

//                progressDialog.setMessage("가입 중...");
//                progressDialog.show();

                session.createLoginSession(Statics.my_id, Statics.my_gender);

                finish();
                Intent intent = new Intent(getApplicationContext(), JoinActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (result.equals("1")) {
                Toast.makeText(JoinActivity.this.getApplicationContext(), "잘못된 오류입니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("2")) {
                Toast.makeText(JoinActivity.this.getApplicationContext(), "이미 가입한 이메일입니다.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}