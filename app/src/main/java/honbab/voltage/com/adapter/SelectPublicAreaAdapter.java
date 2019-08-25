package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.data.UserData;
import honbab.voltage.com.task.AccountTask;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.SelectPublicTimeActivity;

public class SelectPublicAreaAdapter extends RecyclerView.Adapter<SelectPublicAreaAdapter.ViewHolder> {
    private Context mContext;
//    private OkHttpClient httpClient;

    int TYPE_TIME = 1;
//    int TYPE_END = 2;

//    private Fragment fragment;

    public ArrayList<AreaData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public SelectPublicAreaAdapter() {

    }

    public SelectPublicAreaAdapter(Context mContext, ArrayList<AreaData> listViewItemList) {
        this.mContext = mContext;
//        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_public_area, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TIME) {

            final AreaData data = listViewItemList.get(position);
            data.setPosition(position);

            holder.bindToPost(data, getItemViewType(position));
        } else {
            //TYPE_END
//            holder.bindToPost(null, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
//        return listViewItemList.size() == position ? TYPE_END : TYPE_TIME;
        return TYPE_TIME;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SelectPublicAreaAdapter mAdapter;

        public RelativeLayout layout_card;
        public CheckBox checkBox;
        public ImageView img_area;
        public TextView txt_area;

        public ViewHolder(View itemView, int viewType, final SelectPublicAreaAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            img_area = itemView.findViewById(R.id.img_area);
            txt_area = itemView.findViewById(R.id.txt_area);
            checkBox = itemView.findViewById(R.id.checkBox);
//            layout_card.setOnClickListener(this);
//            checkBox.setOnClickListener(this);
            if (viewType == TYPE_TIME) {

            }
        }

        public void bindToPost(final AreaData data, int viewType) {
            if (viewType == TYPE_TIME) {
                Picasso.get().load(data.getArea_image())
                        .placeholder(R.drawable.icon_no_image)
                        .error(R.drawable.icon_no_image)
                        .into(img_area);

                txt_area.setText(data.getArea_name());

                try {
                    setDateToView(data, data.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SelectPublicTimeActivity) mContext).chatRoomCd = data.getArea_cd();

                        mSelectedItem = getAdapterPosition();
                        notifyItemRangeChanged(0, listViewItemList.size());
                        mAdapter.onItemHolderClick(ViewHolder.this);

                        ((SelectPublicTimeActivity) mContext).groupchatUsersList.clear();
                        FirebaseDatabase.getInstance().getReference().child("groupchats").child(data.getArea_cd()).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        UserData userData = new AccountTask(mContext).execute(userSnapshot.getKey()).get();
                                        Log.e("abc", "userData img = " + userData.getImg_url());

                                        ((SelectPublicTimeActivity) mContext).groupchatUsersList.add(userData);
                                    }
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

//                        Map<String, Object> userObj;
//                        Map<String, Object> userObj2 = dataSnapshot.getValue(userObj);
//                        userObj.g
//                        Log.e("abc", "dataSnapshot.getValue() = " + dataSnapshot.getValue());
//                        Map<String, Object> userObj = mDatabase.child("groupchats").child(chatRoomCd).child("users").child(dataSnapshot.getKey()).;
//                        Log.e("abc", "dataSnapshot.getKey() = " + dataSnapshot.getKey());

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            } else {
                //TYPE_END
//                layout_card.setBackgroundColor(Color.parseColor("#efefef"));
//                txt_time.setText("시간선택");
//
//                checkBox.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (Integer.parseInt(Statics.my_id) < 1) {
//                            Intent intent = new Intent(mContext, LoginActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            mContext.startActivity(intent);
//                        } else {
//                            PickDateDialog pickDateDialog = new PickDateDialog(mContext);
//                            pickDateDialog.callFunction(((SelectFeedFragment) fragment).dateAllList);
//                        }
//                    }
//                });
            }
        }

        public void setDateToView(AreaData data, int position) throws Exception {
//            checkBox.setChecked(position == mSelectedItem);
            Log.e("abc", position + ", mSelectedItem = " + mSelectedItem);

            if (mSelectedItem >= 0) {
                //선택이 되었으면
                if (position == mSelectedItem) {
                    data.setChecked(true);
                } else {
                    data.setChecked(false);
                }
            } else {
                //선택이 없으나, 사전에 체크 표기
                if (listViewItemList.size() == 1) {
                    data.setChecked(true);
                } else {
//                    if (listViewItemList.size() == position + 1) {
                    if (position ==  0) {
                        data.setChecked(true);
                    }
                }

            }

            checkBox.setChecked(data.isChecked());
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

//    public void activateButtons(boolean activate) {
//        this.activate = activate;
//
//        notifyDataSetChanged();
//    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(SelectPublicAreaAdapter.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}