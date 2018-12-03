package honbab.pumkit.com.tete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;

import honbab.pumkit.com.task.AccountTask;
import honbab.pumkit.com.task.EditCommentTask;
import honbab.pumkit.com.utils.ButtonUtil;
import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import honbab.pumkit.com.widget.ProfileUploadRequest;
import honbab.pumkit.com.widget.RealPathUtil;
import okhttp3.OkHttpClient;

public class ProfileActivity extends AppCompatActivity {

    private OkHttpClient httpClient;
    private RequestQueue mQueue;

    public TextView title_topbar;
    public ImageView img_user;
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
        if (user_id==null) {
            user_id = Statics.my_id;
        }
        Log.e("abc", "user_id = " + user_id);

        title_topbar = (TextView) findViewById(R.id.title_topbar);
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
                    selectImage(view);

                    break;
                case R.id.btn_edit_comment:
                    Log.e("abc","bool_edityn = " + bool_edityn);
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

    //이미지 저장하기
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
                if (Build.VERSION.SDK_INT < 11) {
                    try {
                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
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
                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
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
                        Picasso.get().load(mImageUri)
                                .placeholder(R.drawable.icon_noprofile_circle)
                                .error(R.drawable.icon_noprofile_circle)
                                .transform(new CircleTransform())
                                .into(img_user);
                        Log.e("abc", "mImageUri = " + mImageUri);
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
    }

    public void uploadContents(View v) {
        ProfileUploadRequest req = new ProfileUploadRequest(ProfileActivity.this, com.android.volley.Request.Method.POST, Statics.opt_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ProfileActivity.this, R.string.upload_profile_complete, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "ErrorResponse", error);
            }
        });

//        Log.e("abc", "uploadContents = ", mImageUri);
        if (mImageUri != null) {
            req.addFileUpload("uploadfile", mImageUri);
            req.addStringUpload("opt", "upload_profile");
            req.addStringUpload("my_id", Statics.my_id);
        }

        mQueue.add(req);
    }
}