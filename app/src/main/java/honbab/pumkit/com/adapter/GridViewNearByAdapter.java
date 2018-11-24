package honbab.pumkit.com.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.ReservActivity;

public class GridViewNearByAdapter extends RecyclerView.Adapter<GridViewNearByAdapter.ViewHolder> {

    private ArrayList<MapData> listViewitemList = new ArrayList<>();
    private Context mContext;

    public GridViewNearByAdapter(Context context, ArrayList<MapData> listViewitemList) {
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
        String rest_img_url = listViewitemList.get(position).getRest_img();

        if (rest_img_url.isEmpty()) {
            holder.image.setImageResource(R.drawable.icon_no_image);
        } else {
            Picasso.get().load(listViewitemList.get(position).getRest_img())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(holder.image);
        }
        holder.rest_name.setText(listViewitemList.get(position).getRest_name());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("place_id", listViewitemList.get(position).getPlace_id());
                mContext.startActivity(intent);
            }
        });
        holder.btn_one_reserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "체크 선택되었음");
//                holder.btn_one_reserv.setBackgroundResource(R.drawable.icon_check_y);

                ((ReservActivity) mContext).txt_restName.setText(listViewitemList.get(position).getRest_name());

                ReservActivity.rest_name = listViewitemList.get(position).getRest_name();
                ReservActivity.place_id = listViewitemList.get(position).getPlace_id();
                ReservActivity.rest_img = listViewitemList.get(position).getRest_img();
                LatLng latLng = listViewitemList.get(position).getLatLng();
                Double d_lat = latLng.latitude;
                Double d_lng = latLng.longitude;
                String str_lat = d_lat.toString();
                String str_lng = d_lng.toString();
                ReservActivity.lat = str_lat;
                ReservActivity.lng = str_lng;

                ((ReservActivity) mContext).layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
        holder.btn_one_reserv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.e("abc", "motion = " + motionEvent.getAction());
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("abc", "터치 다운");
//                        holder.btn_one_reserv.setImageAlpha(70);
//                        holder.btn_one_reserv.setBackground(mContext.getResources().getDrawable(R.drawable.icon_check_y));
                        holder.btn_one_reserv.setBackgroundResource(R.drawable.icon_check_y);

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("abc", "터치 업");
//                        holder.btn_one_reserv.setImageAlpha(255);
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