package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
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

public class MyFeedListAdapter extends RecyclerView.Adapter<MyFeedListAdapter.ViewHolder> {
    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>() ;

    public MyFeedListAdapter() {

    }

    public MyFeedListAdapter(Context context, OkHttpClient httpClient, ArrayList<FeedReqData> listViewItemList) {
        this.mContext = context;
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
//        Log.e("abc", "data getImg_url = " + data.getImg_url());

//        Picasso.get().load(data.getImg_url())
//                .placeholder(R.drawable.icon_noprofile_circle)
//                .error(R.drawable.icon_noprofile_circle)
//                .transform(new CircleTransform())
//                .into(holder.image_feedee);
        holder.txt_restName.setText(data.getRest_name());


        String feed_id = listViewItemList.get(position).getFeed_id();
        ArrayList<UserData> usersList = listViewItemList.get(position).getUsersList();

//        Context context = itemView.getContext();
//        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
//        holder.recyclerView = itemView.findViewById(R.id.recyclerView_req_feedee);
//        holder.recyclerView.setLayoutManager(layoutManager);

        if (usersList.size() == 0) {
            holder.txt_no_req.setVisibility(View.VISIBLE);
        } else {
            holder.txt_no_req.setVisibility(View.GONE);

            String acceptYn = "n";
            String acceptUserName = null;

            for (int i=0; i<usersList.size(); i++) {
                String status = usersList.get(i).getStatus();


                if (status.equals("y")) {
                    acceptYn = "y";
                    acceptUserName = usersList.get(i).getUser_name();
                }
            }

            Log.e("abc", "acceptYn = " + acceptYn);
            if (acceptYn.equals("y")) {
                holder.recyclerView_feedee.setVisibility(View.GONE);
                holder.recyclerView_comment.setVisibility(View.VISIBLE);

                holder.txt_restName.setText(data.getRest_name() + " (" + acceptUserName + "님과의 식사)");

                ArrayList<CommentData> commentsList = listViewItemList.get(position).getCommentsList();
                Log.e("abc", "commentsList.size() = " + commentsList.size());

                ReqCommentAdapter mAdapter = new ReqCommentAdapter(mContext, httpClient, feed_id, commentsList);
                holder.recyclerView_comment.setAdapter(mAdapter);
            } else {
                holder.recyclerView_comment.setVisibility(View.GONE);

                ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient, feed_id, usersList);
                holder.recyclerView_feedee.setAdapter(mAdapter);
            }
        }

//        txt_no_req.setVisibility(View.VISIBLE);


//        holder.recyclerView.setRecycledViewPool(viewPool);


//        holder.btn_accept.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int action = motionEvent.getAction();
//
//                if (action == MotionEvent.ACTION_DOWN) {
////                    holder.btn_accept.setBackgroundColor(mContext.getResources(R.color.darkgrey));
//                    holder.btn_accept.setBackgroundColor(Color.GRAY);
//                } else if (action == MotionEvent.ACTION_UP) {
//                    holder.btn_accept.setBackgroundColor(Color.WHITE);
//
//                    new AcceptReservTask(mContext, httpClient, holder, data, feed_id, position)
//                            .execute(feed_id, data.getUser_id());
//
//                    holder.btn_accept.setText("수락완료");
//                }
//
//                return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_no_req;
        TextView txt_restName;
        RecyclerView recyclerView_feedee, recyclerView_comment;

        public ViewHolder(View itemView) {
            super(itemView);

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);
            txt_restName = itemView.findViewById(R.id.txt_restName);

            Context context = itemView.getContext();

            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            recyclerView_feedee = itemView.findViewById(R.id.recyclerView_req_feedee);
            recyclerView_feedee.setLayoutManager(layoutManager);
//            mAdapter = new ReqFeedeeAdapter(context, httpClient, feed_id, usersList);
//            recyclerView.setAdapter(mAdapter);

            LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            recyclerView_comment = itemView.findViewById(R.id.recyclerView_comment);
            recyclerView_comment.setLayoutManager(layoutManager2);
        }
    }
}