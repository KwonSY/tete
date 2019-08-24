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
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import honbab.voltage.com.task.UpdateTokenTask;
import honbab.voltage.com.utils.BitmapUtil;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.SessionManager;
import io.fabric.sdk.android.Fabric;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinActivity2 extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;
    private RequestQueue mQueue;
    private FirebaseAuth mAuth;

    private RelativeLayout layout_progress;
    private ImageView img_origin, img_user;
    private RadioGroup radio_group;
    private RadioButton radio_male, radio_female;
    private NumberPicker numberPicker;
    private Button btn_start;

    String gender = "m", age = "24", comment = "", token, uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_join2);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());
        mQueue = Volley.newRequestQueue(this);
        mAuth = FirebaseAuth.getInstance();

        if (Statics.my_id == null) {
            HashMap<String, String> user = session.getUserDetails();
            Statics.my_id = user.get("my_id");
        }

        layout_progress = (RelativeLayout) findViewById(R.id.layout_progress);

        Intent intent = getIntent();
        token = intent.getStringExtra("token");
        uid = intent.getStringExtra("uid");
        Log.e("abc", "JoinActivity2 token = " + token);
        Log.e("abc", "JoinActivity2 uid = " + uid);
        if (token == null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    token = instanceIdResult.getToken();
//                    new UpdateTokenTask(JoinActivity2.this).execute(token);
                    Log.e("abc", "JoinActivit3 token = " + token);
                }
            });
        }
        if (uid == null) {
            uid = mAuth.getCurrentUser().getUid();
            Log.e("abc", "JoinActivity33 uid = " + uid);
        }

        img_origin = (ImageView) findViewById(R.id.img_origin);
        img_user = (ImageView) findViewById(R.id.img_user);
        img_user.setOnClickListener(mOnClickListener);

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
                if (edit_comment.getText().toString().length() >= 35) {
                    Toast.makeText(JoinActivity2.this, "자기소개는 35이내로 해주세요.", Toast.LENGTH_SHORT).show();
                }

                comment = edit_comment.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(mOnClickListener);
    }

    //selectImage(view);
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_user:
                    selectImage(view);

                    break;
                case R.id.btn_start:
//                    Log.e("abc", "timestamp = " + timestamp);
//                    Log.e("abc", "timestamp = " + timestamp.length());
//                    Log.e("abc", "radio_group = " + radio_group.getCheckedRadioButtonId());
//                    Log.e("abc", "numberPicker = " + numberPicker.getValue());
                    Log.e("abc", "token = " + token);

//                    if (mImageUri == null) {
                    if (timestamp.length() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.upload_profile_image, Toast.LENGTH_SHORT).show();
                    } else if (radio_group.getCheckedRadioButtonId() == -1) {
                        //2131296531 / 2131296529
                        Toast.makeText(getApplicationContext(), R.string.enter_gender, Toast.LENGTH_SHORT).show();
                    } else if (numberPicker.getValue() == 0) {
                        Toast.makeText(getApplicationContext(), R.string.enter_age, Toast.LENGTH_SHORT).show();
                    } else {
                        new UpdateProfileTask().execute(gender, age, comment, token, uid);
                    }

                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        uid = currentUser.getEmail();
        uid = currentUser.getUid();
        uid = currentUser.getUid();
//        updateUI(currentUser);
    }

    private class UpdateProfileTask extends AsyncTask<String, String, Void> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_profile")
                    .add("my_id", Statics.my_id)
                    .add("gender", params[0])
                    .add("age", params[1])
                    .add("comment", params[2])
                    .add("token", params[3])
                    .add("uid", params[4])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();

                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
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

                session.createLoginSession(Statics.my_id, Statics.my_username, Statics.my_gender);

                if (session.isLoggedIn()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            token = instanceIdResult.getToken();

                            if (token.length() > 0)
                                new UpdateTokenTask(JoinActivity2.this).execute(token);
                        }
                    });
                }

//                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (result.equals("1")) {
                Toast.makeText(getApplicationContext(), "네트워크 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    int GALLERY_PHOTO = 2;
    public String timestamp = "";
    private static final int RESULT_SELECT_IMAGE = 1;

    public void selectImage(View v) {
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PHOTO);
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
            new JoinActivity2.UploadProfileTask(bitImage, "Profile_" + Statics.my_id + "_" + timestamp).execute(image);
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

            layout_progress.setVisibility(View.VISIBLE);
            btn_start.setEnabled(false);
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
            layout_progress.setVisibility(View.GONE);
            btn_start.setEnabled(true);
            Toast.makeText(getApplicationContext(), R.string.upload_profile_complete, Toast.LENGTH_SHORT).show();
        }
    }
}