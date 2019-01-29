package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.RestData;
import honbab.voltage.com.task.RestLikeTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.OneRestaurantActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectRestListAdapter extends RecyclerView.Adapter<SelectRestListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    private ArrayList<RestData> listViewItemList = new ArrayList<>();
//    private String pack;

    public SelectRestListAdapter() {

    }

    public SelectRestListAdapter(Context mContext, ArrayList<RestData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
//        this.pack = pack;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_rest, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final RestData data = listViewItemList.get(position);

        holder.bindToPost(data);
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public String like_yn;

        public RelativeLayout layout_like_yn, layout_check;
        public ImageView img_rest;
        public TextView txt_restName;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            layout_like_yn = itemView.findViewById(R.id.layout_like_yn);
            img_rest = itemView.findViewById(R.id.img_rest);
            txt_restName = itemView.findViewById(R.id.txt_restName);

            layout_check = itemView.findViewById(R.id.layout_check);
        }

        public void bindToPost(final RestData data) {
            if (data.getLike_yn().equals("y")) {
                like_yn = "y";
                layout_like_yn.setVisibility(View.VISIBLE);
            } else {
                like_yn = "n";
                layout_like_yn.setVisibility(View.GONE);
            }

            Picasso.get().load(data.getRest_img())
                    .placeholder(R.drawable.icon_no_image)
                    .error(R.drawable.icon_no_image)
                    .into(img_rest);
            img_rest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (like_yn.equals("y")) {
                        like_yn = "n";
                        layout_like_yn.setVisibility(View.GONE);
                    } else {
                        like_yn = "y";
                        layout_like_yn.setVisibility(View.VISIBLE);
                    }

                    Log.e("abc", "SelectTask like_yn = " + like_yn);

                    new RestLikeTask(mContext).execute(data.getRest_id(), like_yn);

                    Toast.makeText(mContext, "음식점 상세를 보시려면 음식점을 길게 누르세요.", Toast.LENGTH_SHORT).show();
                }
            });
            img_rest.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(mContext, OneRestaurantActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("feed_id", data.getFeed_id());
                    intent.putExtra("rest_name", data.getRest_name());
                    intent.putExtra("compound_code", data.getCompound_code());
                    intent.putExtra("rest_phone", data.getRest_phone());
//                    intent.putExtra("feed_time", data.getFeed_time());
                    intent.putExtra("place_id", data.getPlace_id());
                    intent.putExtra("vicinity", data.getVicinity());
                    intent.putExtra("latLng", data.getLatLng());

//                    intent.putExtra("feeder_id", data.getUser_id());
//                    intent.putExtra("feeder_name", data.getUser_name());
//                    intent.putExtra("feeder_img", Statics.main_url + data.getUser_img());
//                    intent.putExtra("status", data.getStatus());
                    mContext.startActivity(intent);

                    return false;
                }
            });
            txt_restName.setText(data.getRest_name());
        }
    }

//    public void removeAt(int position) {
//        listViewItemList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listViewItemList.size());
//    }

    public void clearItemList() {
        listViewItemList.clear();
        notifyDataSetChanged();
    }
}