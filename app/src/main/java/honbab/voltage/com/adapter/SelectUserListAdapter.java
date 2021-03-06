package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import honbab.voltage.com.data.UserData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.tete.LoginActivity;
import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.ProfileActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.widget.CircleTransform;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectUserListAdapter extends RecyclerView.Adapter<SelectUserListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    private Fragment fragment;
    public ArrayList<UserData> listViewItemList = new ArrayList<>();

    public SelectUserListAdapter() {

    }

    public SelectUserListAdapter(Context mContext, ArrayList<UserData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
        fragment = ((MainActivity2) mContext).getSupportFragmentManager().findFragmentByTag("Match");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_user, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UserData data = listViewItemList.get(position);

        holder.bindToPost(data);
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img_user;
        public TextView txt_userName;
        public TextView icon_me, txt_comment;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            img_user = itemView.findViewById(R.id.img_user);
            txt_userName = itemView.findViewById(R.id.txt_userName);
            icon_me = itemView.findViewById(R.id.icon_me);
            txt_comment = itemView.findViewById(R.id.txt_comment);
        }

        public void bindToPost(final UserData data) {
            Picasso.get().load(data.getImg_url())
                    .placeholder(R.drawable.icon_noprofile_circle)
                    .error(R.drawable.icon_noprofile_circle)
                    .transform(new CircleTransform())
                    .into(img_user);
            String str_gender = ", ";
            if (data.getGender().equals("m"))
                str_gender += "남";
            else
                str_gender += "여";
            txt_userName.setText(data.getUser_name() + ", " + data.getAge() + str_gender);

            if (Statics.my_id.equals(data.getUser_id())) {
                icon_me.setVisibility(View.VISIBLE);
            } else {
                icon_me.setVisibility(View.GONE);
                img_user.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SelectFeedFragment) fragment).to_id = data.getUser_id();
                        ((SelectFeedFragment) fragment).to_name = data.getUser_name();
                        ((SelectFeedFragment) fragment).layout_slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        ((SelectFeedFragment) fragment).txt_explain_reserv.setText(data.getUser_name() + "님을 선택하셨습니다.");
                    }
                });
                img_user.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (Integer.parseInt(Statics.my_id) < 1) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("user_id", data.getUser_id());
                            mContext.startActivity(intent);
                        }

                        return false;
                    }
                });
            }

            txt_comment.setText(data.getComment());
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

//    public boolean activate;
//    public void activateButtons(boolean activate) {
//        this.activate = activate;
//        notifyDataSetChanged();
//    }
}