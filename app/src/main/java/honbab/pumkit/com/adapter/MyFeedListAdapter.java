package honbab.pumkit.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import honbab.pumkit.com.data.FeedReqData;
import honbab.pumkit.com.data.UserData;
import honbab.pumkit.com.task.FeedCancleTask;
import honbab.pumkit.com.tete.ChatActivity;
import honbab.pumkit.com.tete.R;
import okhttp3.OkHttpClient;

public class MyFeedListAdapter extends RecyclerView.Adapter<MyFeedListAdapter.ViewHolder> {
    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>();

    int TYPE_STATUS_N = 0;
    int TYPE_STATUS_Y = 1;

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
        final View view;

        if (viewType == TYPE_STATUS_N)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_feed_n, parent, false);
        else//TYPE_STATUS_Y
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_feed_y, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FeedReqData data = listViewItemList.get(position);
        ArrayList<UserData> usersList = data.getUsersList();
        final String feed_id = data.getFeed_id();

        Picasso.get().load(data.getRest_img())
                .resize(70, 70).centerCrop()
                .placeholder(R.drawable.icon_no_image).error(R.drawable.icon_no_image)
                .into(holder.img_rest);
        holder.txt_restName.setText(data.getRest_name());

        try {
            Date tmp_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(data.getFeed_time());
            String feed_time = new SimpleDateFormat("MM/dd a hh:mm").format(tmp_time);
            holder.txt_feedTime.setText(feed_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.btn_feed_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.ask_cancle_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new FeedCancleTask(mContext, httpClient, feed_id, listViewItemList.get(position).getRest_name()).execute();
                            }
                        });
                builder.setNegativeButton(R.string.no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        if (holder.holderId == TYPE_STATUS_N) {
            //TYPE_STATUS_N
            final ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient, feed_id, usersList);

            if (usersList.size() == 0) {
                holder.txt_no_req.setVisibility(View.VISIBLE);
                holder.btn_choose_feeder.setVisibility(View.GONE);
            } else {
                holder.txt_no_req.setVisibility(View.GONE);
                holder.btn_choose_feeder.setVisibility(View.VISIBLE);

                holder.recyclerView_feedee.setAdapter(mAdapter);
            }



            holder.btn_choose_feeder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.bool_choose) {
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            ImageView tmp = (ImageView) holder.recyclerView_feedee
                                    .findContainingItemView(holder.recyclerView_feedee.getChildAt(i))
                                    .findViewById(R.id.btn_check_feedee);
                            tmp.setVisibility(View.VISIBLE);
                        }

                        holder.btn_choose_feeder.setText(R.string.cancellation);
                        holder.bool_choose = false;
                    } else {
//                        new AcceptReservTask(mContext, httpClient).execute();
                        for (int i = 0; i < mAdapter.getItemCount(); i++) {
                            ImageView tmp = (ImageView) holder.recyclerView_feedee
                                    .findContainingItemView(holder.recyclerView_feedee.getChildAt(i))
                                    .findViewById(R.id.btn_check_feedee);
                            tmp.setVisibility(View.GONE);
                        }

                        holder.btn_choose_feeder.setText(R.string.accept);
                        holder.bool_choose = true;
                    }
                }
            });
        } else {
            //TYPE_STATUS_Y
//            holder.txt_no_req.setVisibility(View.GONE);
//            holder.txt_restName.setText(data.getRest_name());


            for (int i=0; i<usersList.size(); i++) {
                if (usersList.get(i).getStatus().equals("y")) {
                    holder.toId = String.valueOf(usersList.get(i).getUser_id());
                    holder.toNm = String.valueOf(usersList.get(i).getUser_name());
                    holder.toImg = String.valueOf(usersList.get(i).getImg_url());
                }
            }

            holder.btn_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("fromId", data.getHost_id());
                    intent.putExtra("toId", holder.toId);
                    intent.putExtra("toUserName", holder.toNm);
                    intent.putExtra("toUserImg", holder.toImg);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {

        String status = listViewItemList.get(position).getStatus();

        if (status.equals("n")) {
            return TYPE_STATUS_N;
        } else {
            return TYPE_STATUS_Y;
        }
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;
        public boolean bool_choose = true;
        public String fromId, fromImg, toId, toNm, toImg;

        TextView txt_no_req;
        TextView txt_restName, txt_feedTime;
        Button btn_feed_cancle;
        RecyclerView recyclerView_feedee;
        Button btn_choose_feeder;

        ImageView img_rest, img_feeder;
        Button btn_chat;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_STATUS_N) {
                holderId = TYPE_STATUS_N;

                txt_no_req = itemView.findViewById(R.id.txt_no_req);
                txt_no_req.setVisibility(View.VISIBLE);
                btn_choose_feeder = itemView.findViewById(R.id.btn_choose_feeder);

                Context context = itemView.getContext();
                //수락대기 리스트
                LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                recyclerView_feedee = itemView.findViewById(R.id.recyclerView_req_feedee);
                recyclerView_feedee.setLayoutManager(layoutManager);
            } else {
                holderId = TYPE_STATUS_Y;

                img_feeder = itemView.findViewById(R.id.img_feeder);
                btn_chat = itemView.findViewById(R.id.btn_chat);
            }

            img_rest = itemView.findViewById(R.id.img_rest);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_feedTime = itemView.findViewById(R.id.txt_feedTime);
            btn_feed_cancle = itemView.findViewById(R.id.btn_feed_cancle);
        }
    }
}