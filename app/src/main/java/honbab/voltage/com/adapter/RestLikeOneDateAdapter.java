package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.RestLikeData;
import honbab.voltage.com.tete.R;
import okhttp3.OkHttpClient;

public class RestLikeOneDateAdapter extends RecyclerView.Adapter<RestLikeOneDateAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    public ArrayList<RestLikeData> listViewItemList = new ArrayList<>();
    String feed_time = "";

//    int TYPE_HEADER = 0;
//    int TYPE_STATUS_N = 1;
//    int TYPE_STATUS_Y = 2;

    public RestLikeOneDateAdapter() {

    }

    public RestLikeOneDateAdapter(Context mContext, OkHttpClient httpClient, ArrayList<RestLikeData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_restlike_onedate, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RestLikeData restLikeData = listViewItemList.get(position);

        holder.bindToPost(restLikeData);
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        return TYPE_STATUS_N;
//    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        int holderId;

        public TextView txt_feedTime;
        public RecyclerView recyclerView_onedate;
//        public RestLikeListAdapter mAdapter;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

//            holderId = TYPE_STATUS_N;

            txt_feedTime = itemView.findViewById(R.id.txt_feedTime);

            Context context = itemView.getContext();
            //수락대기 리스트
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView_onedate = itemView.findViewById(R.id.recyclerView_onedate);
            recyclerView_onedate.setLayoutManager(layoutManager);
        }

        public void bindToPost(final RestLikeData restLikeData) {
            final String feed_time = restLikeData.getFeed_time();
            ArrayList<FeedData> feedList = restLikeData.getFeedList();

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MM월 dd일 aa hh시");
                SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = formatter2.parse(feed_time + "");
                Log.e("abc", "date : " + date.toString());
                String str_feed_time = formatter.format(date);
                Log.e("abc", "str_feed_time : " + str_feed_time);

                txt_feedTime.setText(str_feed_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            RestLikeListAdapter mAdapter = new RestLikeListAdapter(mContext, httpClient, feedList);
            recyclerView_onedate.setAdapter(mAdapter);
        }
    }

//    public void removeAt(int position) {
//        listViewItemList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listViewItemList.size());
//    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }
}