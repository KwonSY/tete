package honbab.pumkit.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import honbab.pumkit.com.tete.ProfileActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.CircleTransform;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AccountTask extends AsyncTask<Void, Void, Void> {

    private OkHttpClient httpClient;
    private Context mContext;

    private String user_id;
    private int seq;
    private String user_name, user_img, comment;

    public AccountTask(Context mContext, OkHttpClient httpClient,
                       String user_id, int seq) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.user_id = user_id;
        this.seq = seq;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... objects) {
        FormBody body = new FormBody.Builder()
                .add("opt", "account")
                .add("user_id", user_id)
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();

                JSONObject obj = new JSONObject(bodyStr);
                Log.e("abc", "어카운트 : " + obj);

                JSONObject user_obj = obj.getJSONObject("user");
//                    user_id = user_obj.getString("sid");
                user_name = user_obj.getString("name");
                String email = user_obj.getString("email");
                String gender = user_obj.getString("gender");
                String age = user_obj.getString("age");
                user_img = Statics.main_url + user_obj.getString("img_url");
                comment = user_obj.getString("comment");
            } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (seq == 0) {
            Picasso.get().load(user_img)
//                    .resize(200,200)
//                    .centerCrop()
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(((ProfileActivity) mContext).img_user);

            ((ProfileActivity) mContext).txt_my_name.setText(user_name);
            ((ProfileActivity) mContext).edit_comment.setText(comment);
        }

        ((ProfileActivity) mContext).seq++;
    }

}