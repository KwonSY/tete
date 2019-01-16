package honbab.voltage.com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import honbab.voltage.com.adapter.RestLikeOneDateAdapter;
import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.task.RestLikeListTask;
import honbab.voltage.com.tete.DelayBefroePickRestActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class RestLikeFragment extends Fragment {
    private OkHttpClient httpClient;

    public RelativeLayout btn_go_tinder;
    public SwipeRefreshLayout swipeContainer;
    public TextView txt_feedTime;
    public RecyclerView recyclerView;
    public RestLikeOneDateAdapter mAdapter;
    //리스트 없을 때
    public LinearLayout layout_rest;

    public ArrayList<FeedReqData> feedList = new ArrayList<>();
    private String my_id = Statics.my_id;

    public static RestLikeFragment newInstance (int val) {
        RestLikeFragment fragment = new RestLikeFragment();

        Bundle args = new Bundle();
        args.putInt("val", val);
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
        View rootView = inflater.inflate(R.layout.fragment_restlikelist, container, false);

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

        new RestLikeListTask(getActivity(), httpClient).execute();
    }

    private void initControls() {
        //같틴더 가기
        btn_go_tinder = (RelativeLayout) getActivity().findViewById(R.id.btn_go_tinder);
        btn_go_tinder.setOnClickListener(mOnClickListener);

        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer_feed);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clearItemList();
                new RestLikeListTask(getActivity(), httpClient).execute();
            }
        });



        //Rest Like
        txt_feedTime = (TextView) getActivity().findViewById(R.id.txt_feedTime);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView_feed);
        recyclerView.setLayoutManager(layoutManager);
//        mAdapter = new RestLikeListAdapter();
        mAdapter = new RestLikeOneDateAdapter();
        recyclerView.setAdapter(mAdapter);

        //좋아요 레스토랑이 없을 때
        layout_rest = (LinearLayout) getActivity().findViewById(R.id.layout_rest);
        RelativeLayout btn_go_pick_rest = (RelativeLayout) getActivity().findViewById(R.id.btn_go_pick_rest);
        btn_go_pick_rest.setOnClickListener(mOnClickListener);


    }

    public View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_go_tinder:
                    Intent intent = new Intent(getActivity(), DelayBefroePickRestActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    break;
                case R.id.btn_go_pick_rest:
                    Intent intent2 = new Intent(getActivity(), DelayBefroePickRestActivity.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);

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
//                        new CheckReservTask().execute();
//                    }
////                        intent2 = new Intent(getActivity(), ReservActivity.class);
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



}