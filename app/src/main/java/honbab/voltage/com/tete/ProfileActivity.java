package honbab.voltage.com.tete;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.AddFrTask;
import honbab.voltage.com.task.EditCommentTask;
import honbab.voltage.com.utils.BitmapUtil;
import honbab.voltage.com.utils.ButtonUtil;
import honbab.voltage.com.utils.Request;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class ProfileActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private RequestQueue mQueue;

    private ProgressBar progress_upload;
    public TextView title_topbar, txt_my_name, explain_req_fr;
    public ImageView img_origin, img_user, icon_camera;
    public EditText edit_comment;
    public Button btn_edit_comment, btn_add_fr;

    public int seq = 0;
    private String user_id;
    public String fr_status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        mQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        if (user_id == null) {
            user_id = Statics.my_id;
        }

        progress_upload = (ProgressBar) findViewById(R.id.progress_upload);
        progress_upload.setVisibility(View.GONE);

        title_topbar = (TextView) findViewById(R.id.title_topbar);
        img_origin = (ImageView) findViewById(R.id.img_origin);
        img_user = (ImageView) findViewById(R.id.img_user);
        txt_my_name = (TextView) findViewById(R.id.txt_my_name);
        // when MY PROFILE
        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);
        icon_camera = (ImageView) findViewById(R.id.icon_camera);
        // when NOT MY PROFILE
        explain_req_fr = (TextView) findViewById(R.id.explain_req_fr);
        btn_add_fr = (Button) findViewById(R.id.btn_add_fr);

        img_user.setOnClickListener(mOnClickListener);
        img_setting.setOnClickListener(mOnClickListener);
        btn_add_fr.setOnClickListener(mOnClickListener);

        edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_comment.setEnabled(false);
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 35) {
                    Toast.makeText(ProfileActivity.this, "35자 이내로 작성해주세요.", Toast.LENGTH_SHORT).show();
//                    edit_comment.setText(s.subSequence(0, 35));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_edit_comment = (Button) findViewById(R.id.btn_edit_comment);
        btn_edit_comment.setOnClickListener(mOnClickListener);

        //마이 프로필 구분
        if (user_id.equals(Statics.my_id)) {
            img_setting.setVisibility(View.VISIBLE);
            icon_camera.setVisibility(View.VISIBLE);
            btn_add_fr.setVisibility(View.GONE);
        } else {
            img_setting.setVisibility(View.GONE);
            icon_camera.setVisibility(View.GONE);
            btn_add_fr.setVisibility(View.VISIBLE);
            btn_edit_comment.setVisibility(View.GONE);
        }

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AccountTask(ProfileActivity.this, seq).execute(user_id);
    }

    @Override
    protected void onStop() {
        super.onStop();

        img_user.setImageDrawable(null);
    }

    boolean bool_edityn = false;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_user:
                    if (user_id.equals(Statics.my_id))
                        selectImage();

                    break;
                case R.id.btn_edit_comment:
                    String comment = edit_comment.getText().toString();
                    Log.e("abc", "상태 = " + bool_edityn);

                    if (bool_edityn) {
                        //편집 -> 저장


                        if (comment.length() > 35)
                            Toast.makeText(ProfileActivity.this, "저장되지 않았습니다. 35자 이내로 내 소개를 작성해주세요.", Toast.LENGTH_SHORT).show();
                        else {
                            //수정중
                            bool_edityn = false;
                            edit_comment.setEnabled(false);
//                        edit_comment.setSelection(comment.length());
                            edit_comment.setTextColor(getResources().getColor(R.color.grey));
                            edit_comment.setBackgroundResource(R.drawable.border_round_gr1);
                            btn_edit_comment.setText(R.string.edit);

                            new EditCommentTask(ProfileActivity.this, httpClient, comment, seq).execute();
                        }
                    } else {
                        //default only showing
                        //저장 -> 편집
//                        String comment = edit_comment.getText().toString();
                        edit_comment.setFocusable(true);
//                        edit_comment.requestFocus();
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(edit_comment, InputMethodManager.SHOW_IMPLICIT);
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                        bool_edityn = true;
                        edit_comment.setEnabled(true);
                        edit_comment.setSelection(comment.length());
                        edit_comment.setTextColor(getResources().getColor(R.color.black));
                        edit_comment.setBackgroundResource(R.drawable.border_round_bk1);
                        btn_edit_comment.setText(R.string.save);


//                            new EditCommentTask(ProfileActivity.this, httpClient, comment, seq).execute();
                    }

                    break;
                case R.id.img_setting:
                    Intent intent2 = new Intent(getApplicationContext(), SettingActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
                case R.id.btn_add_fr:
                    if (fr_status.equals("wait_accept")) {
                        btn_add_fr.setText("친구");
                    } else {
                        btn_add_fr.setText("요청중");
                    }
                    btn_add_fr.setEnabled(false);

                    new AddFrTask(ProfileActivity.this).execute(user_id);

                    break;
            }
        }
    };

