package honbab.voltage.com.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AcceptFeedTask extends AsyncTask<String, Void, String> {
    private OkHttpClient httpClient;
    private Context mContext;

//    ReqFeedeeAdapter.ViewHolder viewHolder;
//    UserData userData;
//    String feed_id;
//    String rest_id;
//    String place_id;
    String user_id, user_name, user_img, user_token;
    int position;

    public AcceptFeedTask(Context mContext, int position) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        this.position = position;
    }

//    public AcceptFeedTask(Context mContext, ReqFeedeeAdapter.ViewHolder viewHolder,
//                          UserData userData, String feed_id, String rest_id, String place_id, int position) {
//        this.mContext = mContext;
//        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
//        this.viewHolder = viewHolder;
//
//        this.userData = userData;
//        this.feed_id = feed_id;
//        this.rest_id = rest_id;
//        this.place_id = place_id;
//        this.position = position;
//    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... objects) {
        String result = null;

        FormBody body = new FormBody.Builder()
                .add("opt", "accept_feed")
                .add("my_id", Statics.my_id)
                .add("feed_id", objects[0])
                .add("user_id", objects[1])
                .build();

        Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

        try {
            okhttp3.Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String bodyStr = response.body().string();
                Log.e("abc", "AcceptFeedTask = " + bodyStr);

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");

                JSONObject userObj = obj.getJSONObject("user");
                user_id = userObj.getString("sid");
                user_name = userObj.getString("name");
                user_img = userObj.getString("img_url");
                user_token = userObj.getString("token");
            } else {
                    Log.d("abc", "Error : " + response.code() + ", " + response.message());
            }

        } catch (Exception e) {
            Log.e("abc", "Error : " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
            if (result.equals("0")) {
//                View view2 = (View) ((ViewGroup) v.getParent().getParent()).getChildAt(1);
//                int fragment_id = view2.getId();
//                View rootView = viewHolder.itemView;

//                Pair<View, String> pair1 = Pair.create(rootView.findViewById(R.id.img_feedee), "img_user");
//                Pair<View, String> pair2 = Pair.create(rootView.findViewById(R.id.txt_feedee_name), "txt_userName");

//                ActivityOptionsCompat optionsCompat;
//                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, pair1, pair2);
//                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((MyFeedListActivity) mContext, pair1, pair2);

//                ((Activity) mContext).finish();
//                Intent intent = new Intent(mContext, DelayHandlerActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("user_name", userData.getUser_name());
//                intent.putExtra("user_img", userData.getImg_url());
//                intent.putExtra("rest_id", rest_id);
//                intent.putExtra("place_id", place_id);
//                mContext.startActivity(intent, optionsCompat.toBundle());

//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage(user_name + "님과 같이 식사하시겠습니까? 이후에 대화창이 열립니다.");
//                builder.setPositiveButton(R.string.yes,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
////                                new PokeFeedTask(mContext, httpClient).execute(restData.getRest_id(), data.getUser_id(), feed_time);
////                                new AcceptFeedTask(mContext, httpClient, holder, data, feed_id, restData.getRest_id(), restData.getPlace_id(), position)
////                                        .execute(feed_id, data.getUser_id());
//                                Intent intent = new Intent(mContext, ChatActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                intent.putExtra("fromId", Statics.my_id);
////                    intent.putExtra("fromUserName", Statics.my_username);
//                                intent.putExtra("toId", user_id);
//                                intent.putExtra("toUserName", user_name);
//                                intent.putExtra("toUserImg", user_img);
//                                intent.putExtra("toToken", user_token);
//                                intent.putExtra("restData", new RestData());
//                                mContext.startActivity(intent);
//                            }
//                        });
//                builder.setNegativeButton(R.string.no,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
////                                holder.btn_check_feedee.setBackgroundResource(R.drawable.icon_check_n);
//                            }
//                        });
//                builder.show();

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fromId", Statics.my_id);
                intent.putExtra("toId", user_id);
                intent.putExtra("toUserName", user_name);
                intent.putExtra("toUserImg", user_img);
                intent.putExtra("toToken", user_token);
                intent.putExtra("restData", new RestData());
                mContext.startActivity(intent);
            }

    }

}