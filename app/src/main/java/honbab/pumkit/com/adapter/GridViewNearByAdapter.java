package honbab.pumkit.com.adapter;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
                Log.e("abc", "선택되었음");
                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("place_id", listViewitemList.get(position).getPlace_id());
                mContext.startActivity(intent);
            }
        });
        holder.btn_one_reserv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ReservActivity.txt_restName.setText(listViewitemList.get(position).getRest_name());
                ((ReservActivity) mContext).txt_restName.setText(listViewitemList.get(position).getRest_name());

                ReservActivity.rest_name = listViewitemList.get(position).getRest_name();
                ReservActivity.place_id = listViewitemList.get(position).getPlace_id();
                Log.e("abc", "pppppppplace id = " + ReservActivity.place_id);
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
    }

    @Override
    public int getItemCount() {
        return listViewitemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView btn_one_reserv;
        TextView rest_name;
        TextView rest_location;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_food);
            btn_one_reserv = itemView.findViewById(R.id.btn_one_reserv);
            rest_name = itemView.findViewById(R.id.txt_restName);
            rest_location = itemView.findViewById(R.id.txt_restLocation);
        }
    }
}