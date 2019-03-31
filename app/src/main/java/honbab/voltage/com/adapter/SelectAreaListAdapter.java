package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import honbab.voltage.com.data.AreaData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.PickAreaTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class SelectAreaListAdapter extends RecyclerView.Adapter<SelectAreaListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    //    int TYPE_TIME = 1;
//    int TYPE_END = 2;
    int i_touch = 0;

    private Fragment fragment;

    public ArrayList<AreaData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public SelectAreaListAdapter() {
//        this.mContext = mContext;
    }

    public SelectAreaListAdapter(Context mContext, ArrayList<AreaData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_area, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (getItemViewType(position) == TYPE_TIME) {

        final AreaData data = listViewItemList.get(position);
        data.setPosition(position);

        holder.bindToPost(data, getItemViewType(position));
//        } else {
//            //TYPE_END
//            holder.bindToPost(null, getItemViewType(position));
//        }
    }

//    @Override
//    public int getItemViewType(int position) {
////        return listViewItemList.size() == position ? TYPE_END : TYPE_TIME;
//        return position;
//    }

    @Override
    public int getItemCount() {
        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SelectAreaListAdapter mAdapter;

        public RelativeLayout layout_card;
        public TextView txt_area;
        public CheckBox checkBox;


        public ViewHolder(View itemView, int viewType, final SelectAreaListAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            checkBox = itemView.findViewById(R.id.checkBox);
            txt_area = itemView.findViewById(R.id.txt_area);
            layout_card.setOnClickListener(this);
            checkBox.setOnClickListener(this);
        }

        public void bindToPost(final AreaData data, int viewType) {

            txt_area.setText(data.getArea_name());

            try {
                setDateToView(data, data.getPosition());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setDateToView(AreaData data, int position) throws Exception {
//            checkBox.setChecked(position == mSelectedItem);

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    ((SelectFeedFragment) fragment).area_cd = data.getArea_cd();
                    if (((SelectFeedFragment) fragment).feed_time.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText(R.string.select_feed_time);
                    } else if (((SelectFeedFragment) fragment).feed_rest_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText(R.string.select_feed_rest);
                    } else if (((SelectFeedFragment) fragment).to_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    } else {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    }

                    data.setChecked(true);

//                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                            ((SelectFeedFragment) fragment).area_cd,
//                            ((SelectFeedFragment) fragment).feed_rest_id, "readOnlyUser");
                    new PickAreaTask(mContext).execute();
                } else {
                    data.setChecked(false);

                }
            } else {//선택이 없으나, 사전에 체크 표기

//                if (listViewItemList.size() == 1) {
//                    data.setChecked(true);
//                    ((SelectFeedFragment) fragment).area_cd = data.getArea_cd();
//
//                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
//                            ((SelectFeedFragment) fragment).area_cd,
//                            ((SelectFeedFragment) fragment).feed_rest_id,
//                            "readBelowRest");
//                    Log.e("abc", "Select date feed_time = " + ((SelectFeedFragment) fragment).feed_time);
//                }
            }

            checkBox.setChecked(data.isChecked());
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.checkBox:
                    mSelectedItem = getAdapterPosition();
                    notifyItemRangeChanged(0, listViewItemList.size());
                    mAdapter.onItemHolderClick(SelectAreaListAdapter.ViewHolder.this);

                    if (i_touch == 0) {
                        Toast.makeText(mContext, "길게 누르면,\n음식점 상세를 볼 수 있습니다.", Toast.LENGTH_SHORT).show();
                        i_touch++;
                    }

                    break;
            }
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

    public void onItemHolderClick(SelectAreaListAdapter.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}