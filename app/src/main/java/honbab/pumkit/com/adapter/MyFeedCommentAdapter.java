package honbab.pumkit.com.adapter;

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

import java.util.ArrayList;

import honbab.pumkit.com.data.CommentData;
import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.tete.R;
import okhttp3.OkHttpClient;

public class MyFeedCommentAdapter extends RecyclerView.Adapter<MyFeedCommentAdapter.ViewHolder> {

    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>() ;

    public MyFeedCommentAdapter() {

    }

    public MyFeedCommentAdapter(Context context, OkHttpClient httpClient, ArrayList<FeedReqData> listViewItemList) {
        this.mContext = context;
        this.httpClient = httpClient;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_feed_comment, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FeedReqData data = listViewItemList.get(position);
//        final UserData data = listViewItemList.get(position);

        holder.txt_restName.setText(data.getRest_name());


        String feed_id = listViewItemList.get(position).getFeed_id();
        ArrayList<UserData> usersList = listViewItemList.get(position).getUsersList();


        ArrayList<CommentData> commentsList = listViewItemList.get(position).getCommentsList();
        Log.e("abc", "commentsList.size() = " + commentsList.size());

        if (commentsList.size() > 0) {
            holder.txt_no_comment.setVisibility(View.GONE);

            ReqCommentAdapter mAdapter = new ReqCommentAdapter(mContext, httpClient, feed_id, commentsList);
            holder.recyclerView_comment.setAdapter(mAdapter);
        } else {
            holder.txt_no_comment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no_comment;
        TextView txt_restName;
//        RecyclerView recyclerView_feedee;
        RecyclerView recyclerView_comment;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_comment = itemView.findViewById(R.id.txt_no_comment);
            txt_no_comment.setVisibility(View.VISIBLE);
            txt_restName = itemView.findViewById(R.id.txt_restName);

            Context context = itemView.getContext();

            //수락해서 코멘트하기
            LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView_comment = itemView.findViewById(R.id.recyclerView_comment);
            recyclerView_comment.setLayoutManager(layoutManager2);
        }
    }
}