//    //이미지 저장하기
//    int GALLERY_PHOTO = 2;
//    public void selectImage(View v) {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        startActivityForResult(intent, 1);
////        intent.setAction(Intent.ACTION_GET_CONTENT);
////        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_PHOTO);
//    }

    public String SERVER = Statics.opt_url;
    public String timestamp;
    private static final int RESULT_SELECT_IMAGE = 1;

    //function to select a image
    private void selectImage() {
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }

    /*
     * This function is called when we pick some image from the album
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uriImage = data.getData();
            String path = BitmapUtil.getPath(getApplicationContext(), uriImage);
            File f = new File(path);

            img_origin.setImageURI(uriImage);
//            Picasso.get().load(f)
//                    .placeholder(R.drawable.icon_noprofile_circle)
//                    .error(R.drawable.icon_noprofile_circle)
//                    .transform(new CircleTransform())
//                    .into(img_user);

            //get the current timeStamp and strore that in the time Variable
            Long tsLong = System.currentTimeMillis() / 1000;
            timestamp = tsLong.toString();

            Bitmap bitImage = ((BitmapDrawable) img_origin.getDrawable()).getBitmap();
            new UploadProfileTask(bitImage, "Profile_" + Statics.my_id + "_" + timestamp).execute(uriImage);
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


    //async task to upload image
    private class UploadProfileTask extends AsyncTask<Uri, Void, Uri> {
        private Bitmap image;
        private String name;

        public UploadProfileTask(Bitmap image, String name) {
            this.image = image;
            this.name = name;

            progress_upload.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            Bitmap bitmapRotate;
            String path = BitmapUtil.getPath(getApplicationContext(), params[0]);

            try {
                bitmapRotate = BitmapUtil.modifyOrientation(image, path);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //compress the image to jpg format
//            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                bitmapRotate.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                /*
                 * encode image to base64 so that it can be picked by saveImage.php file
                 * */
                String encodeImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                //generate hashMap to store encodedImage and the name
                HashMap<String, String> detail = new HashMap<>();
                detail.put("opt", "upload_profile");
                detail.put("my_id", Statics.my_id);
                detail.put("image", encodeImage);
                detail.put("name", name);

                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = Request.post(SERVER, dataToSend);
                Log.e("abc", "response = " + response);

                //return the response
                return params[0];
            } catch (IOException e) {
                e.printStackTrace();

                return null;
            }


//            try {
//                //convert this HashMap to encodedUrl to send to php file
//                String dataToSend = hashMapToUrl(detail);
//                //make a Http request and send data to saveImage.php file
//                String response = Request.post(SERVER, dataToSend);
//                Log.e("abc", "response = " + response);
//
//                //return the response
//                return params[0];
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("abc", "ERROR  " + e);
//                return null;
//            }
        }

        @Override
        protected void onPostExecute(Uri uriImage) {
            Picasso.get().load(uriImage)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            //show image uploaded
            progress_upload.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "프로필이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}