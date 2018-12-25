package honbab.voltage.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {
    Context mContext;
    public ArrayList<FeedData> listViewItemList = new ArrayList<FeedData>();

    public FeedListAdapter () {

    }

    public FeedListAdapter(Context mContext, ArrayList<FeedData> listViewItemList) {
        this.mContext = mContext;
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
        Log.e("abc", "FeedListAdapter feed_id = " + data.getFeed_id());

        Picasso.get().load(Statics.main_url + data.getUser_img())
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

        Log.e("abc", "FeedListAdapter data.getRest_name() = " + data.getRest_name());
        holder.txt_userName.setText(data.getUser_name() + " " + data.getUser_age() + " " + str_gender);
        holder.txt_restName.setText(data.getRest_name() + data.getVicinity());

        String[] time1 = data.getTime().split(" ");
        String date[] = time1[0].split("-");
        String time[] = time1[1].split(":");
        holder.txt_time.setText(date[1] + "/" + date[2] + " " + time[0] + "시 " + time[1] + "분");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("compound_code", data.getCompound_code());
                intent.putExtra("feed_id", data.getFeed_id());
                intent.putExtra("rest_name", data.getRest_name());
                intent.putExtra("rest_phone", data.getRest_phone());
                intent.putExtra("feed_time", data.getTime());
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
        ImageView img_user;
        ImageView img_rest;
        TextView txt_userName;
        TextView txt_restName;
        TextView txt_time;

        public ViewHolder(View itemView) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            img_rest = itemView.findViewById(R.id.img_rest);

            txt_userName = itemView.findViewById(R.id.txt_userName);
            txt_restName = itemView.findViewById(R.id.txt_restName);
            txt_time = itemView.findViewById(R.id.txt_time);
        }
    }

    public void addItem(int feed_id,
                        String user_id, String user_name, String user_age, String user_gender, String img_url,
                        int rest_id, String rest_name, String compound_code, String vicinity, String place_id, LatLng latLng, String rest_phone, String rest_img,
                        String status, String time) {

        FeedData item = new FeedData(feed_id,
                user_id, user_name, user_age, user_gender, img_url,
                rest_id, rest_name,
                compound_code, latLng, place_id, rest_img, rest_phone, vicinity,
                status, time);

        listViewItemList.add(item);
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