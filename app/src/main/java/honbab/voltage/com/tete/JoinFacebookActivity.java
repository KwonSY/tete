package honbab.voltage.com.tete;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import honbab.voltage.com.utils.BitmapUtil;
import honbab.voltage.com.utils.KeyboardUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinFacebookActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;

    private TextView txt_email;
    private EditText edit_name;
    private ImageView img_origin, img_user;
    private RadioGroup radio_group;
    private RadioButton radio_male, radio_female;
    private NumberPicker numberPicker;
    private CheckBox chk_privacy, chk_personal;

    private String my_uid, my_name, my_email, my_img, my_password, gender = "m", age = "24", comment = "", my_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_facebook);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());

        KeyboardUtil.hideKeyboard(this);

        Intent intent = getIntent();
        my_uid = intent.getStringExtra("my_uid");
        my_name = intent.getStringExtra("my_name");
        my_email = intent.getStringExtra("my_email");
        my_img = intent.getStringExtra("my_img");
        my_password = intent.getStringExtra("my_password");
//        my_token = intent.getStringExtra("my_token");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                my_token = instanceIdResult.getToken();
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

        txt_email = (TextView) findViewById(R.id.txt_email);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(my_name);
//        edit_password = (EditText) findViewById(R.id.edit_password);
//        edit_password.setText(my_uid);
        txt_email.setText(my_email);
//        StringFilter stringFilter = new StringFilter(this);
//        InputFilter[] allowAlphanumeric = new InputFilter[1];
//        allowAlphanumeric[0] = stringFilter.allowAlphanumeric;
//        edit_password.setFilters(allowAlphanumeric);

//        ImageView btn_show_password = (ImageView) findViewById(R.id.btn_show_password);
//        btn_show_password.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT);
//                        edit_password.setSelection(edit_password.getText().length());
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        edit_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        edit_password.setSelection(edit_password.getText().length());
//                        break;
//                }
//                return true;
//            }
//        });

        img_origin = (ImageView) findViewById(R.id.img_origin);
        img_user = (ImageView) findViewById(R.id.img_user);
        img_user.setOnClickListener(mOnClickListener);
//        Uri image = data.getData();
//        String path = BitmapUtil.getPath(getApplicationContext(), image);
//        File f = new File(path);
//        img_origin.setImageURI(image);
        Picasso.get().load(my_img)
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(img_origin);
        Picasso.get().load(my_img)
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(img_user);

        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        radio_male = (RadioButton) findViewById(R.id.radio_male);
        radio_female = (RadioButton) findViewById(R.id.radio_female);
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

        numberPicker = (NumberPicker) findViewById(R.id.picker_age);
        numberPicker.setMinValue(8);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(24);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // do your other stuff depends on the new value
                age = String.valueOf(newVal);
            }
        });

        final EditText edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                tv.setText(String.valueOf(s.toString().length()));
                if (edit_comment.getText().toString().length() >= 139) {
                    Toast.makeText(getApplicationContext(), "자기소개는 140이내로 해주세요.", Toast.LENGTH_SHORT).show();
                }

                comment = edit_comment.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        chk_privacy = (CheckBox) findViewById(R.id.chk_privacy);
        chk_personal = (CheckBox) findViewById(R.id.chk_personal);

        Button btn_join = (Button) findViewById(R.id.btn_join);
        btn_join.setOnClickListener(mOnClickListener);

//        ButtonUtil.setBackButtonClickListener(this);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_user:
                    selectImage(view);

                    break;
                case R.id.btn_join:
                    String user_name = edit_name.getText().toString().trim();
                    String str_email = my_email;
//                    String password = edit_password.getText().toString().trim();
                    Log.e("abc", "chk_privacy.isChecked() = " + chk_privacy.isChecked());
                    if (user_name.equals("") || user_name == null) {
                        Toast.makeText(JoinFacebookActivity.this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                    } else if (str_email.equals("") || str_email == null) {
                        Toast.makeText(JoinFacebookActivity.this, R.string.enter_email, Toast.LENGTH_SHORT).show();
                    } else if (!chk_privacy.isChecked()) {
                        Toast.makeText(JoinFacebookActivity.this, R.string.agree_privacy, Toast.LENGTH_SHORT).show();
                    } else if (!chk_personal.isChecked()) {
                        Toast.makeText(JoinFacebookActivity.this, R.string.agree_personal, Toast.LENGTH_SHORT).show();
                    } else if (isValidEmail(str_email)) {
                        new JoinFaceBookTask().execute();
                    } else {
                        Toast.makeText(JoinFacebookActivity.this.getApplicationContext(), R.string.not_a_valid_email_format, Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        }
    };

    private class JoinFaceBookTask extends AsyncTask<Void, Void, Void> {
        String user_name, email, password = "facebook";
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            user_name = edit_name.getText().toString();
            email = my_email;
//            password = edit_password.getText().toString().trim();

            Encryption.setPassword(password);
            Encryption.encryption(password);
            password = Encryption.getPassword();
        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "join_facebook")
                    .add("user_name", user_name.trim())
                    .add("email", email.trim())
                    .add("password", password)
                    .add("gender", gender)
                    .add("age", age)
                    .add("comment", comment)
                    .add("img_url", my_img)
                    .add("token", my_token)
                    .add("uid", my_uid)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();
                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");

                    if (result.equals("0") || result.equals("2")) {
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
                session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

                Toast.makeText(JoinFacebookActivity.this, "환영합니다. 우리 이제 같이먹어요!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (result.equals("1")) {
                Toast.makeText(JoinFacebookActivity.this.getApplicationContext(), "잘못된 오류입니다. 다시 시도하세요.", Toast.LENGTH_SHORT).show();
            } else if (result.equals("2")) {
                session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

                Toast.makeText(JoinFacebookActivity.this.getApplicationContext(), "다시 돌아오신 것을 환영합니다.\n우리 이제 같이먹어요!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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

    public String timestamp = "";
    private static final int RESULT_SELECT_IMAGE = 1;

    public void selectImage(View v) {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri image = data.getData();
            String path = BitmapUtil.getPath(getApplicationContext(), image);
            File f = new File(path);

            img_origin.setImageURI(image);
            Picasso.get().load(f)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

//            Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

//            new Upload(img_user, "Profile_" + Statics.my_id + "_" + timestamp).execute();
            Bitmap bitImage = ((BitmapDrawable) img_origin.getDrawable()).getBitmap();
            new UploadProfileTask(bitImage, "Profile_" + Statics.my_id + "_" + timestamp).execute(image);
        }

    }

    private String hashMapToUrl(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private class UploadProfileTask extends AsyncTask<Uri, Void, Uri> {
        private Bitmap image;
        private String name;

        public UploadProfileTask(Bitmap image, String name) {
            this.image = image;
            this.name = name;
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            Bitmap bitmapRotate;
            String path = BitmapUtil.getPath(getApplicationContext(), params[0]);

            try {
                bitmapRotate = BitmapUtil.modifyOrientation(image, path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                bitmapRotate.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

                String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                HashMap<String, String> detail = new HashMap<>();
                detail.put("opt", "upload_profile");
                detail.put("my_id", Statics.my_id);
                detail.put("image", encodeImage);
                detail.put("name", name);

                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = honbab.voltage.com.utils.Request.post(Statics.opt_url, dataToSend);
                Log.e("abc", "response = " + response);

                //return the response
                return params[0];
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }
        }

        @Override
        protected void onPostExecute(Uri image) {
            Toast.makeText(getApplicationContext(), R.string.upload_profile_complete, Toast.LENGTH_SHORT).show();
        }
    }
}