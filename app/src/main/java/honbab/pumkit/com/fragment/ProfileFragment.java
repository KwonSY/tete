package honbab.pumkit.com.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.tete.CommentTalkActivity;
import honbab.pumkit.com.tete.MyFeedListActivity;
import honbab.pumkit.com.tete.PokeListActivity;
import honbab.pumkit.com.tete.ProfileActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.SettingActivity;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.CircleTransform;
import honbab.pumkit.com.widget.OkHttpClientSingleton;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileFragment extends Fragment {

    private OkHttpClient httpClient;

    public String my_id = Statics.my_id;

    private ImageButton btn_setting;
    private ImageView image_myProfile;
    private TextView txt_my_name, txt_comment;
    private TextView title_reserve;
    private RelativeLayout layout_go_feedlist, layout_go_pokelist, layout_go_talk;
    public TextView badge_feed_cnt, badge_poke_cnt, badge_comment_cnt;

    int cnt_my = 0, cnt_your = 0, cnt_comment = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        initControls();
    }

    @Override
    public void onResume() {
        super.onResume();

        new MyAccountTask().execute();
    }

    private void initControls() {
        //마이탭 프로필
        image_myProfile = (ImageView) getActivity().findViewById(R.id.image_myProfile);
        txt_my_name = (TextView) getActivity().findViewById(R.id.txt_my_name);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);

        image_myProfile.setOnClickListener(mOnClickListener);

        btn_setting = (ImageButton) getActivity().findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(mOnClickListener);
        btn_setting.setOnTouchListener(mOnTouchListener);

        //3가지 버튼
        layout_go_feedlist = (RelativeLayout) getActivity().findViewById(R.id.layout_go_feedlist);
        layout_go_pokelist = (RelativeLayout) getActivity().findViewById(R.id.layout_go_pokelist);
        layout_go_talk = (RelativeLayout) getActivity().findViewById(R.id.layout_go_talk);
        layout_go_feedlist.setOnClickListener(mOnClickListener);
        layout_go_pokelist.setOnClickListener(mOnClickListener);
        layout_go_talk.setOnClickListener(mOnClickListener);
        badge_feed_cnt = (TextView) getActivity().findViewById(R.id.badge_feed_cnt);
        badge_poke_cnt = (TextView) getActivity().findViewById(R.id.badge_poke_cnt);
        badge_comment_cnt = (TextView) getActivity().findViewById(R.id.badge_comment_cnt);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.image_myProfile:
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_setting:
                    Intent intent2 = new Intent(getActivity(), SettingActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

                    break;
                case R.id.layout_go_feedlist:
                    Intent intent3 = new Intent(getActivity(), MyFeedListActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent3.putExtra("feedList", myFeedList);
                    startActivity(intent3);

                    break;
                case R.id.layout_go_pokelist:
                    Intent intent4 = new Intent(getActivity(), PokeListActivity.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent4.putExtra("feedList", pokeList);
                    startActivity(intent4);

                    break;
                case R.id.layout_go_talk:
                    Intent intent5 = new Intent(getActivity(), CommentTalkActivity.class);
                    intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent5.putExtra("feedList", commentList);
                    startActivity(intent5);

                    break;
            }
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    btn_setting.setImageAlpha(70);

                    break;
                case MotionEvent.ACTION_UP:
                    btn_setting.setImageAlpha(255);

                    break;
            }
            return false;
        }
    };

    // load my account
    public class MyAccountTask extends AsyncTask<Void, Void, Void> {
        String user_id, user_name, img_url, comment;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "account")
                    .add("user_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    JSONObject user_obj = obj.getJSONObject("user");
                    user_id = user_obj.getString("sid");
                    user_name = user_obj.getString("name");
                    String gender = user_obj.getString("gender");
                    img_url = Statics.main_url + user_obj.getString("img_url");
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
        protected void onPostExecute(Void aVoid) {
            txt_my_name.setText(user_name);
            txt_comment.setText(comment);
            Picasso.get().load(img_url)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(image_myProfile);

            new MyFeedListTask().execute();
        }
    }

    ArrayList<FeedReqData> myFeedList = new ArrayList<>();
    ArrayList<FeedReqData> pokeList = new ArrayList<>();
    public class MyFeedListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        String result;
        String host;
        String rest_name;

        @Override
        protected void onPreExecute() {
            myFeedList.clear();
            pokeList.clear();

            cnt_my = 0;
            cnt_your = 0;
            cnt_comment = 0;
        }

        @Override
        protected ArrayList<FeedReqData> doInBackground(Void... params) {
            ArrayList<FeedReqData> feedReqList = new ArrayList<>();

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_totla_feed_cnt")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
                    Log.e("abc", "my_feed_list = " + obj);

                    result = obj.getString("result");

                    String cnt_my_feed = obj.getString("cnt_my_feed");
                    String cnt_poke = obj.getString("cnt_poke");
                    String cnt_comm = obj.getString("cnt_comment");
                    cnt_my = Integer.parseInt(cnt_my_feed);
                    cnt_your = Integer.parseInt(cnt_poke);
                    cnt_comment = Integer.parseInt(cnt_comm);

                    if (result.equals("0")) {
//                        if (!obj.getJSONObject("host").isNull("") ) {
                        JSONArray feedArr = obj.getJSONArray("feed");
                        for (int i = 0; i < feedArr.length(); i++) {
                            ArrayList<UserData> reqUsersList = new ArrayList<>();
                            ArrayList<CommentData> commentsList = new ArrayList<>();


                            JSONObject feedObj = feedArr.getJSONObject(i);

                            String feed_id = feedObj.getString("sid");
                            host = feedObj.getString("host");
//                            if (host.equals("my"))
//                                cnt_my++;
//                            else if (host.equals("your"))
//                                cnt_your++;
//                            else if (host.equals("comment"))
//                                cnt_comment++;

                            JSONObject restObj = feedObj.getJSONObject("rest");
                            String rest_id = restObj.getString("sid");
                            rest_name = restObj.getString("name");
                            Double lat = Double.parseDouble(restObj.getString("lat"));
                            Double lng = Double.parseDouble(restObj.getString("lng"));
                            String rest_img = restObj.getString("img_url");

                            if (feedObj.has("users")) {
                                JSONArray usersArr = feedObj.getJSONArray("users");

                                for (int j = 0; j < usersArr.length(); j++) {
                                    JSONObject userObj = usersArr.getJSONObject(j);

                                    String user_id = userObj.getString("sid");
                                    String user_name = userObj.getString("name");
                                    String user_img = Statics.main_url + userObj.getString("img_url");
                                    String age = userObj.getString("age");
                                    String gender = userObj.getString("gender");
                                    String user_status = userObj.getString("status");
                                    String time = userObj.getString("time");

                                    UserData userData = new UserData(user_id, user_name, age, gender, user_img, user_status);
                                    reqUsersList.add(userData);
                                }
                            }

//                            if (feedObj.has("comments")) {
//                                JSONArray usersArr = feedObj.getJSONArray("comments");
//
//                                for (int k = 0; k < usersArr.length(); k++) {
//                                    JSONObject userObj = usersArr.getJSONObject(k);
//
//                                    String comment_id = userObj.getString("sid");
//                                    JSONObject c_user_obj = userObj.getJSONObject("user");
//                                    String c_user_id = c_user_obj.getString("sid");
//                                    String c_user_name = c_user_obj.getString("name");
//                                    String c_img_url = Statics.main_url + c_user_obj.getString("img_url");
//                                    String comment = userObj.getString("comment");
//                                    String comment_time = userObj.getString("time");
//
//                                    CommentData commentData = new CommentData(comment_id, c_user_id, c_user_name, c_img_url, comment, comment_time);
//                                    commentsList.add(commentData);
//                                }
//                            }

                            FeedReqData data = new FeedReqData(feed_id, rest_id, rest_name, rest_img, reqUsersList, commentsList);
                            feedReqList.add(data);

                            if (host.equals("my"))
                                myFeedList.add(data);
                            else if (host.equals("your"))
                                pokeList.add(data);

                            String status = feedObj.getString("status");
                        }
                    }

                } else {
//                    Log.d(TAG, "Error : " + response.code() + ", " + response.message());
                }

            } catch (Exception e) {
                Log.e("abc", "Error : " + e.getMessage());
                e.printStackTrace();
            }
            return feedReqList;
        }

        @Override
        protected void onPostExecute(ArrayList<FeedReqData> feedReqList) {
            super.onPostExecute(feedReqList);
//            Log.e("abc", "onPostExecute feedReqList.size = " + feedReqList.size());

            badge_feed_cnt.setText(String.valueOf(cnt_my));
            badge_poke_cnt.setText(String.valueOf(cnt_your));
            badge_comment_cnt.setText(String.valueOf(cnt_comment));

            if (cnt_my==0)
                badge_feed_cnt.setVisibility(View.GONE);
            if (cnt_your==0)
                badge_poke_cnt.setVisibility(View.GONE);
            if (cnt_comment==0)
                badge_comment_cnt.setVisibility(View.GONE);
        }

    }

}