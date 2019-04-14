package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class BabFriendsAdapter extends RecyclerView.Adapter<BabFriendsAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    public ArrayList<UserData> listViewItemList = new ArrayList<>();

    public BabFriendsAdapter() {

    }

    public BabFriendsAdapter(Context mContext, ArrayList<UserData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();;
        this.listViewItemList = listViewItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_babfriends, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UserData UserData = listViewItemList.get(position);

        holder.bindToPost(UserData);
    }

//    @Override
//    public int getItemViewType(int position) {
//
//        return TYPE_STATUS_N;
//    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_user;
        public TextView txt_userName, txt_status;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            img_user = (ImageView) itemView.findViewById(R.id.img_user);
            txt_userName = (TextView) itemView.findViewById(R.id.txt_userName);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);

//            Context context = itemView.getContext();
//            //수락대기 리스트
//            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//            recyclerView_onedate = itemView.findViewById(R.id.recyclerView_onedate);
//            recyclerView_onedate.setLayoutManager(layoutManager);
        }

        public void bindToPost(final UserData data) {
            Picasso.get().load(data.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            txt_userName.setText(data.getUser_name());
            txt_status.setText(data.getStatus_name());

            img_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user_id", data.getUser_id());
                    mContext.startActivity(intent);
                }
            });
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