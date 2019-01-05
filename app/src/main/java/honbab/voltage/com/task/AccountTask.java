package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AccountTask extends AsyncTask<String, Void, UserData> {
    private OkHttpClient httpClient;
    private Context mContext;

    private String user_id;
    private int seq;
    private String user_name, user_img, comment;

    public AccountTask(Context mContext, OkHttpClient httpClient, int seq) {
        this.mContext = mContext;
        this.httpClient = httpClient;
//        this.user_id = user_id;
        this.seq = seq;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected UserData doInBackground(String... objects) {
        UserData userData = new UserData();
        user_id = objects[0];

        FormBody body = new FormBody.Builder()
                .add("opt", "account")
                .add("user_id", objects[0])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);

                JSONObject user_obj = obj.getJSONObject("user");
//                user_id = user_obj.getString("sid");
                user_name = user_obj.getString("name");
                String email = user_obj.getString("email");
                String age = user_obj.getString("age");
                String gender = user_obj.getString("gender");
                String token = user_obj.getString("token");
                user_img = Statics.main_url + user_obj.getString("img_url");
                comment = user_obj.getString("comment");

                userData = new UserData(user_id, user_name, age, gender, token, user_img, null);
                userData.setComment(comment);
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return userData;
    }

    @Override
    protected void onPostExecute(UserData userData) {
        super.onPostExecute(userData);

        String activityName = mContext.getClass().getSimpleName();
        if (activityName.equals("ProfileActivity")) {
            if (seq == 0) {
                ((ProfileActivity) mContext).title_topbar.setText(user_name + mContext.getResources().getString(R.string.whose_profile));

                Picasso.get().load(user_img)
//                    .resize(200,200)
//                    .centerCrop()
                        .placeholder(R.drawable.icon_noprofile_circle)
                        .error(R.drawable.icon_noprofile_circle)
                        .transform(new CircleTransform())
                        .into(((ProfileActivity) mContext).img_user);

                ((ProfileActivity) mContext).txt_my_name.setText(user_name);

                if (comment==null || comment.equals("null"))
                    comment = "";
                ((ProfileActivity) mContext).edit_comment.setText(comment);
//            ((ProfileActivity) mContext).edit_comment.setEnabled(false);

                if (user_id.equals(Statics.my_id)) {
                    if (comment.length() == 0) {
                        ((ProfileActivity) mContext).edit_comment.setEnabled(true);
                        ((ProfileActivity) mContext).edit_comment.setBackgroundResource(R.drawable.border_round_bk1);
                        ((ProfileActivity) mContext).edit_comment.setTextColor(mContext.getResources().getColor(R.color.black));
                        ((ProfileActivity) mContext).btn_edit_comment.setText(R.string.save);
                    }
                } else {
                    ((ProfileActivity) mContext).edit_comment.setEnabled(false);
                    ((ProfileActivity) mContext).edit_comment.setTextColor(mContext.getResources().getColor(R.color.black));
                }
            }

            ((ProfileActivity) mContext).seq++;
        } else {
            //MainActivity > MyFeedFragment는 get으로 해당 fragment에서 처리
        }

    }

}