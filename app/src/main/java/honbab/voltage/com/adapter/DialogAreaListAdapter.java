package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.task.OneFeedRestLikeListTask;
import honbab.voltage.com.tete.PickRestLikeActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DialogAreaListAdapter extends RecyclerView.Adapter<DialogAreaListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    int TYPE_AREA = 0;
//    int TYPE_TIME = 1;
//    int TYPE_END = 2;

//    private Fragment fragment;

    public ArrayList<AreaData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public DialogAreaListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public DialogAreaListAdapter(Context mContext, ArrayList<AreaData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;
//        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_dialog_pick_area, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (getItemViewType(position) == TYPE_TIME) {
        final AreaData data = listViewItemList.get(position);
        data.setPosition(position);

        holder.bindToPost(data, getItemViewType(position));
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_AREA;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private DialogAreaListAdapter mAdapter;

        public RelativeLayout layout_card;
        public TextView txt_area, txt_cnt;
        public CheckBox checkBox;

        public ViewHolder(View itemView, int viewType, final DialogAreaListAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            txt_area = itemView.findViewById(R.id.txt_area);
            txt_cnt = itemView.findViewById(R.id.txt_cnt);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bindToPost(final AreaData data, int viewType) {
            if (data.getArea_name().length() > 5)
                txt_area.setTextSize(10);
            txt_area.setText(data.getArea_name());
            txt_cnt.setText(data.getCnt() + "명");
//            layout_card.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((PickRestLikeActivity) mContext).area_cd = data.getArea_cd();
//
//
//                    new OneFeedRestLikeListTask(mContext).execute(((PickRestLikeActivity) mContext).area_cd);
//                }
//            });
            checkBox.setOnClickListener(this);

            try {
                setDateToView(data, data.getPosition());
            } catch (Exception e) {
                e.printStackTrace();
            }

//            checkBox.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Log.e("abc", "sssss feed_id = " + ((SelectFeedFragment) fragment).feed_id);
////                    new PickAreaTask(mContext).execute(((SelectFeedFragment) fragment).feed_id, data.getArea_cd());
//
//                    try {
//                        mSelectedItem = getAdapterPosition();
//                        notifyItemRangeChanged(0, listViewItemList.size());
//                        mAdapter.onItemClickListener(ViewHolder.this);
//
//                        new OneFeedRestLikeListTask(mContext).execute(((PickRestLikeActivity) mContext).area_cd);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }

        public void setDateToView(AreaData data, int position) throws Exception {

            checkBox.setChecked(position == mSelectedItem);

//            ((PickRestLikeActivity) mContext).area_cd = data.getArea_cd();

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    ((PickRestLikeActivity) mContext).area_cd = data.getArea_cd();
                    ((PickRestLikeActivity) mContext).mAdapter_rest.clearItemList();

                    ((PickRestLikeActivity) mContext).progressBar_rest.setVisibility(View.VISIBLE);

                    new OneFeedRestLikeListTask(mContext).execute(((PickRestLikeActivity) mContext).timelike_id, ((PickRestLikeActivity) mContext).area_cd);
//
//                    data.setChecked(true);
////                checkBox.setChecked(data.isChecked());
//
//
//                } else {
//                    data.setChecked(false);
////                checkBox.setChecked(data.isChecked());
                }
//
//            } else {//선택이 없으나, 사전에 체크 표기
////                Log.e("abc", "feed_time = " + ((SelectFeedFragment) fragment).feed_time + ", feed_rest_id = "+((SelectFeedFragment) fragment).feed_rest_id);
////                if (data.isChecked()) {
//////                    ((SelectFeedFragment) fragment).feed_rest_id = data.getRest_id();
////
////                    new OneFeedRestLikeListTask(mContext).execute(((PickRestLikeActivity) mContext).area_cd);
////                }
            }

//            checkBox.setChecked(data.isChecked());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkBox:
                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, listViewItemList.size());
                    mAdapter.onItemHolderClick(DialogAreaListAdapter.ViewHolder.this);

                    break;
            }
        }
    }


//    public void addItem(AreaData data) {
////        ChatData item = new ChatData();
////        ChatData item = new ChatData(type, fromId, toId, toUserName, message, imageUrl, imageWidth, imageHeight, toUserImg);
////        item.setFromId(fromId);
//
//        listViewItemList.add(data);
//    }

//    public void removeAt(int position) {
//        listViewItemList.remove(position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, listViewItemList.size());
//    }

//    public void clearItemList() {
//        listViewItemList.clear();
//        notifyDataSetChanged();
//    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void onItemHolderClick(DialogAreaListAdapter.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}