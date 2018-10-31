package honbab.pumkit.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.ReservData;
import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;
import honbab.pumkit.com.widget.CircleTransform;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.ViewHolder> {

    Context context;
    public ArrayList<ReservData> listViewItemList = new ArrayList<ReservData>();

    public FeedListAdapter(Context context) {
        this.context = context;
    }

    public FeedListAdapter(Context context, ArrayList<ReservData> listViewItemList) {
        this.context = context;
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
        final ReservData data = listViewItemList.get(position);

        Picasso.get().load(Statics.main_url + data.getUser_img())
                .resize(200,200)
                .centerCrop()
                .placeholder(R.drawable.icon_noprofile_circle)
                .error(R.drawable.icon_noprofile_circle)
                .transform(new CircleTransform())
                .into(holder.img_user);

        Picasso.get().load(data.getRest_img())
                .resize(400,400)
                .centerCrop()
                .placeholder(R.drawable.icon_no_image)
                .error(R.drawable.icon_no_image)
                .into(holder.img_rest);

        holder.txt_userName.setText(data.getUser_name() + data.getUser_age() + data.getUser_gender());
        holder.txt_restName.setText("#" + data.getLocation());
        holder.txt_time.setText(data.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("feed_id", data.getSid());
                intent.putExtra("place_id", data.getPlace_id());
                intent.putExtra("feeder_img", Statics.main_url + data.getUser_img());
                intent.putExtra("feeder_name", data.getUser_name());
//                intent.putExtra("feedee_status", data.getFeedee_status());
                intent.putExtra("status", data.getStatus());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(view.getContext(), OneFeedActivity.class);
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

    public void addItem(String sid, String user_id, String user_name, String img_url, String user_age, String user_gender,
                        String rest_name, String location, String place_id, LatLng latLng, String rest_img,
                        String status, String time) {

        ReservData item = new ReservData(sid, user_id, user_name, img_url, user_age, user_gender,
                rest_name, location, place_id, latLng, rest_img,
                status, time);

        listViewItemList.add(item);
    }

    public void clearItemList() {
        listViewItemList.clear();
    }
}