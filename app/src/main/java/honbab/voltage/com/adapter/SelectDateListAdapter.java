package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import honbab.voltage.com.tete.LoginActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
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
//        public TextView txt_date, txt_time, cnt_users;
        public TextView txt_day_of_week, txt_time, cnt_users;

        public ViewHolder(View itemView, int viewType, final SelectDateListAdapter mAdapter) {
            super(itemView);
            this.mAdapter = mAdapter;

            layout_card = itemView.findViewById(R.id.layout_card);
            txt_day_of_week = itemView.findViewById(R.id.txt_day_of_week);
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
                txt_day_of_week.setText(data.getDay_of_week());

                try {
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("d일");
                    Date date = formatter1.parse(data.getTime());
                    String str_feed_time = formatter2.format(date);

                    txt_time.setText(str_feed_time + " " + data.getTimeName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

//                if (data.getTime().contains("12:00:00") || data.getTime().contains("13:00:00") || data.getTime().contains("14:00:00") || data.getTime().contains("15:00:00"))
//                    txt_time.setText("점심");
//                else if (data.getTime().contains("17:00:00") || data.getTime().contains("18:00:00") || data.getTime().contains("19:00:00") || data.getTime().contains("20:00:00") || data.getTime().contains("21:00:00"))
//                    txt_time.setText("저녁");
//                else
//                    txt_time.setText(data.getTime());

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
//                                        ((SelectFeedFragment) fragment).area_cd,
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
                        if (Integer.parseInt(Statics.my_id) < 1) {
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        } else {
                            PickDateDialog pickDateDialog = new PickDateDialog(mContext);
                            pickDateDialog.callFunction(((SelectFeedFragment) fragment).dateAllList);
                        }
                    }
                });
            }
        }

        public void setDateToView(SelectDateData data, int position) throws Exception {
//            checkBox.setChecked(position == mSelectedItem);
            Log.e("abc", position + ", mSelectedItem = " + mSelectedItem);

            if (mSelectedItem >= 0) {//선택이 되었으면
                if (position == mSelectedItem) {
                    ((SelectFeedFragment) fragment).timelike_id = data.getTimelike_id();
                    ((SelectFeedFragment) fragment).feed_time = data.getTime();
//                    ((SelectFeedFragment) fragment).areaList = data.getAreasList();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("M/d E요일");
                    Date date = formatter1.parse(data.getTime());
                    String str_feed_time = formatter2.format(date);
                    ((SelectFeedFragment) fragment).txt_explain_time.setText(str_feed_time + " , ");

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

//                    Log.e("abc", position + ", data.getAreasList() = " + data.getAreasList());
//                    ((SelectFeedFragment) fragment).mAdapter_area.clearItemList();
//                    ((SelectFeedFragment) fragment).mAdapter_area = new SelectAreaListAdapter(mContext, data.getAreasList());
//                    ((SelectFeedFragment) fragment).recyclerView_area.setAdapter(((SelectFeedFragment) fragment).mAdapter_area);
//                    ((SelectFeedFragment) fragment).mAdapter_area.notifyDataSetChanged();
                    Log.e("abc", position + ", data.getRestList() = " + data.getRestList().size());
//                    ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                    ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, data.getRestList());
                    ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
                    ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();
                } else {
                    data.setChecked(false);
                }
            } else {//선택이 없으나, 사전에 체크 표기

                if (listViewItemList.size() == 1) {
                    ((SelectFeedFragment) fragment).timelike_id = data.getTimelike_id();
                    ((SelectFeedFragment) fragment).feed_time = data.getTime();
//                    ((SelectFeedFragment) fragment).areaList = data.getAreasList();
                    SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    SimpleDateFormat formatter2 = new SimpleDateFormat("M/d E요일");
                    Date date = formatter1.parse(data.getTime());
                    String str_feed_time = formatter2.format(date);
                    ((SelectFeedFragment) fragment).txt_explain_time.setText(str_feed_time + " , ");

                    data.setChecked(true);

                    ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                    ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, data.getRestList());
                    ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
                    ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();
                } else {
//                    if (listViewItemList.size() == position + 1) {
                    if (position ==  0) {
                        ((SelectFeedFragment) fragment).timelike_id = data.getTimelike_id();
                        ((SelectFeedFragment) fragment).feed_time = data.getTime();
//                        ((SelectFeedFragment) fragment).restLikeList = data.getRestList();
                        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        SimpleDateFormat formatter2 = new SimpleDateFormat("M/d E요일");
                        Date date = formatter1.parse(data.getTime());
                        String str_feed_time = formatter2.format(date);
                        ((SelectFeedFragment) fragment).txt_explain_time.setText(str_feed_time + " , ");

                        data.setChecked(true);

//                        ((SelectFeedFragment) fragment).mAdapter_area.clearItemList();
//                        ((SelectFeedFragment) fragment).mAdapter_area = new SelectAreaListAdapter(mContext, data.getAreasList());
//                        ((SelectFeedFragment) fragment).recyclerView_area.setAdapter(((SelectFeedFragment) fragment).mAdapter_area);
                        ((SelectFeedFragment) fragment).mAdapter_rest.clearItemList();
                        ((SelectFeedFragment) fragment).mAdapter_rest = new SelectRestListAdapter(mContext, data.getRestList());
                        ((SelectFeedFragment) fragment).recyclerView_rest.setAdapter(((SelectFeedFragment) fragment).mAdapter_rest);
                        ((SelectFeedFragment) fragment).mAdapter_rest.notifyDataSetChanged();
                    }
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