package honbab.voltage.com.tete;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

    public TextView title_topbar;
    public ImageView img_origin, img_user;
    public TextView txt_my_name;
    public EditText edit_comment;
    public Button btn_edit_comment;

    public int seq = 0;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        mQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        Log.e("abc", "ProfileAct user_id = " + user_id);
        if (user_id == null) {
            user_id = Statics.my_id;
        }

        title_topbar = (TextView) findViewById(R.id.title_topbar);
        img_origin = (ImageView) findViewById(R.id.img_origin);
        img_user = (ImageView) findViewById(R.id.img_user);
        img_user.setOnClickListener(mOnClickListener);
        txt_my_name = (TextView) findViewById(R.id.txt_my_name);

        edit_comment = (EditText) findViewById(R.id.edit_comment);
        edit_comment.setEnabled(false);
        btn_edit_comment = (Button) findViewById(R.id.btn_edit_comment);
        btn_edit_comment.setOnClickListener(mOnClickListener);
        if (!user_id.equals(Statics.my_id))
            btn_edit_comment.setVisibility(View.GONE);

        ButtonUtil.setBackButtonClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new AccountTask(ProfileActivity.this, httpClient, user_id, seq).execute();
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
                    if (bool_edityn) {
                        String comment = edit_comment.getText().toString();
                        //수정중
                        bool_edityn = false;
                        edit_comment.setEnabled(false);
//                        edit_comment.setSelection(comment.length());
                        edit_comment.setTextColor(getResources().getColor(R.color.grey));
                        edit_comment.setBackgroundResource(R.drawable.border_round_gr1);
                        btn_edit_comment.setText(R.string.edit);

                        new EditCommentTask(ProfileActivity.this, httpClient, comment, seq).execute();
                    } else {
                        //default only showing
                        String comment = edit_comment.getText().toString();

                        bool_edityn = true;
                        edit_comment.setEnabled(true);
                        edit_comment.setSelection(comment.length());
                        edit_comment.setTextColor(getResources().getColor(R.color.black));
                        edit_comment.setBackgroundResource(R.drawable.border_round_bk1);
                        btn_edit_comment.setText(R.string.save);

                        new EditCommentTask(ProfileActivity.this, httpClient, comment, seq).execute();
                    }

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

            Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();

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


    //async task to upload image
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
        protected void onPostExecute(Uri image) {
            //show image uploaded
            Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
        }
    }


//    String realPath;
//    private Uri mImageUri;
//    Bitmap newbitmap;
//    private Uri fileUri;
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("abc", "result Code : " + resultCode);
//
//        if (requestCode == GALLERY_PHOTO) {
//
//            if (resultCode == RESULT_OK) {
//                // SDK < API 11
//                if (Build.VERSION.SDK_INT < 11) {
//                    try {
//                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
//                        setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);
//
//                        mImageUri = data.getData();
//                        img_user.setImageURI(mImageUri);
//                        Picasso.get().load(mImageUri)
//                                .placeholder(R.drawable.icon_noprofile_circle)
//                                .error(R.drawable.icon_noprofile_circle)
//                                .transform(new CircleTransform())
//                                .into(img_user);
//
//                        Log.e("abc", "case sdk below 11 = " + Build.VERSION.SDK_INT);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = getContentResolver()
//                                .query(selectedImage, filePathColumn, null,null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        String filePath = cursor.getString(columnIndex);
//                        cursor.close();
//
//                        newbitmap = BitmapFactory.decodeFile(filePath);
//                        img_user.setImageBitmap(newbitmap);
//                    }
//                } else if (Build.VERSION.SDK_INT < 19) {
//                    // SDK >= 11 && SDK < 19
//
//                    try {
//                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
//                        setTextViews(Build.VERSION.SDK_INT, data.getData().getPath(), realPath);
//
//                        mImageUri = data.getData();
//                        Log.e("abc", "case sdk below 17 = " + Build.VERSION.SDK_INT);
//                    } catch (Exception e1) {
//
//                        e1.printStackTrace();
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = getContentResolver()
//                                .query(selectedImage, filePathColumn, null,
//                                        null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor
//                                .getColumnIndex(filePathColumn[0]);
//                        String filePath = cursor.getString(columnIndex);
//                        cursor.close();
//
//                        newbitmap = BitmapFactory.decodeFile(filePath);
//                        img_user.setImageBitmap(newbitmap);
//
//                    }
//                } else {
//                    // SDK  >= 19
//                    try {
//                        mImageUri = data.getData();
//                        Picasso.get().load(mImageUri)
//                                .placeholder(R.drawable.icon_noprofile_circle)
//                                .error(R.drawable.icon_noprofile_circle)
//                                .transform(new CircleTransform())
//                                .into(img_user);
//                        Log.e("abc", "mImageUri = " + mImageUri);
//                        Log.e("abc", "case sdk upper 19 = " + Build.VERSION.SDK_INT);
//                    } catch (Exception e) {
//                        Uri selectedImage = data.getData();
//                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                        Cursor cursor = getContentResolver()
//                                .query(selectedImage, filePathColumn, null,
//                                        null, null);
//                        cursor.moveToFirst();
//
//                        int columnIndex = cursor
//                                .getColumnIndex(filePathColumn[0]);
//                        String filePath = cursor.getString(columnIndex);
//                        cursor.close();
//
//                        newbitmap = BitmapFactory.decodeFile(filePath);
//                        img_user.setImageBitmap(newbitmap);
//                    }
//                }
//
//                uploadContents(img_user);
//
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
//            } else {
//                // failed to capture image
//                Toast.makeText(getApplicationContext(), "잠시 후 재시도해주세요.", Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }
//
//    private void setTextViews(int sdk, String uriPath, String realPath) {
//        Uri uriFromPath = Uri.fromFile(new File(realPath));
//
//        fileUri = uriFromPath;
//
//        try {
//            newbitmap = BitmapFactory.decodeStream(getContentResolver()
//                    .openInputStream(fileUri));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        img_user.setImageURI(fileUri);
//    }
//
//    public void uploadContents(View v) {
//        ProfileUploadRequest req = new ProfileUploadRequest(ProfileActivity.this, com.android.volley.Request.Method.POST, Statics.opt_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Toast.makeText(ProfileActivity.this, R.string.upload_profile_complete, Toast.LENGTH_SHORT).show();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                Log.e(TAG, "ErrorResponse", error);
//            }
//        });
//
////        Log.e("abc", "uploadContents = ", mImageUri);
//        if (mImageUri != null) {
//            req.addFileUpload("uploadfile", mImageUri);
//            req.addStringUpload("opt", "upload_profile");
//            req.addStringUpload("my_id", Statics.my_id);
//        }
//
//        mQueue.add(req);
//    }
}