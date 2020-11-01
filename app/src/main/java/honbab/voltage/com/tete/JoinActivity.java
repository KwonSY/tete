package honbab.voltage.com.tete;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.StringFilter;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import io.fabric.sdk.android.Fabric;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;
    private FirebaseAuth firebaseAuth;

    private RelativeLayout layout_progress;
//    private ProgressBar progressBar;
    private EditText edit_email, edit_name, edit_password;
    private CheckBox chk_privacy, chk_personal;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_join);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        layout_progress = (RelativeLayout) findViewById(R.id.layout_progress);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });

        TextView link_privacy = (TextView) findViewById(R.id.link_privacy);
        TextView link_personal = (TextView) findViewById(R.id.link_personal);
        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher match, String url) {
                return "";
            }
        };
        Pattern pattern = Pattern.compile("");
        Linkify.addLinks(link_privacy, pattern, Statics.main_url + "law/privacy/", null, mTransform);
        Linkify.addLinks(link_personal, pattern, Statics.main_url + "law/personal/", null, mTransform);

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

        chk_privacy = (CheckBox) findViewById(R.id.chk_privacy);
        chk_personal = (CheckBox) findViewById(R.id.chk_personal);

        Button btn_join = (Button) findViewById(R.id.btn_join);
        btn_join.setOnClickListener(mOnClickListener);

        ButtonUtil.setBackButtonClickListener(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_join:
                    String user_name = edit_name.getText().toString().trim();
                    String str_email = edit_email.getText().toString().trim();
                    String password = edit_password.getText().toString().trim();

                    if (user_name.equals("") || user_name == null) {
                        Toast.makeText(JoinActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    } else if (str_email.equals("") || str_email == null) {
                        Toast.makeText(JoinActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    } else if (password.equals("") || password == null) {
                        Toast.makeText(JoinActivity.this, R.string.enter_password, Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 8 || password.matches("[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힝]*")) {
                        Toast.makeText(JoinActivity.this, R.string.enter_at_least_8, Toast.LENGTH_SHORT).show();
                    } else if (!chk_privacy.isChecked()) {
                        Toast.makeText(JoinActivity.this, R.string.agree_privacy, Toast.LENGTH_SHORT).show();
                    } else if (!chk_personal.isChecked()) {
                        Toast.makeText(JoinActivity.this, R.string.agree_personal, Toast.LENGTH_SHORT).show();
                    } else if (isValidEmail(str_email)) {
                        new JoinTask().execute();
                    } else {
                        Toast.makeText(JoinActivity.this.getApplicationContext(), R.string.not_a_valid_email_format, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };



    private class JoinTask extends AsyncTask<String, Void, Void> {
        String user_name, email, password;
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user_name = edit_name.getText().toString();
            email = edit_email.getText().toString();
            password = edit_password.getText().toString().trim();

//            Encryption.setPassword(password);
//            Encryption.encryption(password);
//            password = Encryption.getPassword();

//            progressBar.setVisibility(View.VISIBLE);
            layout_progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "join")
                    .add("user_name", user_name.trim())
                    .add("email", email.trim())
                    .add("password", password)
                    .build();

            //before
//            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();
            //after
            Request request = new Request.Builder().url(Statics.optUrl + "join.php").post(body).build();


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
                        email = obj_user.getString("email");
                        Statics.my_gender = obj_user.getString("gender");
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

                //create user
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                layout_progress.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

                                    Toast.makeText(JoinActivity.this, "환영합니다. 우리 이제 같이먹어요!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), JoinActivity2.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.putExtra("token", token);
                                    intent.putExtra("uid", firebaseAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            } else if (result.equals("1")) {
                layout_progress.setVisibility(View.GONE);
                Toast.makeText(JoinActivity.this.getApplicationContext(), "잘못된 오류입니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("2")) {
                layout_progress.setVisibility(View.GONE);
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