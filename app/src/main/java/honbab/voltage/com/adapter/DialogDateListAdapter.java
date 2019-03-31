package honbab.voltage.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import honbab.voltage.com.data.SelectDateData;
import honbab.voltage.com.task.PickDateTask;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.widget.OkHttpClientSingleton;
import okhttp3.OkHttpClient;

public class DialogDateListAdapter extends RecyclerView.Adapter<DialogDateListAdapter.ViewHolder> {
    private Context mContext;
    private OkHttpClient httpClient;

//    int TYPE_DATE = 0;
//    int TYPE_TIME = 1;
//    int TYPE_END = 2;

    private Fragment fragment;
    Calendar curCal;
    Date curDate;
    Date d_time;

    public ArrayList<SelectDateData> listViewItemList = new ArrayList<>();

    public DialogDateListAdapter() {

    }

    public DialogDateListAdapter(Context mContext, ArrayList<SelectDateData> listViewItemList) {
        this.mContext = mContext;
        this.httpClient = OkHttpClientSingleton.getInstance().getHttpClient();
        this.listViewItemList = listViewItemList;

        fragment = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("page:0");

        curCal = Calendar.getInstance();
        curDate = curCal.getTime();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_dialog_pick_date, parent, false);

        return new ViewHolder(view, viewType);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        if (getItemViewType(position) == TYPE_TIME) {
//            Log.e("abc", "position = " + position + ", getItemViewType = " + getItemViewType(position));
        final SelectDateData data = listViewItemList.get(position);
        data.setPosition(position);

        holder.bindToPost(data, getItemViewType(position));
    }

//    @Override
//    public int getItemViewType(int position) {
////        Log.e("abc", position + ", getItemViewType listViewItemList = " + listViewItemList.size());
////        Log.e("abc", position + " == " + listViewItemList.size());
////        if (listViewItemList.size() == position)
////            return TYPE_END;
////        else
////            return TYPE_TIME;
//
////        return position % 2 == 0 ? VIEW_TYPE_TEXT : VIEW_TYPE_IMAGE;
//    }

    @Override
    public int getItemCount() {
        if (listViewItemList.size() > 10)
            return 10;

        return listViewItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView layout_card;
        public CheckBox checkBox;
        public TextView txt_date, txt_time, cnt_users;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            layout_card = itemView.findViewById(R.id.layout_card);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);
            cnt_users = itemView.findViewById(R.id.cnt_users);
//            cnt_users.setVisibility(View.GONE);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void bindToPost(final SelectDateData data, int viewType) {
            try {
                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                SimpleDateFormat formatter2 = new SimpleDateFormat("M월 d일");
//                Date date = formatter1.parse(data.getTime());
                d_time = formatter1.parse(data.getTime());
                String str_feed_time = formatter2.format(d_time);

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

            if (data.getCnt() > 0)
                cnt_users.setText(String.format(mContext.getResources().getString(R.string.enable_cnt_feedee), String.valueOf(data.getCnt())));

            if(d_time.before(curDate)) {
                checkBox.setEnabled(false);
//                checkBox.setChecked(true);
                layout_card.setBackgroundResource(R.drawable.border_round_drgr9);
                layout_card.setVisibility(View.INVISIBLE);
            }


            if (data.getStatus().equals("y")) {
                checkBox.setChecked(true);
//                cnt_users.setText("예약완료");
            } else if (data.getStatus().equals("p")) {
                checkBox.setEnabled(false);
                checkBox.setChecked(true);
                cnt_users.setText("수락대기중");
            } else if (data.getStatus().equals("a")) {
                checkBox.setEnabled(false);
                checkBox.setChecked(true);
                cnt_users.setText("예약완료");
            } else {
                //n
                checkBox.setChecked(false);
            }

            layout_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getStatus().equals("n")) {
                        // n -> y
                        checkBox.setChecked(true);
                        data.setStatus("y");

                        new PickDateTask(mContext).execute(data.getTime());
                    } else if (data.getStatus().equals("y")) {
                        // y -> n
                        checkBox.setChecked(false);
                        data.setStatus("n");

                        new PickDateTask(mContext).execute(data.getTime());
                    } else {
                        //a
                        Toast.makeText(mContext, "이미 진행한 일정입니다.", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("abc", "checkBox = " +data.getStatus());
                    if (data.getStatus().equals("n")) {
                        checkBox.setChecked(true);
                        data.setStatus("y");

                        new PickDateTask(mContext).execute(data.getTime());
                    } else if (data.getStatus().equals("y")) {
                        checkBox.setChecked(false);
                        data.setStatus("n");

                        new PickDateTask(mContext).execute(data.getTime());
                    } else {
                        //a
                        Toast.makeText(mContext, "이미 진행한 일정입니다.", Toast.LENGTH_SHORT).show();
                    }
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