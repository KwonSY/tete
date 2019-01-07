package honbab.voltage.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.FeedData;
import honbab.voltage.com.data.RestData;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.ReservActivity;

public class GridViewNearByAdapter extends RecyclerView.Adapter<GridViewNearByAdapter.ViewHolder> {

    private ArrayList<FeedData> listViewitemList = new ArrayList<>();
    private Context mContext;

    public GridViewNearByAdapter(Context context, ArrayList<FeedData> listViewitemList) {
        this.mContext = context;
        this.listViewitemList = listViewitemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_reserv, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final FeedData data = listViewitemList.get(position);

        if (data.getRest_img().isEmpty()) {
            holder.image.setImageResource(R.drawable.icon_no_image);
        } else {
            Picasso.get().load(data.getRest_img())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(holder.image);
        }
        holder.rest_name.setText(data.getRest_name());
        //vvvvvvvvvvvvvvvv 확인 필요 rest_location
        holder.rest_location.setText(data.getVicinity());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("rest_name", data.getRest_name());
                intent.putExtra("compound_code", data.getCompound_code());
                intent.putExtra("latLng", data.getLatLng());
                intent.putExtra("place_id", data.getPlace_id());
                intent.putExtra("rest_phone", data.getRest_phone());
                intent.putExtra("vicinity", data.getVicinity());
                mContext.startActivity(intent);
            }
        });
        holder.btn_one_reserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReservActivity) mContext).txt_restName.setText(data.getRest_name());

//                ((ReservActivity) mContext).place_id = data.getPlace_id();
//                ((ReservActivity) mContext).rest_name = data.getRest_name();
//                ((ReservActivity) mContext).rest_phone = data.getRest_phone();
//                ((ReservActivity) mContext).rest_img = data.getRest_img();
//                LatLng latLng = data.getLatLng();
//                Double d_lat = latLng.latitude;
//                Double d_lng = latLng.longitude;
//                String str_lat = d_lat.toString();
//                String str_lng = d_lng.toString();
//                ((ReservActivity) mContext).lat = str_lat;
//                ((ReservActivity) mContext).lng = str_lng;
//                ((ReservActivity) mContext).compound_code = data.getCompound_code();
//                ((ReservActivity) mContext).vicinity = data.getVicinity();
                ((ReservActivity) mContext).restData = new RestData(
                        data.getRest_id(),
                        data.getRest_name(),
                        data.getCompound_code(),
                        data.getLatLng(),
                        data.getPlace_id(),
                        data.getRest_img(),
                        data.getRest_phone(),
                        data.getVicinity());

                ((ReservActivity) mContext).layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
        holder.btn_one_reserv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holder.btn_one_reserv.setBackgroundResource(R.drawable.icon_check_y);

                        break;
                    case MotionEvent.ACTION_UP:
                        holder.btn_one_reserv.setBackgroundResource(R.drawable.icon_check_y);

                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listViewitemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public ImageView btn_one_reserv;
        TextView rest_name;
        TextView rest_location;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_food);
            btn_one_reserv = itemView.findViewById(R.id.btn_one_reserv);
            btn_one_reserv.setBackgroundResource(R.drawable.icon_check_n);
            rest_name = itemView.findViewById(R.id.txt_restName);
            rest_location = itemView.findViewById(R.id.txt_restLocation);
        }

//        public static void setBtnClear(View itemView) {
//            ImageView btn_one_reserv = itemView.findViewById(R.id.btn_one_reserv);
//            btn_one_reserv.setBackgroundResource(R.drawable.icon_check_n);
//        }
    }
}