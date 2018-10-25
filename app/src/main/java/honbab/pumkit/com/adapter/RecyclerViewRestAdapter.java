package honbab.pumkit.com.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.pumkit.com.data.MapData;
import honbab.pumkit.com.tete.R;

public class RecyclerViewRestAdapter extends RecyclerView.Adapter<RecyclerViewRestAdapter.ViewHolder> {

//    private ArrayList<String> mNames = new ArrayList<>();
//    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<MapData> mMapRestList = new ArrayList<>();
    private Context mContext;

    public RecyclerViewRestAdapter(Context context, ArrayList<MapData> mMapRestList) {
        this.mContext = context;
        this.mMapRestList = mMapRestList;
    }

//    public RecyclerViewRestAdapter(Context context, ArrayList<String> mNames, ArrayList<String> mImages) {
//        this.mContext = context;
//        this.mNames = mNames;
//        this.mImages = mImages;
//    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_horizontal, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

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
                Log.e("abc", "선택되었음");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMapRestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView rest_name;
        TextView rest_location;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.img_food);
            rest_name = itemView.findViewById(R.id.txt_restName);
            rest_location = itemView.findViewById(R.id.txt_restLocation);
        }
    }
}