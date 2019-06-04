package honbab.voltage.com.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.EditCommentTask;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.utils.BitmapUtil;
import honbab.voltage.com.utils.Request;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class ProfileFragment extends Fragment {
    private OkHttpClient httpClient;
//    private RequestQueue mQueue;

    private ProgressBar progress_upload;
    public TextView title_topbar, txt_my_name, explain_req_fr;
    public ImageView img_origin, img_user, icon_camera;
    public EditText edit_comment;
    public Button btn_edit_comment, btn_add_fr;

    public int seq = 0;
//    private String user_id;
    public String fr_status = "";

    public ProfileFragment() {

    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
////        mQueue = Volley.newRequestQueue(this);
////
////        Intent intent = getIntent();
////        user_id = intent.getStringExtra("user_id");
////        if (user_id == null) {
////            user_id = Statics.my_id;
////        }
////
////        progress_upload = (ProgressBar) findViewById(R.id.progress_upload);
////        progress_upload.setVisibility(View.GONE);
////
////        title_topbar = (TextView) findViewById(R.id.title_topbar);
////        img_origin = (ImageView) findViewById(R.id.img_origin);
////        img_user = (ImageView) findViewById(R.id.img_user);
////        txt_my_name = (TextView) findViewById(R.id.txt_my_name);
////        // when MY PROFILE
////        ImageView img_setting = (ImageView) findViewById(R.id.img_setting);
////        icon_camera = (ImageView) findViewById(R.id.icon_camera);
////        // when NOT MY PROFILE
////        explain_req_fr = (TextView) findViewById(R.id.explain_req_fr);
////        btn_add_fr = (Button) findViewById(R.id.btn_add_fr);
////
////        img_user.setOnClickListener(mOnClickListener);
////        img_setting.setOnClickListener(mOnClickListener);
////        btn_add_fr.setOnClickListener(mOnClickListener);
////
////        edit_comment = (EditText) findViewById(R.id.edit_comment);
////        edit_comment.setEnabled(false);
////        edit_comment.addTextChangedListener(new TextWatcher() {
////            @Override
////            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////                if (s.length() > 35) {
////                    Toast.makeText(ProfileFragment.this, "35자 이내로 작성해주세요.", Toast.LENGTH_SHORT).show();
//////                    edit_comment.setText(s.subSequence(0, 35));
////                }
////            }
////
////            @Override
////            public void onTextChanged(CharSequence s, int start, int before, int count) {
////
////            }
////
////            @Override
////            public void afterTextChanged(Editable s) {
////
////            }
////        });
////        btn_edit_comment = (Button) findViewById(R.id.btn_edit_comment);
////        btn_edit_comment.setOnClickListener(mOnClickListener);
////
////        //마이 프로필 구분
////        if (user_id.equals(Statics.my_id)) {
////            img_setting.setVisibility(View.VISIBLE);
////            icon_camera.setVisibility(View.VISIBLE);
////            btn_add_fr.setVisibility(View.GONE);
////        } else {
////            img_setting.setVisibility(View.GONE);
////            icon_camera.setVisibility(View.GONE);
////            btn_add_fr.setVisibility(View.VISIBLE);
////            btn_edit_comment.setVisibility(View.GONE);
////        }
////
////        ButtonUtil.setBackButtonClickListener(this);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_profile, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    private void initControls() {
        progress_upload = (ProgressBar) getActivity().findViewById(R.id.progress_upload);
        progress_upload.setVisibility(View.GONE);

        img_user = (ImageView) getActivity().findViewById(R.id.img_user);
        txt_my_name = (TextView) getActivity().findViewById(R.id.txt_my_name);
        edit_comment = (EditText) getActivity().findViewById(R.id.edit_comment);
        btn_edit_comment = (Button) getActivity().findViewById(R.id.btn_edit_comment);

        edit_comment.setEnabled(false);
        edit_comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() > 35) {
                    Toast.makeText(getActivity(), "35자 이내로 작성해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_edit_comment = (Button) getActivity().findViewById(R.id.btn_edit_comment);
        btn_edit_comment.setOnClickListener(mOnClickListener);
//        Button btn_go_select_public;
//        btn_go_select_public = (Button) getActivity().findViewById(R.id.btn_go_select_public);
//        btn_go_select_public.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume(){
        super.onResume();

        try {
            UserData userData = new AccountTask(getActivity(), seq).execute(Statics.my_id).get();

            Picasso.get().load(userData.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            txt_my_name.setText(userData.getUser_name());
            String comment = "";
//            comment = userData.getComment();
//            if (comment.equals("null"))
//                comment = "";
            if (userData.getComment() == null || userData.getComment().equals("null"))
                comment = "";
            else
                comment = userData.getComment();
            edit_comment.setText(comment);

            if (userData.getComment().length() == 0) {
                edit_comment.setEnabled(true);
                edit_comment.setBackgroundResource(R.drawable.border_round_bk1);
                edit_comment.setTextColor(getActivity().getResources().getColor(R.color.black));
                btn_edit_comment.setText(R.string.save);
            } else {
                edit_comment.setEnabled(false);
                edit_comment.setTextColor(getActivity().getResources().getColor(R.color.black));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    boolean bool_edityn = false;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_select_public:
//                    if (user_id.equals(Statics.my_id))
                        selectImage();

                    break;
                case R.id.btn_edit_comment:
                    String comment = edit_comment.getText().toString();

                    if (bool_edityn) {
                        //편집 -> 저장


                        if (comment.length() > 35)
                            Toast.makeText(getActivity(), "저장되지 않았습니다. 35자 이내로 내 소개를 작성해주세요.", Toast.LENGTH_SHORT).show();
                        else {
                            //수정중
                            bool_edityn = false;
                            edit_comment.setEnabled(false);
//                        edit_comment.setSelection(comment.length());
                            edit_comment.setTextColor(getResources().getColor(R.color.grey));
                            edit_comment.setBackgroundResource(R.drawable.border_round_gr1);
                            btn_edit_comment.setText(R.string.edit);

                            new EditCommentTask(getActivity(), comment, seq).execute();
                        }
                    } else {
                        //default only showing
                        //저장 -> 편집
//                        String comment = edit_comment.getText().toString();
                        edit_comment.setFocusable(true);
//                        edit_comment.requestFocus();
//                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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

//    public String SERVER = Statics.opt_url;
    public String timestamp;
    private static final int RESULT_SELECT_IMAGE = 1;

    //function to select a image
    private void selectImage() {
        //open album to select image
        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallaryIntent, RESULT_SELECT_IMAGE);
    }

    int RESULT_OK = -1;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uriImage = data.getData();
            String path = BitmapUtil.getPath(getActivity(), uriImage);
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
            //"Profile_" + Statics.my_id + "_" + timestamp
            new UploadProfileTask(bitImage).execute(uriImage);
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
//        private String name;

        public UploadProfileTask(Bitmap image) {
            this.image = image;
//            this.name = name;

            progress_upload.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Uri... params) {
            Bitmap bitmapRotate;
            String path = BitmapUtil.getPath(getActivity(), params[0]);

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
                detail.put("auth", Encryption.voltAuth());
                detail.put("my_id", Statics.my_id);
                detail.put("image", encodeImage);
//                detail.put("name", name);

                //convert this HashMap to encodedUrl to send to php file
                String dataToSend = hashMapToUrl(detail);
                //make a Http request and send data to saveImage.php file
                String response = Request.post(Statics.optUrl + "profile.php", dataToSend);
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
            Toast.makeText(getActivity(), "프로필이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

}