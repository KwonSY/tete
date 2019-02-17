package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.fragment.SelectFeedFragment;
import honbab.voltage.com.task.SelectFeedListTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import honbab.voltage.com.widget.PickDateDialog;
import okhttp3.OkHttpClient;

public class SelectDateListAdapter extends RecyclerView.Adapter<SelectDateListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

    int TYPE_TIME = 1;
    int TYPE_END = 2;

    private Fragment fragment;

    public ArrayList<SelectDateData> listViewItemList = new ArrayList<>();
    private int mSelectedItem = -1;
    private AdapterView.OnItemClickListener onItemClickListener;

    public SelectDateListAdapter() {

    }

    public SelectDateListAdapter(Context mContext, ArrayList<SelectDateData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_select_date, parent, false);

        return new ViewHolder(view, viewType, this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_TIME) {

            final SelectDateData data = listViewItemList.get(position);
            Log.e("abc", position+" : " + data.getTime());
            data.setPosition(position);

            holder.bindToPost(data, getItemViewType(position));
        } else {
            //TYPE_END
            holder.bindToPost(null, getItemViewType(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return listViewItemList.size() == position ? TYPE_END : TYPE_TIME;
    }

    @Override
    public int getItemCount() {
        return listViewItemList.size() + 1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SelectDateListAdapter mAdapter;

        public RelativeLayout layout_card;
        public CheckBox checkBox;
        public TextView txt_date, txt_time, cnt_users;

        public ViewHolder(View itemView, int viewType, final SelectDateListAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            cnt_users = itemView.findViewById(R.id.cnt_users);
            checkBox = itemView.findViewById(R.id.checkBox);
//            layout_card.setOnClickListener(this);
//            checkBox.setOnClickListener(this);
            if (viewType == TYPE_TIME) {

            }
        }

        public void bindToPost(final SelectDateData data, int viewType) {
            if (viewType == TYPE_TIME) {
                try {
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("M월 d일");
                    Date date = formatter1.parse(data.getTime());
                    String str_feed_time = formatter2.format(date);

                    txt_date.setText(str_feed_time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (data.getTime().contains("12:00:00") || data.getTime().contains("13:00:00") || data.getTime().contains("14:00:00") || data.getTime().contains("15:00:00"))
                    txt_time.setText("점심");
                else if (data.getTime().contains("17:00:00") || data.getTime().contains("18:00:00") || data.getTime().contains("19:00:00") || data.getTime().contains("20:00:00") || data.getTime().contains("21:00:00"))
                    txt_time.setText("저녁");
                else
                    txt_time.setText(data.getTime());

                try {
                    setDateToView(data, data.getPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (data.getCnt() > 0)
                    cnt_users.setText(String.format(mContext.getResources().getString(R.string.enable_cnt_feedee), String.valueOf(data.getCnt())));
//                else
//                    cnt_users.setText("-");

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SelectFeedFragment) fragment).feed_time = data.getTime();

                        if (((SelectFeedFragment) fragment).feed_time.equals("")) {
                            ((SelectFeedFragment) fragment).txt_explain_pick.setText("식사하고자 하는 시간을 선택하세요.");
                        } else if (((SelectFeedFragment) fragment).feed_rest_id.equals("")) {
                            ((SelectFeedFragment) fragment).txt_explain_pick.setText("가고 싶은 음식점을 선택해보세요.");
                        } else if (((SelectFeedFragment) fragment).to_id.equals("")) {
                            ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                        } else {
                            ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                        }

                        try {
                            Calendar calendar = Calendar.getInstance();
                            Date date_setting_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(listViewItemList.get(getAdapterPosition()).getTime());
                            calendar.setTime(date_setting_time);

                            Calendar curCal = Calendar.getInstance();
                            long time_setting = calendar.getTimeInMillis();
                            long time_current = curCal.getTimeInMillis();

                            if (time_setting > time_current) {
                                mSelectedItem = getAdapterPosition();
                                notifyItemRangeChanged(0, listViewItemList.size());
                                mAdapter.onItemHolderClick(ViewHolder.this);

                                new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
                                        ((SelectFeedFragment) fragment).area_cd,
                                        ((SelectFeedFragment) fragment).feed_rest_id,
                                        "readBelowRest");
                            } else {
                                checkBox.setVisibility(View.GONE);
                                Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                //TYPE_END
                layout_card.setBackgroundColor(Color.parseColor("#efefef"));
                txt_time.setText("시간선택");

                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PickDateDialog pickDateDialog = new PickDateDialog(mContext);
                        pickDateDialog.callFunction(((SelectFeedFragment) fragment).dateLikeList);
                    }
                });
            }
        }

        public void setDateToView(SelectDateData data, int position) throws Exception {
//            checkBox.setChecked(position == mSelectedItem);

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    ((SelectFeedFragment) fragment).feed_time = data.getTime();
                    if (((SelectFeedFragment) fragment).feed_time.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("식사하고자 하는 시간을 선택하세요.");
                    } else if (((SelectFeedFragment) fragment).feed_rest_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("가고 싶은 음식점을 선택해보세요.");
                    } else if (((SelectFeedFragment) fragment).to_id.equals("")) {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    } else {
                        ((SelectFeedFragment) fragment).txt_explain_pick.setText("");
                    }

                    data.setChecked(true);
                } else {
                    data.setChecked(false);

                }
            } else {//선택이 없으나, 사전에 체크 표기

                if (listViewItemList.size() == 1) {
                    data.setChecked(true);
                    ((SelectFeedFragment) fragment).feed_time = data.getTime();

                    new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
                            ((SelectFeedFragment) fragment).area_cd,
                            ((SelectFeedFragment) fragment).feed_rest_id,
                            "readBelowRest");
                    Log.e("abc", "Select date feed_time = " + ((SelectFeedFragment) fragment).feed_time);
                }
            }

            checkBox.setChecked(data.isChecked());
        }

//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
////                case R.id.layout_card:
////                    Log.e("abc","layout_card");
////
////
////                    break;
////                case R.id.checkBox:
////                    Log.e("abc","checkBox feed_time = " + ((SelectFeedFragment) fragment).feed_time);
////
////                    try {
////                        Calendar calendar = Calendar.getInstance();
////                        Date date_setting_time = new SimpleDateFormat("yyyyy-MM-dd HH:mm:ss").parse(listViewItemList.get(getAdapterPosition()).getTime());
////                        calendar.setTime(date_setting_time);
////
////                        Calendar curCal = Calendar.getInstance();
////                        long time_setting = calendar.getTimeInMillis();
////                        long time_current = curCal.getTimeInMillis();
////
////                        if (time_setting > time_current) {
////                            mSelectedItem = getAdapterPosition();
////                            notifyItemRangeChanged(0, listViewItemList.size());
////                            mAdapter.onItemHolderClick(ViewHolder.this);
////
////                            new SelectFeedListTask(mContext).execute(((SelectFeedFragment) fragment).feed_time,
////                                    ((SelectFeedFragment) fragment).area_cd,
////                                    ((SelectFeedFragment) fragment).feed_rest_id,
////                                    "readOnlyUser");
////                        } else {
////                            checkBox.setVisibility(View.GONE);
////                            Toast.makeText(mContext, R.string.cannot_reserve_past, Toast.LENGTH_SHORT).show();
////                        }
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////
////                    break;
//            }
//        }
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

    public void onItemHolderClick(SelectDateListAdapter.ViewHolder holder) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(null, holder.itemView, holder.getAdapterPosition(), holder.getItemId());
    }
}