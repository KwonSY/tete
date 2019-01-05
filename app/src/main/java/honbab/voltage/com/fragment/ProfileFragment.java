package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.ArrayList;

import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.task.MyFeedListTask2;
import honbab.voltage.com.tete.PokeListActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.SettingActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.VerticalItemDecoration;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ProfileFragment extends Fragment {
    private OkHttpClient httpClient;

    public String my_id = Statics.my_id;
    private static final String ARG_PARAM1 = "param1";

    private ArrayList<FeedReqData> mParam1;

    private ImageButton btn_setting;
    private ImageView image_myProfile;
    private TextView txt_my_name, txt_comment;
//    private TextView title_reserve;
//    private RelativeLayout layout_go_feedlist, layout_go_pokelist, layout_go_talk;
    private RelativeLayout layout_go_pokelist;
    public static TextView badge_poke_cnt;
    public SwipeRefreshLayout swipeContainer;
    public static RecyclerView recyclerView_myFeed;
    public static MyFeedListAdapter myFeedListAdapter;

    private String token;
    private ArrayList<FeedReqData> pokeList = new ArrayList<>();



    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(boolean refresh);
    }

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(ArrayList<FeedReqData> param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = (ArrayList<FeedReqData>) getArguments().getSerializable(ARG_PARAM1);
            Log.e("abc", "0 mParam1 = " + mParam1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        if (getArguments() != null) {
            //xxxxxxxxxxx 안 돌아감
            mParam1 = (ArrayList<FeedReqData>) getArguments().getSerializable(ARG_PARAM1);
            Log.e("abc", "1 mParam1 = " + mParam1);

            MyFeedListAdapter mAdapter= new MyFeedListAdapter(getActivity(), httpClient, mParam1);
            recyclerView_myFeed.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }

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
        /*
        //마이탭 프로필
        image_myProfile = (ImageView) getActivity().findViewById(R.id.image_myProfile);
        txt_my_name = (TextView) getActivity().findViewById(R.id.txt_my_name);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);

        image_myProfile.setOnClickListener(mOnClickListener);

        btn_setting = (ImageButton) getActivity().findViewById(R.id.btn_setting);
        btn_setting.setOnClickListener(mOnClickListener);
        btn_setting.setOnTouchListener(mOnTouchListener);

        //3가지 버튼
        layout_go_pokelist = (RelativeLayout) getActivity().findViewById(R.id.layout_go_pokelist);
        layout_go_pokelist.setOnClickListener(mOnClickListener);
        badge_poke_cnt = (TextView) getActivity().findViewById(R.id.badge_poke_cnt);
        */

        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myFeedListAdapter.clearItemList();
                new MyFeedListTask2(getActivity(), httpClient).execute();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView_myFeed = (RecyclerView) getActivity().findViewById(R.id.recyclerView_myFeed);
        recyclerView_myFeed.setLayoutManager(layoutManager);
        if (recyclerView_myFeed.getItemDecorationCount() == 0)
        recyclerView_myFeed.addItemDecoration(new VerticalItemDecoration(20, false));
        myFeedListAdapter = new MyFeedListAdapter();
        recyclerView_myFeed.setAdapter(myFeedListAdapter);
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
//                case R.id.layout_go_feedlist:
//                    Intent intent3 = new Intent(getActivity(), MyFeedListActivity.class);
//                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    intent3.putExtra("feedList", myFeedList);
//                    startActivity(intent3);
//
//                    break;
                case R.id.layout_go_pokelist:
                    Intent intent4 = new Intent(getActivity(), PokeListActivity.class);
                    intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent4.putExtra("feedList", pokeList);
                    startActivity(intent4);

                    break;
//                case R.id.layout_go_talk:
//                    Intent intent5 = new Intent(getActivity(), ChatActivity.class);
////                    Intent intent5 = new Intent(getActivity(), CommentTalkActivity.class);
//                    intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent5);
//
//                    break;
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
        String user_id, user_name, img_url, comment, user_token;

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
                    if (comment.equals("null"))
                        comment = "";
                    user_token = user_obj.getString("token");
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
            /*
            txt_my_name.setText(user_name);
            txt_comment.setText(comment);
            Picasso.get().load(img_url)
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(image_myProfile);
            */
            new MyFeedListTask2(getActivity(), httpClient).execute();

            if (!token.equals(user_token))
                new UpdateToken().execute(token);
        }
    }

    public class UpdateToken extends AsyncTask<String, Void, Void> {
        String result;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            FormBody body = new FormBody.Builder()
                    .add("opt", "update_token")
                    .add("my_id", my_id)
                    .add("token", params[0])
                    .build();

            Request request = new Request.Builder().url(Statics.opt_url).post(body).build();

            try {
                okhttp3.Response response = httpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String bodyStr = response.body().string();

                    JSONObject obj = new JSONObject(bodyStr);

                    result = obj.getString("result");
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

        }
    }

    public void setSwipeRefresh(boolean refresh) {
        swipeContainer.setRefreshing(refresh);
    }
}