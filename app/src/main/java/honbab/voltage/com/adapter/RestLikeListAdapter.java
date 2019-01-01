package honbab.voltage.com.adapter;

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

import java.util.ArrayList;

import honbab.voltage.com.data.FeedReqData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.FeedCancleTask;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import okhttp3.OkHttpClient;

public class RestLikeListAdapter extends RecyclerView.Adapter<RestLikeListAdapter.ViewHolder> {
    Context mContext;
    OkHttpClient httpClient;
    public ArrayList<FeedReqData> listViewItemList = new ArrayList<>();

//    int TYPE_HEADER = 0;
    int TYPE_STATUS_N = 1;
//    int TYPE_STATUS_Y = 2;

    public RestLikeListAdapter() {

    }

    public RestLikeListAdapter(Context mContext, OkHttpClient httpClient, ArrayList<FeedReqData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = httpClient;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_my_feed_n, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final FeedReqData data = listViewItemList.get(position);
        final int feed_id = data.getFeed_id();
        final int rest_id = data.getRest_id();
        final String place_id = data.getPlace_id();
        ArrayList<UserData> usersList = data.getUsersList();

        Picasso.get().load(data.getRest_img())
                .resize(70, 70).centerCrop()
                .placeholder(R.drawable.icon_no_image).error(R.drawable.icon_no_image)
                .into(holder.img_rest);
        holder.img_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("feed_id", data.getFeed_id());
                intent.putExtra("rest_name", data.getRest_name());
                intent.putExtra("compound_code", data.getCompound_code());
                intent.putExtra("rest_phone", data.getRest_phone());
                intent.putExtra("feed_time", data.getFeed_time());
                intent.putExtra("latLng", data.getLatLng());
                intent.putExtra("place_id", data.getPlace_id());
                intent.putExtra("vicinity", data.getVicinity());
//                intent.putExtra("latLng", data.getLatLng());
                intent.putExtra("feeder_id", data.getHost_id());
                intent.putExtra("feeder_name", data.getHost_name());
                intent.putExtra("feeder_img", Statics.main_url + data.getHost_img());
                intent.putExtra("status", data.getStatus());
                mContext.startActivity(intent);
            }
        });
        holder.txt_restName.setText(data.getRest_name());


        //TYPE_STATUS_N
        final ReqFeedeeAdapter mAdapter = new ReqFeedeeAdapter(mContext, httpClient,
                feed_id, rest_id, place_id, data.getRest_phone(), usersList);

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
//                mAdapter.activateButtons(holder.bool_choose);
//                Log.e("abc", "holder.bool_choose = " + holder.bool_choose);
//                if (holder.bool_choose) {
//                    holder.btn_choose_feeder.setText(R.string.cancellation);
//                    holder.bool_choose = false;
//                } else {
//                    holder.btn_choose_feeder.setText(R.string.accept);
//                    holder.bool_choose = true;
//                }
            }
        });

        holder.btn_feed_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.ask_cancle_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new FeedCancleTask(mContext, httpClient,
                                        feed_id, listViewItemList.get(position).getRest_name(), position)
                                        .execute();
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



//        Date tmp_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(data.getFeed_time());
//        String feed_time = new SimpleDateFormat("MM/dd a hh:mm").format(tmp_time);
//        holder.txt_feedTime.setText(feed_time);
    }

    @Override
    public int getItemViewType(int position) {

        return TYPE_STATUS_N;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        int holderId;
        public boolean bool_choose = true;
        public String fromId, fromImg, toId, toNm, toImg, toToken;

        TextView txt_no_req;
        TextView txt_restName, txt_feedTime;
        Button btn_feed_cancle;
        RecyclerView recyclerView_feedee;
        Button btn_choose_feeder;

        ImageView img_rest, img_feeder;
        TextView txt_feedee_name;
        Button btn_chat;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            holderId = TYPE_STATUS_N;

            txt_no_req = itemView.findViewById(R.id.txt_no_req);
            txt_no_req.setVisibility(View.VISIBLE);
            btn_choose_feeder = itemView.findViewById(R.id.btn_choose_feeder);

            Context context = itemView.getContext();
            //수락대기 리스트
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            recyclerView_feedee = itemView.findViewById(R.id.recyclerView_req_feedee);
            recyclerView_feedee.setLayoutManager(layoutManager);

            img_rest = itemView.findViewById(R.id.img_rest);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_feedTime = itemView.findViewById(R.id.txt_feedTime);
            btn_feed_cancle = itemView.findViewById(R.id.btn_feed_cancle);
        }
    }

    public void removeAt(int position) {
        listViewItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listViewItemList.size());
    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }
}