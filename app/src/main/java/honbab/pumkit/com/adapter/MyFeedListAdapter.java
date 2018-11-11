package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.tete.R;
import okhttp3.OkHttpClient;

public class MyFeedListAdapter extends RecyclerView.Adapter<MyFeedListAdapter.ViewHolder> {
    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>() ;

    public MyFeedListAdapter() {

    }

    public MyFeedListAdapter(Context mContext, OkHttpClient httpClient, ArrayList<FeedReqData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_feed, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FeedReqData data = listViewItemList.get(position);
//        final UserData data = listViewItemList.get(position);
        Log.e("abc", "data.getRest_name() = " + data.getRest_name());

        holder.txt_restName.setText(data.getRest_name());


        String feed_id = listViewItemList.get(position).getFeed_id();
        Log.e("abc", "feed_id = " + feed_id);
        ArrayList<UserData> usersList = listViewItemList.get(position).getUsersList();

        if (usersList.size() == 0) {
            holder.txt_no_req.setVisibility(View.VISIBLE);
        } else {
            holder.txt_no_req.setVisibility(View.GONE);

            ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient, feed_id, usersList);
            holder.recyclerView_feedee.setAdapter(mAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no_req;
        TextView txt_restName;
        RecyclerView recyclerView_feedee;
//        recyclerView_comment;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);
            txt_restName = itemView.findViewById(R.id.txt_restName);

            Context context = itemView.getContext();

            //수락대기 리스트
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            recyclerView_feedee = itemView.findViewById(R.id.recyclerView_req_feedee);
            recyclerView_feedee.setLayoutManager(layoutManager);
//            mAdapter = new ReqFeedeeAdapter(context, httpClient, feed_id, usersList);
//            recyclerView.setAdapter(mAdapter);

//            //내가 찌른 피드 리스트
//            LinearLayoutManager layoutManager3 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            recyclerView_poke = itemView.findViewById(R.id.recyclerView_poke);
//            recyclerView_poke.setLayoutManager(layoutManager3);

//            //수락해서 코멘트하기
//            LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            recyclerView_comment = itemView.findViewById(R.id.recyclerView_comment);
//            recyclerView_comment.setLayoutManager(layoutManager2);
        }
    }
}