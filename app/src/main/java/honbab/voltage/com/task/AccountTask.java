package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.network.RestClient;
import honbab.voltage.com.network.RetroInterface;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.Encryption;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountTask extends AsyncTask<String, Void, UserData> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    private String activityName;

    private int seq;
    private String user_id, user_name, user_img, comment, token, fr_status;

    public AccountTask(Context mContext) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
//        this.seq = seq;
    }

    public AccountTask(Context mContext, int seq) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        this.seq = seq;
    }

    @Override
    protected void onPreExecute() {
        if (mContext != null) {
            activityName = mContext.getClass().getSimpleName();
//        fragment = (Fragment) ((MainActivity) mContext).pagerAdapter.instantiateItem(((MainActivity) mContext).viewPager, 1);
            if (activityName.equals("MainActivity")) {
                fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:1");
            }
        }
    }

    @Override
    protected UserData doInBackground(String... objects) {
        UserData userData = new UserData();
        user_id = objects[0];
        Log.e("abc", "user_id  objects[0] = " + user_id);
        Log.e("abc", "auth = " + Encryption.voltAuth());

        FormBody body = new FormBody.Builder()
                .add("opt", "account")
                .add("auth", Encryption.voltAuth())
                .add("my_id", Statics.my_id)
                .add("user_id", objects[0])
                .build();

        Request request = new Request.Builder().url(Statics.optUrl + "profile.php").post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "AccountTask = " + bodyStr);
                JSONObject obj = new JSONObject(bodyStr);

                JSONObject user_obj = obj.getJSONObject("user");
//                user_id = user_obj.getString("sid");
                user_name = user_obj.getString("name");
                String email = user_obj.getString("email");
                String age = user_obj.getString("age");
                String gender = user_obj.getString("gender");
                token = user_obj.getString("token");
                if (user_obj.getString("img_url").contains("http")) {
                    user_img = user_obj.getString("img_url");
                } else {
                    user_img = user_obj.getString("img_url");
                }
                comment = user_obj.getString("comment");
                if (comment.equals("null"))
                    comment = "";

                // 친구관계 fr_req
                fr_status = user_obj.getString("fr_status");

                userData = new UserData(user_id, user_name, age, gender, token, user_img, fr_status, null);
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

        Log.e("abc", "AccountTask user_id = " + user_id);
        Log.e("abc", "AccountTask userData.getUser_id() = " + userData.getUser_id());

        if (activityName == null) {

        } else if (activityName.equals("MainActivity2")) {
            if (userData.getUser_id().equals(Statics.my_id)) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        token = instanceIdResult.getToken();

                        if (!userData.getToken().equals(token))
                            new UpdateTokenTask(mContext).execute(token);
                    }
                });
            }
        } else if (activityName.equals("ProfileActivity")) {
            if (seq == 0) {
                ((ProfileActivity) mContext).title_topbar.setText(user_name + mContext.getResources().getString(R.string.whose_profile));

                Picasso.get().load(user_img)
                        .placeholder(R.drawable.icon_noprofile_circle)
                        .error(R.drawable.icon_noprofile_circle)
                        .transform(new CircleTransform())
                        .into(((ProfileActivity) mContext).img_user);

                ((ProfileActivity) mContext).txt_my_name.setText(user_name);

                if (comment == null || comment.equals("null"))
                    comment = "";
                ((ProfileActivity) mContext).edit_comment.setText(comment);

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

            ((ProfileActivity) mContext).fr_status = fr_status;
            ((ProfileActivity) mContext).btn_add_fr.setEnabled(false);
            if (fr_status.equals("fr")) {
                ((ProfileActivity) mContext).btn_add_fr.setText("친구");
                ((ProfileActivity) mContext).btn_add_fr.setEnabled(true);
            } else if (fr_status.equals("i_required")) {
                ((ProfileActivity) mContext).btn_add_fr.setText("요청중");
                ((ProfileActivity) mContext).btn_add_fr.setEnabled(true);
            } else if (fr_status.equals("wait_accept")) {
                ((ProfileActivity) mContext).explain_req_fr.setText(user_name + "님께서\n친구 요청중");
                ((ProfileActivity) mContext).btn_add_fr.setText("수락");
                ((ProfileActivity) mContext).btn_add_fr.setEnabled(true);
            } else if (fr_status.equals("no_fr")) {
                ((ProfileActivity) mContext).btn_add_fr.setText("+친구맺기");
                ((ProfileActivity) mContext).btn_add_fr.setEnabled(true);
            } else {
                ((ProfileActivity) mContext).btn_add_fr.setVisibility(View.GONE);
                ((ProfileActivity) mContext).btn_add_fr.setText("");
                ((ProfileActivity) mContext).btn_add_fr.setEnabled(false);
            }

        } else if (activityName.equals("ChatActivity")) {
            ((ChatActivity) mContext).toUserName = user_name;
            ((ChatActivity) mContext).toUserImg = user_img;
            ((ChatActivity) mContext).toToken = token;

            Picasso.get().load(user_img)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(((ChatActivity) mContext).topbar_img_user);
            Picasso.get().load(user_img)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(((ChatActivity) mContext).no_chat_img_user);

            ((ChatActivity) mContext).txt_userName.setText(user_name);
            ((ChatActivity) mContext).no_chat_txt_userName.setText(user_name);
            String str_no_chat = String.format(mContext.getResources().getString(R.string.chat_start), user_name);
            ((ChatActivity) mContext).no_chat_explain.setText(str_no_chat);
        }

    }

    // /login
    UserData userData = new UserData();

    public UserData login(String uid) {


        RetroInterface service = RestClient.getService();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("opt", "account");
        jsonObject.addProperty("auth", Encryption.voltAuth());
        jsonObject.addProperty("my_id", Statics.my_id);
        jsonObject.addProperty("user_id", uid);

        Call<JsonObject> call;
        call = service.account(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    if (response.isSuccessful()) {
                        JsonObject obj = response.body();

                        if (obj.has("user")) {
                            JsonObject userObj = obj.get("user").getAsJsonObject();

                            userData = new Gson().fromJson(userObj.getAsJsonObject(), UserData.class);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

        return userData;
    }
}