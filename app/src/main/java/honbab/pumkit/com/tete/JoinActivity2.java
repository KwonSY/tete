package honbab.pumkit.com.tete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.io.ByteStreams;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.RealPathUtil;
import honbab.pumkit.com.widget.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class JoinActivity2 extends AppCompatActivity {

    private OkHttpClient httpClient;
    private SessionManager session;
    private RequestQueue mQueue;

    private ImageView img_user;
    private RadioGroup radio_group;
    private RadioButton radio_male, radio_female;
    private NumberPicker numberPicker;

    String gender = "m", age = "24", comment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        session = new SessionManager(getApplicationContext());
        mQueue = Volley.newRequestQueue(this);

        if (Statics.my_id == null) {
            HashMap<String, String> user = session.getUserDetails();
            String my_id = user.get("my_id");
            Statics.my_id = my_id;
        }

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
                if (edit_comment.getText().toString().length() >= 139) {
                    Toast.makeText(JoinActivity2.this, "자기소개는 140이내로 해주세요.", Toast.LENGTH_SHORT).show();
                }

                comment = edit_comment.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button btn_start = (Button) findViewById(R.id.btn_start);
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
                    Log.e("abc", "numberPicker = " + radio_group.getCheckedRadioButtonId());

                    if (mImageUri == null) {
                        Toast.makeText(JoinActivity2.this, R.string.upload_profile_image, Toast.LENGTH_SHORT).show();
                    } else if (numberPicker.getValue() == 0) {
                        Toast.makeText(JoinActivity2.this, R.string.enter_age, Toast.LENGTH_SHORT).show();
                    } else if (radio_group.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(JoinActivity2.this, R.string.enter_gender, Toast.LENGTH_SHORT).show();
                    } else {
                        new UpdateProfileTask().execute();
                    }

                    break;
            }
        }
    };

    private class UpdateProfileTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.e("abc", "xxx = " + Statics.my_id + gender + age + comment);
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_profile")
                    .add("my_id", Statics.my_id)
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
                Toast.makeText(getApplicationContext(), "네트워크 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    int GALLERY_PHOTO = 2;
    public void selectImage(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PHOTO);
    }

    String realPath;
    private Uri mImageUri;
    Bitmap newbitmap;

    private Uri fileUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("abc", "result Code : " + resultCode);

        if (requestCode == GALLERY_PHOTO) {

            if (resultCode == RESULT_OK) {
                // SDK < API 11
                Log.e("abc", "sdk = " + Build.VERSION.SDK_INT);
                if (Build.VERSION.SDK_INT < 11) {

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(JoinActivity2.this, data.getData());
                        setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);

                        mImageUri = data.getData();
                        img_user.setImageURI(mImageUri);
                        Picasso.get().load(mImageUri)
                                .placeholder(R.drawable.icon_noprofile_circle)
                                .error(R.drawable.icon_noprofile_circle)
                                .transform(new CircleTransform())
                                .into(img_user);

                        Log.e("abc", "case sdk below 11 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
                        img_user.setImageBitmap(newbitmap);
                    }
                } else if (Build.VERSION.SDK_INT < 19) {
                    // SDK >= 11 && SDK < 19

                    try {
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(JoinActivity2.this, data.getData());
                        setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);

                        mImageUri = data.getData();
                        Log.e("abc", "case sdk below 17 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e1) {

                        e1.printStackTrace();
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
                        img_user.setImageBitmap(newbitmap);

                    }
                } else {
                    // SDK  >= 19
                    try {
                        mImageUri = data.getData();
//                        img_user.setImageURI(mImageUri);
                        Picasso.get().load(mImageUri)
                                .placeholder(R.drawable.icon_noprofile_circle)
                                .error(R.drawable.icon_noprofile_circle)
                                .transform(new CircleTransform())
                                .into(img_user);

                        Log.e("abc", "case sdk upper 19 = " + Build.VERSION.SDK_INT);
                    } catch (Exception e) {

                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver()
                                .query(selectedImage, filePathColumn, null,
                                        null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor
                                .getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        newbitmap = BitmapFactory.decodeFile(filePath);
                        img_user.setImageBitmap(newbitmap);
                    }
                }

                uploadContents(img_user);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(), "잠시 후 재시도해주세요.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void setTextViews(int sdk, String uriPath, String realPath) {

        Uri uriFromPath = Uri.fromFile(new File(realPath));

        fileUri = uriFromPath;

        try {
            newbitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(fileUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        img_user.setImageURI(fileUri);

        Log.d("abc", "Build.VERSION.SDK_INT:" + sdk);
        Log.d("abc", "URI Path:" + fileUri);
        Log.d("abc", "Real Path: " + realPath);
    }

    public void uploadContents(View v) {
        ProfileUploadRequest req = new ProfileUploadRequest(com.android.volley.Request.Method.POST, Statics.opt_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(JoinActivity2.this, "프로필 사진이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "ErrorResponse", error);
            }
        });

        if (mImageUri != null) {
            req.addFileUpload("uploadfile", mImageUri);
            req.addStringUpload("opt", "upload_profile");
            req.addStringUpload("my_id", Statics.my_id);
        }

        mQueue.add(req);
    }

    public class ProfileUploadRequest extends StringRequest {

        public ProfileUploadRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        // 파일 업로드
        private Map<String, Uri> fileUploads = new HashMap<String, Uri>();

        // 키-밸류 업로드
        private Map<String, String> stringUploads = new HashMap<String, String>();


        public void addFileUpload(String param, Uri uri) {
            fileUploads.put(param, uri);
        }

        public void addStringUpload(String param, String content) {
            stringUploads.put(param, content);
        }

        String boundary = "XXXYYYZZZ";
        String lineEnd = "\r\n";

        @Override
        public byte[] getBody() throws AuthFailureError {
//            Log.d(TAG, "getBody works in MultlpartRequest");
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(os);

                for (String key : stringUploads.keySet()) {
                    dos.writeBytes("--" + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: text/plain; charset-UTF-8" + lineEnd);
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(stringUploads.get(key));
                    dos.writeBytes(lineEnd);
                }

                for (String key : fileUploads.keySet()) {
                    dos.writeBytes("--" + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + key + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/octet-stream" + lineEnd);
                    dos.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Uri uri = fileUploads.get(key);
                    InputStream is = getContentResolver().openInputStream(uri);
                    byte[] fileData = ByteStreams.toByteArray(is);
                    dos.write(fileData);
                    dos.writeBytes(lineEnd);
                }

                dos.writeBytes("--" + boundary + "--" + lineEnd);
                dos.flush();
                dos.close();

                return os.toByteArray();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data; boundary=" + boundary;
        }
    }
}