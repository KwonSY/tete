package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.adapter.ChatListAdapter;
import honbab.voltage.com.adapter.MyFeedListAdapter;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.task.MyFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class MyFeedFragment extends Fragment {

    private OkHttpClient httpClient;
    private DatabaseReference mDatabase;

    //채팅리스트
    public TextView title_chatlist;
    public RecyclerView recyclerView_cb;
    public ChatListAdapter mAdapter_cb;
    //마이프로필
    public View line_timeline_vertical;
    public ImageView img_my;
    public TextView txt_myName, txt_comment, btn_go_my_profile;
    //마이피드
    public SwipeRefreshLayout swipeContainer_myfeed;
    public SwipeRefreshLayout swipeContainer;
    public RecyclerView recyclerView_myfeed;
    public MyFeedListAdapter mAdapter;
    //스케쥴없음
    public LinearLayout layout_no_my_schedule;
    public Button btn_go_rest_like;

//    public ArrayList<FeedData> feedList = new ArrayList<>();
    private String my_id = Statics.my_id;

    public static MyFeedFragment newInstance(int val) {
        MyFeedFragment fragment = new MyFeedFragment();

        Bundle args = new Bundle();
        args.putInt("val2", val);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpClient = OkHttpClientSingleton.getInstance().getHttpClient();

        if (my_id == null)
            my_id = "-1";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myfeed, container, false);

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

        new MyFeedListTask(getActivity()).execute();
//        try {
//            UserData myData = new AccountTask(getActivity(), httpClient, 0).execute(my_id).get();
//
//            Picasso.get().load(myData.getImg_url())
//                    .placeholder(R.drawable.icon_noprofile_circle)
//                    .error(R.drawable.icon_noprofile_circle)
//                    .transform(new CircleTransform())
//                    .into(img_my);
//            txt_myName.setText(myData.getUser_name());
//            txt_comment.setText(myData.getComment());
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        loadFirebaseChatList();
    }

    private void initControls() {
        //채팅 리스트
        title_chatlist = (TextView) getActivity().findViewById(R.id.title_chatlist);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_cb = (RecyclerView) getActivity().findViewById(R.id.recyclerView_chat_before);
        recyclerView_cb.setLayoutManager(layoutManager2);
        mAdapter_cb = new ChatListAdapter(getActivity());
        recyclerView_cb.setAdapter(mAdapter_cb);

        //마이 프로필
        img_my = (ImageView) getActivity().findViewById(R.id.img_my);
        txt_myName = (TextView) getActivity().findViewById(R.id.txt_myName);
        txt_comment = (TextView) getActivity().findViewById(R.id.txt_comment);
        btn_go_my_profile = (TextView) getActivity().findViewById(R.id.btn_go_my_profile);
        line_timeline_vertical = (View) getActivity().findViewById(R.id.line_timeline_vertical);
        img_my.setOnClickListener(mOnClickListener);
        btn_go_my_profile.setOnClickListener(mOnClickListener);

        swipeContainer_myfeed = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_myfeed);
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_myfeed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearItemList();
                new MyFeedListTask(getActivity()).execute();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView_myfeed = (RecyclerView) getActivity().findViewById(R.id.recyclerView_myfeed);
        recyclerView_myfeed.setLayoutManager(layoutManager);
        mAdapter = new MyFeedListAdapter();
        recyclerView_myfeed.setAdapter(mAdapter);


        //스케쥴없음
        layout_no_my_schedule = (LinearLayout) getActivity().findViewById(R.id.layout_no_my_schedule);
        btn_go_rest_like = (Button) getActivity().findViewById(R.id.btn_go_rest_like);
        btn_go_rest_like.setOnClickListener(mOnClickListener);
    }

    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_my:
                    Intent intent3 = new Intent(getActivity(), ProfileActivity.class);
                    intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent3.putExtra("user_id", Statics.my_id);
                    startActivity(intent3);

                    break;
                case R.id.btn_go_my_profile:
                    Intent intent2 = new Intent(getActivity(), ProfileActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent2.putExtra("user_id", Statics.my_id);
                    startActivity(intent2);

                    break;
                case R.id.btn_go_rest_like:
//                    Fragment fragment = ((MainActivity) getActivity()).getSupportFragmentManager().findFragmentByTag("page:0");
//
//                    if (((RestLikeFragment) fragment).mAdapter.getItemCount() > 0) {
//                        ((MainActivity) getActivity()).viewPager.setCurrentItem(0);
//                    } else {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                        builder.setMessage("내가 원하는 시간, 음식점을 골라주세요");
//                        builder.setPositiveButton(R.string.go_to_godmuktime,
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent(getActivity(), DelayBefroePickRestActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
//                                    }
//                                });
//                        builder.show();
//                    }
                    ((MainActivity) getActivity()).viewPager.setCurrentItem(0);

                    break;
//                case R.id.btn_go_map:
//                    Intent intent = new Intent(getActivity(), FeedMapActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("feedList", feedList);
//                    startActivity(intent);
//
//                    break;
//                case R.id.btn_reserve_google:
//                    Intent intent2;
//                    if (Statics.my_id == null) {
//                        intent2 = new Intent(getActivity(), LoginActivity.class);
//                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent2);
//                    } else {
////                        new CheckReservTask().execute();
//                    }
//
//                    break;
            }
        }
    };

    public void alertShow(final Activity mActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("AlertDialog Title");
        builder.setMessage(R.string.already_reserved_godmuk);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(getActivity(), MyFeedListActivity.class);
                        Intent intent = new Intent(getActivity(), mActivity.getClass());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.setNegativeButton(R.string.reserve_new_godmuk,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.make_new_godmuk, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), ReservActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivity(intent);
                    }
                });
        builder.show();
    }

    public void loadFirebaseChatList() {
        title_chatlist.setVisibility(View.GONE);

        final int i = 0;
        HashMap<String, Integer> chatListHash = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-messages").child(Statics.my_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                i = 0;
                String toId = dataSnapshot.getKey();
                chatListHash.put(toId, 0);
                mDatabase.child("user-messages").child(Statics.my_id).child(toId).limitToLast(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Object o = dataSnapshot.getValue();
                        int plus = Integer.parseInt(o.toString());

                        if (plus > 0)
                            chatListHash.put(toId, plus);

                        if (chatListHash.size() > mAdapter_cb.getItemCount()) {
                            try {
                                UserData userData = new AccountTask(getActivity(), 0).execute(toId).get();
                                userData.setStatus(chatListHash.get(toId).toString());

                                mAdapter_cb.addItem(userData);
                                recyclerView_cb.setAdapter(mAdapter_cb);

                                title_chatlist.setVisibility(View.VISIBLE);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (mAdapter_cb.getItemCount() == 0)
                            title_chatlist.setVisibility(View.INVISIBLE);
                        else
                            title_chatlist.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}