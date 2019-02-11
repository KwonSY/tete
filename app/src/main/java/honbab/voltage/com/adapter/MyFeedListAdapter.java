package honbab.voltage.com.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.CancleFeedTask;
import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class MyFeedListAdapter extends RecyclerView.Adapter<MyFeedListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    public ArrayList<FeedData> listViewItemList = new ArrayList<FeedData>();

    public MyFeedListAdapter() {

    }

    public MyFeedListAdapter(Context mContext, ArrayList<FeedData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_feed, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FeedData data = listViewItemList.get(position);

        holder.careView_rest.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra("feeder_id", data.getUser_id());
                intent.putExtra("feeder_name", data.getUser_name());
                intent.putExtra("feeder_img", data.getUser_img());
                intent.putExtra("status", data.getStatus());
                mContext.startActivity(intent);
            }
        });
        Picasso.get().load(data.getUser_img())
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(holder.img_user);
        holder.img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("user_id", data.getUser_id());
                mContext.startActivity(intent);
            }
        });

        Picasso.get().load(data.getRest_img())
                .resize(400,400)
                .centerCrop()
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(holder.img_rest);

        String str_gender = null;
        if (data.getUser_gender().equals("m"))
            str_gender = "남";
        if (data.getUser_gender().equals("f"))
            str_gender = "여";

        holder.txt_userName.setText(data.getUser_name());
        holder.txt_userInfo.setText(data.getUser_age() + " / " + str_gender);
        holder.txt_restName.setText(data.getRest_name());
        holder.txt_restAddress.setText(data.getVicinity());

        String[] time1 = data.getFeed_time().split(" ");
        String date[] = time1[0].split("-");
        String time[] = time1[1].split(":");
//        if (date[1].substring(0,1).equals("0"))
//            date[1] = date[1].substring(1,2);

        holder.txt_date.setText(date[1]+ "\n" + date[2]);
        holder.txt_time.setText(time[0] + ":" + time[1] + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("compound_code", data.getCompound_code());
                intent.putExtra("feed_id", data.getFeed_id());
                intent.putExtra("rest_name", data.getRest_name());
                intent.putExtra("rest_phone", data.getRest_phone());
                intent.putExtra("feed_time", data.getFeed_time());
                intent.putExtra("place_id", data.getPlace_id());
                intent.putExtra("latLng", data.getLatLng());
                intent.putExtra("feeder_id", data.getUser_id());
                intent.putExtra("feeder_img", Statics.main_url + data.getUser_img());
                intent.putExtra("feeder_name", data.getUser_name());
                intent.putExtra("status", data.getStatus());
                intent.putExtra("vicinity", data.getVicinity());
                mContext.startActivity(intent);
            }
        });
        holder.btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.ask_cancle_godmuk);
                builder.setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                new CancleFeedTask(mContext, position)
                                        .execute(data.getFeed_id(), listViewItemList.get(position).getRest_name());
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
        holder.btn_go_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestData restData = new RestData();

                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("fromId", Statics.my_id);
                intent.putExtra("toId", data.getUser_id());
                intent.putExtra("toUserName", data.getUser_name());
                intent.putExtra("toUserImg", data.getUser_img());
                intent.putExtra("toToken", data.getToken());
                intent.putExtra("restData", restData);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(view.getContext(), OneRestaurantActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        adapterView.startActivity();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView careView_rest;
        ImageView img_user, img_rest, btn_cancle;
        TextView txt_userName, txt_userInfo;
        TextView txt_restName, txt_restAddress;
        TextView txt_date, txt_time;
        Button btn_go_chat;

        public ViewHolder(View itemView) {
            super(itemView);

            careView_rest = itemView.findViewById(R.id.careView_rest);
            img_user = itemView.findViewById(R.id.img_user);
            img_rest = itemView.findViewById(R.id.img_rest);
            btn_cancle = itemView.findViewById(R.id.btn_cancle);

            txt_userName = itemView.findViewById(R.id.txt_userName);
            txt_userInfo = itemView.findViewById(R.id.txt_userInfo);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_restAddress = itemView.findViewById(R.id.txt_restAddress);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            btn_go_chat = itemView.findViewById(R.id.btn_go_chat);
        }
    }

//    public void addItem(String feed_id,
//                        String user_id, String user_name, String user_age, String user_gender, String img_url, String token,
//                        String rest_id, String rest_name, String compound_code, String vicinity, String place_id, LatLng latLng, String rest_phone, String rest_img,
//                        String status, String time, ArrayList<UserData> usersList) {
//
//        FeedData item = new FeedData(feed_id,
//                user_id, user_name, user_age, user_gender, img_url, token,
//                rest_id, rest_name,
//                compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
//                status, time, usersList);
//
//        listViewItemList.add(item);
//    }

    public void removeAt(int position) {
        listViewItemList.remove(position);
        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listViewItemList.size());
    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<FeedData> list) {
        listViewItemList.addAll(list);
        notifyDataSetChanged();
    }
}