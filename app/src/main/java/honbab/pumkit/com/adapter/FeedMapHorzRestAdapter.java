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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.FeedMapActivity;
import honbab.pumkit.com.tete.MapsActivity;
import honbab.pumkit.com.tete.OneRestaurantActivity;
import honbab.pumkit.com.tete.R;
import honbab.pumkit.com.tete.Statics;

public class FeedMapHorzRestAdapter extends RecyclerView.Adapter<FeedMapHorzRestAdapter.ViewHolder> {

//    private ArrayList<String> mNames = new ArrayList<>();
//    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<MapData> mMapRestList = new ArrayList<>();
    private Context mContext;

    public FeedMapHorzRestAdapter(Context context, ArrayList<MapData> mMapRestList) {
        this.mContext = context;
        this.mMapRestList = mMapRestList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_horizontal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final MapData data = mMapRestList.get(position);

        String rest_img_url = mMapRestList.get(position).getRest_img();

        if (rest_img_url.isEmpty()) {
            holder.image.setImageResource(R.drawable.icon_no_image);
        } else {
            Picasso.get().load(mMapRestList.get(position).getRest_img())
                    .resize(100, 100)
                    .centerCrop()
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(holder.image);
        }
        holder.rest_name.setText(mMapRestList.get(position).getRest_name());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("abc", "holder.image feed_id = " + data.getSid());

                Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("feed_id", data.getSid());//vvvvvvvvvvvvvv check 필요
                intent.putExtra("feed_rest_name", data.getRest_name());
                intent.putExtra("place_id", data.getPlace_id());
                intent.putExtra("latLng", data.getLatLng());
                intent.putExtra("feeder_id", data.getUser_id());
                intent.putExtra("feeder_name", data.getUser_name());
                intent.putExtra("feeder_img", Statics.main_url + data.getUser_img());
                intent.putExtra("status", data.getStatus());
                mContext.startActivity(intent);
            }
        });
        holder.layout_card_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext.getClass().equals(MapsActivity.class)) {
                    ((MapsActivity) mContext).txt_restName.setText(mMapRestList.get(position).getRest_name());
                    ((MapsActivity) mContext).layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                } else if (mContext.getClass().equals(FeedMapActivity.class)) {
//                    ((FeedMapActivity) mContext).txt_restName.setText(mMapRestList.get(position).getRest_name());
//                    ((FeedMapActivity) mContext).layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMapRestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout_card_rl;
        ImageView image;
        TextView rest_name;
        TextView rest_location;

        public ViewHolder(View itemView) {
            super(itemView);

            layout_card_rl = itemView.findViewById(R.id.layout_card_rl);
            image = itemView.findViewById(R.id.img_food);
            rest_name = itemView.findViewById(R.id.txt_restName);
            rest_location = itemView.findViewById(R.id.txt_restLocation);
        }
    }
}