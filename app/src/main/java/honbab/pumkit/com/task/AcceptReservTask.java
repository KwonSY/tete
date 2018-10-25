package honbab.pumkit.com.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import honbab.pumkit.com.adapter.ReqFeedeeAdapter;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.tete.DelayHandlerActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AcceptReservTask extends AsyncTask<String, Void, String> {
    private OkHttpClient httpClient;
    private Context mContext;

    ReqFeedeeAdapter.ViewHolder viewHolder;
    UserData data;
    String feed_id;
    int position;

//    ActivityOptionsCompat optionsCompat;

    public AcceptReservTask(Context mContext, OkHttpClient httpClient, ReqFeedeeAdapter.ViewHolder viewHolder,
                            UserData userData, String feed_id,
                            int position) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.viewHolder = viewHolder;
        this.data = userData;
        this.feed_id = feed_id;
        this.position = position;
    }

    @Override
    protected void onPreExecute() {
        Log.e("abc", "xxxxviewHolderxxxx1  viewHolder = " + viewHolder);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            View v = ((MyFeedListAdapter) mContext).getItemViewType(1)
////            ProfileFragment.recyclerView_myFeed.findViewHolderForAdapterPosition
//            //vvvvvvvvvvvvvvvv adapter에서 view를 다시 잡고 transite 잡기
////            MyFeedListAdapter.findViewHolderforLayoutPosition
//            int xxx = viewHolder.getLayoutPosition();
//            int xxx2 = viewHolder.getAdapterPosition();
//            long xxx3 = viewHolder.getItemId();
//            Log.e("abc" , "xxxxxx1 = " + xxx);
//            Log.e("abc" , "xxxxxx2 = " + xxx2);
//            Log.e("abc" , "xxxxxx3 = " + xxx3);
//            View rootView = ((MainActivity)mContext).getWindow().getDecorView().findViewById(R.id.layout_item_myfeed);
//
//            Pair<View, String> pair1 = Pair.create(rootView.findViewById(R.id.image_feedee), "img_user");
//            Pair<View, String> pair2 = Pair.create(rootView.findViewById(R.id.txt_userName), "txt_userName");
//
//            optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, pair1, pair2);
//        }
    }

    @Override
    protected String doInBackground(String... objects) {
        Log.e("abc", "objects[0] = " + objects[0] + ", objects[1]" + objects[1]);
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
                Log.e("abc", "bodyStr = " + bodyStr);

                JSONObject obj = new JSONObject(bodyStr);

                result = obj.getString("result");

//                reqArr = obj.getJSONArray("req");
//                for (int i=0; i<reqArr.length(); i++) {
//                    JSONObject reqUserObj = reqArr.getJSONObject(i);
//                    String sid = reqUserObj.getString("sid");
//                    String name = reqUserObj.getString("name");
//                    String img_url = reqUserObj.getString("img_url");
//                }
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
//                Log.e("abc", "xxxxviewHolderxxxx2  viewHolder = " + viewHolder);
//                Log.e("abc", "xxxxviewHolderxxxx2  viewHolder.getAdapterPosition() = " + viewHolder.getAdapterPosition());
//                Log.e("abc", "xxxxviewHolderxxxx2  getLayoutPosition = " + viewHolder.getLayoutPosition());
//                Log.e("abc", "xxxxviewHolderxxxx2  getItemId = " + viewHolder.getItemId());
//                Log.e("abc", "xxxxviewHolderxxxx2  itemView = " + viewHolder.itemView);
//                View view2 = (View) ((ViewGroup) v.getParent().getParent()).getChildAt(1);
//                int fragment_id = view2.getId();

//                View rootView = ((MainActivity)mContext).getWindow().getDecorView().findViewById(R.id.layout_item_myfeed);
                View rootView = viewHolder.itemView;

                Pair<View, String> pair1 = Pair.create(rootView.findViewById(R.id.image_feedee), "img_user");
                Pair<View, String> pair2 = Pair.create(rootView.findViewById(R.id.txt_userName), "txt_userName");

                ActivityOptionsCompat optionsCompat;
                optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)mContext, pair1, pair2);

                Intent intent = new Intent(mContext, DelayHandlerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                String user_name = null;
//                String user_img = null;
//                try {
//                    JSONObject obj = reqArr.getJSONObject(position);
//                    JSONObject userObj = obj.getJSONObject("user");
//                    user_name = userObj.getString("name");
//                    user_img = userObj.getString("img_url");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                intent.putExtra("user_name", data.getUser_name());
                intent.putExtra("user_img", data.getImg_url());
                mContext.startActivity(intent, optionsCompat.toBundle());
            }

    }

}