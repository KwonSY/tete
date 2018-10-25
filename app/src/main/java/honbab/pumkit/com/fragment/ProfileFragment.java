package honbab.pumkit.com.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import honbab.pumkit.com.adapter.MyFeedListAdapter;
import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
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

    private ImageView image_myProfile;
    private TextView txt_my_name, txt_comment;
    private TextView title_reserve;
    //    private TextView txt_restName;
//    private ListView listView_myFeed;
    public static RecyclerView recyclerView_myFeed;
    private MyFeedListAdapter myFeedListAdapter;

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
        image_myProfile = (ImageView) getActivity().findViewById(R.id.image_myProfile);
        txt_my_name = (TextView) getActivity().findViewById(R.id.txt_my_name);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);

        title_reserve = (TextView) getActivity().findViewById(R.id.title_reserve);

        // My Feed List
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView_myFeed = (RecyclerView) getActivity().findViewById(R.id.recyclerView_myFeed);
        recyclerView_myFeed.setLayoutManager(layoutManager);
        myFeedListAdapter = new MyFeedListAdapter();
        recyclerView_myFeed.setAdapter(myFeedListAdapter);

        image_myProfile.setOnClickListener(mOnClickListener);

        Button btn_setting = (Button) getActivity().findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(mOnClickListener);

//        txt_restName = (TextView) getActivity().findViewById(R.id.txt_restName);
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
            }
        }
    };

    // load my account
    public class MyAccountTask extends AsyncTask<Void, Void, Void> {
        String user_id, user_name, img_url, comment;

        ImageView img_topbar_profile;

        @Override
        protected void onPreExecute() {
//            ((FeedFragment.class) getActivity())
            img_topbar_profile = (ImageView) getActivity().findViewById(R.id.img_topbar_profile);
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
                    Log.e("abc", "프로필어카운트 : " + obj);

//                    JSONObject obj2 = obj.getJSONObject("reserv");
//
//                    String sid = obj2.getString("sid");

                    JSONObject user_obj = obj.getJSONObject("user");
                    user_id = user_obj.getString("sid");
                    user_name = user_obj.getString("name");
                    String gender = user_obj.getString("gender");
                    img_url = Statics.main_url + user_obj.getString("img_url");
                    comment = user_obj.getString("comment");
//                    Log.e("abc", "img_url : " + img_url);

//                    ReservData data = new ReservData();
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

            // in FeedFragment // topbar_profile 이미지 넣기   <-> FeedFragment myObj랑 바꾸기
//            Picasso.get().load(img_url)
//                    .placeholder(R.drawable.icon_noprofile_circle)
//                    .error(R.drawable.icon_noprofile_circle)
//                    .transform(new CircleTransform())
//                    .into(img_topbar_profile);

            new MyFeedListTask().execute();
        }
    }

    public class MyFeedListTask extends AsyncTask<Void, Void, ArrayList<FeedReqData>> {
        String result;
        String rest_name;

//        ArrayList<FeedReqData> feedReqList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
//            feedReqList.clear();

//            txt_restName = (TextView) getActivity().findViewById(R.id.txt_restName);
        }

        @Override
        protected ArrayList<FeedReqData> doInBackground(Void... params) {
            ArrayList<FeedReqData> feedReqList = new ArrayList<>();

            FormBody body = new FormBody.Builder()
                    .add("opt", "my_feed_list")
                    .add("my_id", my_id)
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);
//                    Log.e("abc", "마이피드리스트 : " + obj);
                    result = obj.getString("result");

                    if (result.equals("0")) {
//                        if (!obj.getJSONObject("host").isNull("") ) {
                        JSONArray feedArr = obj.getJSONArray("feed");
                        for (int i = 0; i < feedArr.length(); i++) {
                            ArrayList<UserData> reqUsersList = new ArrayList<>();
                            ArrayList<CommentData> commentsList = new ArrayList<>();


                            JSONObject feedObj = feedArr.getJSONObject(i);

                            String feed_id = feedObj.getString("sid");

                            JSONObject hostObj = feedObj.getJSONObject("host");
                            String host_id = hostObj.getString("sid");
                            String host_name = hostObj.getString("name");
                            String host_img = hostObj.getString("img_url");
//                            if (host_id.equals(Statics.my_id))
//                                result = "host";
//                            else
//                                result = "not_host";

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
                                    String status = userObj.getString("status");
                                    String time = userObj.getString("time");

                                    UserData userData = new UserData(user_id, user_name, age, gender, user_img, status);
                                    reqUsersList.add(userData);
                                }
                            }

                            if (feedObj.has("comments")) {
                                JSONArray usersArr = feedObj.getJSONArray("comments");

                                for (int k = 0; k < usersArr.length(); k++) {
                                    JSONObject userObj = usersArr.getJSONObject(k);

                                    String comment_id = userObj.getString("sid");
                                    JSONObject c_user_obj = userObj.getJSONObject("user");
                                    String c_user_id = c_user_obj.getString("sid");
                                    String c_user_name = c_user_obj.getString("name");
                                    String c_img_url = Statics.main_url + c_user_obj.getString("img_url");
                                    String comment = userObj.getString("comment");
                                    String comment_time = userObj.getString("time");

                                    CommentData commentData = new CommentData(comment_id, c_user_id, c_user_name, c_img_url, comment, comment_time);
                                    commentsList.add(commentData);
                                }
                            }

                            FeedReqData data = new FeedReqData(feed_id, rest_id, rest_name, rest_img, reqUsersList, commentsList);
                            feedReqList.add(data);
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

            if (feedReqList.size() > 0) {
                title_reserve.setVisibility(View.VISIBLE);

                myFeedListAdapter = new MyFeedListAdapter(getActivity(), httpClient, feedReqList);
                recyclerView_myFeed.setAdapter(myFeedListAdapter);
                myFeedListAdapter.notifyDataSetChanged();
            } else {
                title_reserve.setVisibility(View.GONE);
                recyclerView_myFeed.setVisibility(View.GONE);
            }

//            for (int i=0; i<feedReqList.size(); i++) {
//                FeedReqData data = feedReqList.get(i);
//                ArrayList<UserData> usersList = data.getUsersList();
////                ArrayList<UserData> usersList = feedReqList.get(i).getUsersList();
////                ArrayList<UserData> usersList = data.getUsersList();
////                Log.e("abc", i + "번째, " + "usersList = " + usersList);
//                Log.e("abc", i + "번째, " + "usersList size = " + usersList.size());
//
//                for (int j=0; j<usersList.size(); j++) {
//                    UserData userData = usersList.get(j);
//                    String status = userData.getStatus();
//                    Log.e("abc", "onPostExecute status = " + status);
//
//                    if (status.equals("y")) {
////                    RecyclerView recyclerView = getActivity().findViewById(R.id.recyclerView_req_feedee);
////                    recyclerView.setVisibility(View.GONE);
//                        myFeedListAdapter = new MyFeedListAdapter(getActivity(), httpClient, feedReqList);
//                        recyclerView_myFeed.setAdapter(myFeedListAdapter);
//                        myFeedListAdapter.notifyDataSetChanged();
//                    } else {
//                        title_reserve.setVisibility(View.VISIBLE);
//
//                        myFeedListAdapter = new MyFeedListAdapter(getActivity(), httpClient, feedReqList);
//                        recyclerView_myFeed.setAdapter(myFeedListAdapter);
//                        myFeedListAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                usersList.clear();
//            }
        }

    }

}