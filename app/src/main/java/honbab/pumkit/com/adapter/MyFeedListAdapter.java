package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.task.FeedCancleTask;
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
        final String feed_id = listViewItemList.get(position).getFeed_id();
        ArrayList<UserData> usersList = listViewItemList.get(position).getUsersList();

        holder.txt_restName.setText(data.getRest_name());

        try {
            Date tmp_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(data.getFeed_time());
            String feed_time = new SimpleDateFormat("MM/dd a hh:mm").format(tmp_time);
            holder.txt_feedTime.setText(feed_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (usersList.size() == 0) {
            holder.txt_no_req.setVisibility(View.VISIBLE);
        } else {
            holder.txt_no_req.setVisibility(View.GONE);

            ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient, feed_id, usersList);
            holder.recyclerView_feedee.setAdapter(mAdapter);
        }

        holder.btn_feed_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FeedCancleTask(mContext, httpClient, feed_id, listViewItemList.get(position).getRest_name()).execute();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no_req;
        TextView txt_restName, txt_feedTime;
        Button btn_feed_cancle;
        RecyclerView recyclerView_feedee;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);

            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_feedTime = itemView.findViewById(R.id.txt_feedTime);
            btn_feed_cancle = itemView.findViewById(R.id.btn_feed_cancle);

            Context context = itemView.getContext();

            //수락대기 리스트
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            recyclerView_feedee = itemView.findViewById(R.id.recyclerView_req_feedee);
            recyclerView_feedee.setLayoutManager(layoutManager);
        }
    }
}