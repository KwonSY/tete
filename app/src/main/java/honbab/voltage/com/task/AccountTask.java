package honbab.voltage.com.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AccountTask extends AsyncTask<String, Void, UserData> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    private String activityName;

//    HashMap<String, Integer> chatListHash = new HashMap<>();

    private String user_id;
    private int seq;
    private String user_name, user_img, comment, token;

    public AccountTask(Context mContext, int seq) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
//        this.chatListHash = chatListHash;
        this.seq = seq;
    }

    @Override
    protected void onPreExecute() {
        Log.e("abc", "AccountTask mContext = " + mContext);
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
                token = user_obj.getString("token");
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


        if (activityName == null) {

        } else if (activityName.equals("MainActivity")) {
//            Picasso.get().load(userData.getImg_url())
//                    .placeholder(R.drawable.icon_noprofile_circle)
//                    .error(R.drawable.icon_noprofile_circle)
//                    .transform(new CircleTransform())
//                    .into(((MyFeedFragment) fragment).img_my);
//            ((MyFeedFragment) fragment).txt_myName.setText(userData.getUser_name());
//            ((MyFeedFragment) fragment).txt_comment.setText(userData.getComment());

//            userData.setStatus(chatListHash.get(user_id).toString());
//
//            ((MyFeedFragment) fragment).mAdapter_cb.addItem(userData);
//            ((MyFeedFragment) fragment).recyclerView_cb.setAdapter(((MyFeedFragment) fragment).mAdapter_cb);
//
//            ((MyFeedFragment) fragment).title_chatlist.setVisibility(View.VISIBLE);
        } else if (activityName.equals("ProfileActivity")) {
            if (seq == 0) {
                ((ProfileActivity) mContext).title_topbar.setText(user_name + mContext.getResources().getString(R.string.whose_profile));

                Picasso.get().load(user_img)
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
        } else if (activityName.equals("ChatActivity")) {
            ((ChatActivity) mContext).toUserName = user_name;
            ((ChatActivity) mContext).toUserImg = user_img;
            ((ChatActivity) mContext).toToken = token;

            Picasso.get().load(user_img)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(((ChatActivity) mContext).topbar_img_user);
            ((ChatActivity) mContext).txt_userName.setText(user_name);
        }

    }

}